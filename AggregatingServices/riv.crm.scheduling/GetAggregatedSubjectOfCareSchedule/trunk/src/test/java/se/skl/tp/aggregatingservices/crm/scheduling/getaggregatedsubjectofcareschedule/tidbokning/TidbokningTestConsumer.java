package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.Holder;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getaggregatedsubjectofcareschedule.v1.rivtabp21.GetAggregatedSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getaggregatedsubjectofcareschedule.v1.rivtabp21.GetAggregatedSubjectOfCareScheduleResponderService;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.riv.interoperability.headers.v1.ActorTypeEnum;
import se.riv.interoperability.headers.v1.ProcessingStatusType;

public class TidbokningTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(TidbokningTestConsumer.class);

	private GetAggregatedSubjectOfCareScheduleResponderInterface _service = null;

	public static void main(String[] args) {
		String serviceAddress = getAddress("TIDBOKNING_INBOUND_URL");
		String personnummer = "1234567890";

		TidbokningTestConsumer consumer = new TidbokningTestConsumer(serviceAddress);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size());
	}
	
	public TidbokningTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(GetAggregatedSubjectOfCareScheduleResponderInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		//Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = TidbokningTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}
		
		_service  = (GetAggregatedSubjectOfCareScheduleResponderInterface) proxyFactory.create(); 

		
		
		
//		try {
//            URL url =  new URL(serviceAddress + "?wsdl");
//            _service = new GetAggregatedSubjectOfCareScheduleResponderService(url).getGetAggregatedSubjectOfCareScheduleResponderPort();
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
//        }
	}
	
	public void callService(String logicalAddress, String id, Holder<ProcessingStatusType> processingStatusHolder, Holder<GetSubjectOfCareScheduleResponseType> responseHolder) {
		
		log.debug("Calling GetSubjectOfCareSchedule-soap-service with id = {}", id);
		
		ActorType actor = new ActorType();
		actor.setActorId(id);
		actor.setActorType(ActorTypeEnum.SUBJECT_OF_CARE);
		
		GetSubjectOfCareScheduleType request = new GetSubjectOfCareScheduleType();
		request.setSubjectOfCare(id);
		
		_service.getAggregatedSubjectOfCareSchedule(logicalAddress, actor, request, processingStatusHolder, responseHolder);
	}
}