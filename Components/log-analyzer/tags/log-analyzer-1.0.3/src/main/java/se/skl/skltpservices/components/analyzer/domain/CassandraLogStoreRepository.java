/**
 * Copyright (c) 2012, Sjukvardsradgivningen. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package se.skl.skltpservices.components.analyzer.domain;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.xml.datatype.XMLGregorianCalendar;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.logentry.schema.v1.LogEntryType;
import org.soitoolkit.commons.logentry.schema.v1.LogEntryType.ExtraInfo;
import org.soitoolkit.commons.logentry.schema.v1.LogEvent;
import org.soitoolkit.commons.logentry.schema.v1.LogLevelType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import se.skl.skltpservices.components.analyzer.Real;


/**
 * Stores events in an Apache Cassandra database. <p>
 * 
 * This implementation also performs scheduled house-keeping jobs, see {@link #clean()}
 * 
 * @author Peter
 *
 */
@Real @Component
public class CassandraLogStoreRepository implements LogStoreRepository {

	private static final String CONTRACT_PREFIX = "c-";

	private static final String DOMAIN_PREFIX = "d-";

	private static final Logger log = LoggerFactory.getLogger(CassandraLogStoreRepository.class);

	static final String CF_EVENT = "Event";
	static final String CF_WEEKLY_STATS = "WeeklyStats";
	static final String CF_EVENT_TIMELINE = "EventTimeLine";
	static final String CF_META_DATA = "MetaData";

	static final String KEYSPACE = "Log";
	static final String WAYPOINT_IN = "in.";
	static final String WAYPOINT_OUT = "out.";
	static final String WAYPOINT_ERROR = "err.";
	
	//
	static final String SENDER = "sender";
	static final String RECEIVER = "receiver";
	static final String CONTRACT = "contract";
	static final String XREQ_IN = "xreq-in";



	private static Map<String, String> columnNames = new HashMap<String, String>();
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		// info {key, actual column name}
		// once
		columnNames.put("in.cxf_service", CONTRACT);
		columnNames.put("in.senderid", SENDER);
		columnNames.put("in.receiverid", RECEIVER);

		// in
		columnNames.put("in.timestamp", "in_timestamp");
		columnNames.put("in.payload", "in_payload");
		columnNames.put("in.rivversion", "in_riv_version");

		// out
		columnNames.put("out.timestamp", "out_timestamp");
		columnNames.put("out.payload", "out_payload");
		columnNames.put("out.rivversion", "out_riv_version");
		columnNames.put("out.endpoint_time", "endpoint_time");
		columnNames.put("out.endpoint_url", "endpoint_url");
		columnNames.put("out.time.producer", "endpoint_time");

		// errors
		columnNames.put("err.cxf_service", CONTRACT);
		columnNames.put("err.senderid", SENDER);
		columnNames.put("err.receiverid", RECEIVER);
		columnNames.put("err.timestamp", "err_timestamp");
		columnNames.put("err.payload", "err_payload");
		columnNames.put("err.message", "err_message");
		columnNames.put("err.sessionStatus", "err_flag");
		columnNames.put("err.sessionErrorDescription", "err_description");
		columnNames.put("err.sessionErrorTechnicalDescription", "err_detail");
	}

	// config
	@Value("${log.store.instances}")
	private String logStoreInstances;

	@Value("${log.store.payloadTTL}")
	private int payloadTTL;

	@Value("${log.store.timeout}")
	private int timeout;

	@Value("${log.store.metaTTL}")
	private int metaTTL;

	@Value("${log.store.weeklyCounterTTL}")
	private int weeklyCounterTTL;

	@Value("${log.store.clusterName}")
	private String clusterName;

	@Value("${log.store.counterDomains}")
	private String counterDomains;

	private Map<String, List<String>> domainMap = new HashMap<String, List<String>>();

	private Set<String> contracts;
	private Set<String> senders;
	private Set<String> receivers;
	
	// hector stuff
	private Keyspace keyspace;
	final static StringSerializer SS = StringSerializer.get();
	final static LongSerializer LS = LongSerializer.get();
	final static CompositeSerializer CS = CompositeSerializer.get();
	final static ByteBufferSerializer BS = ByteBufferSerializer.get();
	final static UUIDSerializer TS = UUIDSerializer.get();
	
	//
	public CassandraLogStoreRepository() {
	}
	
	// lazy connection.
	Keyspace getKeySpace() {
		if (this.keyspace == null) {
			synchronized (this) {
				if (this.keyspace == null) {
					CassandraHostConfigurator cfg = new CassandraHostConfigurator(logStoreInstances);
					cfg.setMaxActive(5);
					cfg.setCassandraThriftSocketTimeout(timeout);
					cfg.setMaxWaitTimeWhenExhausted(10000);
					cfg.setRetryDownedHosts(true);
					Cluster cluster = HFactory.getOrCreateCluster(clusterName, cfg);
					this.keyspace = HFactory.createKeyspace(KEYSPACE, cluster);
				}
			}
		}
		return this.keyspace;
	}

	@PostConstruct
	public void init() {		
		contracts = getMetaData(CONTRACT);
		receivers = getMetaData(RECEIVER);
		senders = getMetaData(SENDER);
	}

	/**
	 * Returns TTL i seconds.
	 * 
	 * @param days TTL in days.
	 * @return TTL in seconds.
	 */
	private static int toSeconds(int days) {
		return (days * 24 * 3600);
	}

	@Override
	public void storeEvent(LogEvent infoEvent) {
		LogEntryType entry = infoEvent.getLogEntry();

		String waypoint = entry.getMessageInfo().getMessage();
		LogLevelType level = infoEvent.getLogEntry().getMessageInfo().getLevel();
		if (log.isDebugEnabled()) {
			String key = entry.getRuntimeInfo().getBusinessCorrelationId();
			log.debug("Store event { key: {}, waypoint: {}, level: {} }", new Object[] { key, waypoint, level });
		}
		boolean error = LogLevelType.ERROR.equals(level);
		
		if (XREQ_IN.equals(waypoint)) {
			waypoint = WAYPOINT_IN;
		} else if (error) {
			waypoint = WAYPOINT_ERROR;
		} else {
			waypoint = WAYPOINT_OUT;			
		}

		storeEvent(entry, waypoint, error);
	}

	//
	protected Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		return calendar;
	}

	//
	protected void storeEvent(LogEntryType entry, String waypoint, boolean error) {
		Mutator<String> m = HFactory.createMutator(getKeySpace(), SS);
		String key = entry.getRuntimeInfo().getBusinessCorrelationId();

		if (key == null || key.length() == 0) {
			key = java.util.UUID.randomUUID().toString();
			log.warn("Missing business correlationId for event, new  key { uuid: {} } created.", key);
		}

		Calendar calendar = getCalendar();

		long timestamp = toTimestamp(entry.getRuntimeInfo().getTimestamp());
		int ttl = toSeconds(metaTTL);
		
		ReverseEvent reverseEvent = null;
		if (WAYPOINT_IN.equals(waypoint) || error) {
			m.addInsertion(key, CF_EVENT, toStringColumn("date", toDate(calendar), ttl));
			reverseEvent = new ReverseEvent(key, entry.getPayload(), error, timestamp);
		}

		m.addInsertion(key, CF_EVENT,  toLongColumn(columnNames.get(waypoint + "timestamp"), timestamp, ttl));        

		if (error) {
			add(m, CF_EVENT, key, toStringColumn(columnNames.get(waypoint + "message"), entry.getMessageInfo().getMessage(), ttl));
		}

		for (ExtraInfo info : entry.getExtraInfo()) {
			if (log.isDebugEnabled()) {
				log.debug("ExtraInfo { name: {}, value: {} }", info.getName(), info.getValue());
			}
			String name = columnNames.get(waypoint + info.getName());
			if (name != null) {
				if (CONTRACT.equals(name)) {
					updateStatistics(m, info.getValue(), calendar, error);
				}
				if (reverseEvent != null) {
					reverseEvent.add(name, info.getValue());
				}
				add(m, CF_EVENT, key, toStringColumn(name, info.getValue(), ttl));
			}
		}
		add(m, CF_EVENT, key, toStringColumn(columnNames.get(waypoint + "payload"), entry.getPayload(), toSeconds(payloadTTL)));
		
		if (reverseEvent != null) {
			updateReverseIndex(reverseEvent, ttl);
			updateMetaData(m, SENDER, reverseEvent.getSender(), senders);
			updateMetaData(m, RECEIVER, reverseEvent.getReceiver(), receivers);
		}
		
		m.execute();
	}
	
	/** 
	 * Updates reverse index.
	 * 
	 * @param reverseEvent the reverse event.
	 */
	protected void updateReverseIndex(ReverseEvent reverseEvent, int ttl) {
		Mutator<Composite> mr = HFactory.createMutator(getKeySpace(), CS);
		mr.addInsertion(reverseEvent.key(), CF_EVENT_TIMELINE, toTimeUUIDColumn(reverseEvent.getTimeUUID(), reverseEvent.value(), ttl));		
		mr.execute();
	}
	
	//
	protected void updateMetaData(Mutator<String> ms, String key, String name, Set<String> set) {
		if (name.length() == 0) {
			return;
		}
		synchronized (set) {				
			if (!set.contains(name)) {
				set.add(name);
				ms.addInsertion(key, CF_META_DATA, toStringColumn(name, toDate(Calendar.getInstance()), -1));
			}
		}
	}

	//
	private static String toDate(Calendar calendar) {
		return String.format("%tF", calendar.getTime());
	}

	//
	private void updateStatistics(Mutator<String> m, String contract, Calendar calendar, boolean error) {
		if (contract == null) {
			contract = "unknown-contract";
		}
		String week = String.format("Week%d%s", calendar.get(Calendar.WEEK_OF_YEAR),  (error) ? "_Error" : "");
		HCounterColumn<String> column = HFactory.createCounterColumn(week, 1L, SS);
		column.setTtl(weeklyCounterTTL);
		m.addCounter(CONTRACT_PREFIX + contract, CF_WEEKLY_STATS, column);
		for (String domain : getDomains(contract)) {
			column = HFactory.createCounterColumn(week, 1L, SS);
			column.setTtl(weeklyCounterTTL);
			m.addCounter(DOMAIN_PREFIX + domain, CF_WEEKLY_STATS, column);
		}
		if (!contracts.contains(contract)) {
			m.addInsertion(CONTRACT, CF_META_DATA, toStringColumn(contract, toDate(Calendar.getInstance()), -1));
			contracts.add(contract);
		}
	}

	/**
	 * Returns domains for a contract.
	 * 
	 * @param contract the contract.
	 * @return the list of domains, empty if none exists.
	 */
	private List<String> getDomains(String contract) {
		List<String> list = domainMap.get(contract);
		if (list == null) {
			list = new LinkedList<String>();
			domainMap.put(contract, list);
			for (String domain : counterDomains.split(",")) {
				domain = domain.trim();
				if (contract.startsWith(domain)) {
					list.add(domain);
				}
			}
			if (list.isEmpty()) {
				list.add("others");
			}
		}
		return list;
	}

	//
	static HColumn<UUID, Composite> toTimeUUIDColumn(TimeUUID uuid, Composite value, int ttl) {
		HColumn<UUID, Composite> column = HFactory.createColumn(uuid.getUUID(), value, TS, CS);
		if (ttl > 0) {
			column.setTtl(ttl);
		}
		return column;
	}


	//
	static HColumn<String, String> toStringColumn(String name, String value, int ttl) {
		if (value == null || value.length() == 0) {
			return null;
		}
		HColumn<String, String> column = HFactory.createColumn(name, value, SS, SS);
		if (ttl > 0) {
			column.setTtl(ttl);
		}
		return column;
	}

	//
	static HColumn<String, Long> toLongColumn(String name, long value, int ttl) {
		HColumn<String, Long> column = HFactory.createColumn(name, value, SS, LS);
		if (ttl > 0) {
			column.setTtl(ttl);
		}
		return column;
	}

	//
	static void add(Mutator<String> mutator, String columnFamily, String key, HColumn<String, String> column) {
		if (column != null) {
			mutator.addInsertion(key, columnFamily, column);
		}
	}

	//
	static long toTimestamp(final XMLGregorianCalendar timestamp) {	
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, timestamp.getHour());
		c.set(Calendar.MINUTE, timestamp.getMinute());
		c.set(Calendar.SECOND, timestamp.getSecond());
		c.set(Calendar.MILLISECOND, timestamp.getMillisecond());
		c.set(Calendar.YEAR, timestamp.getYear());
		c.set(Calendar.MONTH, timestamp.getMonth() - 1);
		c.set(Calendar.DAY_OF_MONTH, timestamp.getDay());
		return c.getTime().getTime();
	}

	
	//
	@Override
	public Set<String> getSenders() {
		return senders;
	}
	
	//
	@Override
	public Set<String> getReceivers() {
		return receivers;
	}
	
	//
	@Override
	public Set<String> getContracts() {
		return contracts;
	}

	//
	@Override
	public List<Counter> getDomainCounters(int week) {
		List<String> domains = new ArrayList<String>(50);
		for (String domain : counterDomains.split(",")) {
			domains.add(domain.trim());
		}
		return getCounters(DOMAIN_PREFIX, domains, week);
	}

	//
	@Override
	public List<Counter> getContractCounters(int week) {
		return getCounters(CONTRACT_PREFIX, contracts, week);
	}

	//
	protected List<Counter> getCounters(String prefix, Collection<String> names, int week) {
		log.info("Get counters: " + week);
		CounterQuery<String, String> query = HFactory.createCounterColumnQuery(getKeySpace(), SS, SS);
		query.setColumnFamily(CF_WEEKLY_STATS);
		
		String w = String.format("Week%d", week);
		String e = String.format("Week%d_Error", week);

		List<Counter> list = new LinkedList<Counter>();

		for (String name : names) {
			query.setKey(prefix + name);
			query.setName(w);
			HCounterColumn<String> tot = query.execute().get();
			query.setName(e);
			HCounterColumn<String> err = query.execute().get();
			
			if (tot != null || err != null) {
				Counter counter = new Counter();
				counter.setWeek(week);
				counter.setName(name);
				
				if (tot != null)  {
					counter.setTotal(tot.getValue());
				}
				
				if (err != null) {
					counter.setError(err.getValue());
				}
				
				list.add(counter);			
			}
		}
		
		return list;
	}
	
	//
	protected Set<String> getMetaData(String key) {
		SliceQuery<String, String, String> query = HFactory.createSliceQuery(getKeySpace(), SS, SS, SS);
		query.setColumnFamily(CF_META_DATA).setKey(key);
		ColumnIterator<String, String> iter = new ColumnIterator<String, String>(query);
		Set<String> set = new HashSet<String>();
		while (iter.hasNext()) {
			set.add(iter.next().getName());
		}
		return set;
	}
	
	

	@Override
	public List<EventSummary> getTimeLine(String contract, String error,
			String sender, String receiver, long time) {
		
		Composite key = new Composite();
		key.addComponent(contract, SS);
		key.addComponent(error, SS);
		key.addComponent(sender, SS);
		key.addComponent(receiver, SS);
		
		log.info("getTimeLine: {}, start: {}", key, new Date(time));
		
		SliceQuery<Composite, UUID, Composite> query = HFactory.createSliceQuery(getKeySpace(), CS, TS, CS);
		query.setColumnFamily(CF_EVENT_TIMELINE).setKey(key);
		UUID start = (time <= 0) ? null : new TimeUUID(time, true).getUUID();
		ColumnIterator<UUID, Composite> iter = new ColumnIterator<UUID, Composite>(query, start);
		List<EventSummary> list = new LinkedList<EventSummary>();
		int max = 100;
		while (iter.hasNext()) {
			HColumn<UUID, Composite> col = iter.next();
			Composite value = col.getValue();
			TimeUUID tuuid = new TimeUUID(col.getName());
			EventSummary es = new EventSummary();		
			es.setKey((String) value.getComponent(0).getValue(SS));
			es.setError("y".equals(error));
			es.setTimestamp(tuuid.getTimestamp());
			es.setPayload((String) value.getComponent(1).getValue(SS));
			list.add(es);
			if (max <= 0) {
				break;
			}
			max--;
		}
		log.info("getTimeLine: items #{}", list.size());

		return list;
	}
	

	@Override
	public Map<String, String> getEventProperties(String id) {
		Map<String, String> map = new HashMap<String, String>();
		SliceQuery<String, String, ByteBuffer> query = HFactory.createSliceQuery(getKeySpace(), CassandraLogStoreRepository.SS, CassandraLogStoreRepository.SS, CassandraLogStoreRepository.BS);
		query.setColumnFamily(CassandraLogStoreRepository.CF_EVENT).setKey(id);
		ColumnIterator<String, ByteBuffer> iter = new ColumnIterator<String, ByteBuffer>(query);
		while (iter.hasNext()) {
			HColumn<String, ByteBuffer> col = iter.next();	
			String name = col.getName();
			String value = (name.endsWith("timestamp")) ? formatter.format(new Date(LS.fromByteBuffer(col.getValue()))) : SS.fromByteBuffer(col.getValue());
			map.put(name, value);
		}
		
		return map;
	}

}
