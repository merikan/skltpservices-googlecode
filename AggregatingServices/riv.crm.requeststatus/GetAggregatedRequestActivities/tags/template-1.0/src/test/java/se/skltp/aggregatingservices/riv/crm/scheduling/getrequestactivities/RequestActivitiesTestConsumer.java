package se.skltp.aggregatingservices.riv.crm.scheduling.getrequestactivities;

import static se.skltp.aggregatingservices.riv.crm.scheduling.getrequestactivities.RequestActivitiesTestProducer.TEST_ID_ONE_BOOKING;

import java.net.URL;

import javax.xml.ws.Holder;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.rivtabp21.GetSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.riv.interoperability.headers.v1.ActorTypeEnum;
import se.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.cache.SoapHeaderCxfInterceptor;

public class RequestActivitiesTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(RequestActivitiesTestConsumer.class);

	private GetSubjectOfCareScheduleResponderInterface _service = null;
	
	public static void main(String[] args) {
		String serviceAddress = RequestActivitiesMuleServer.getAddress("SERVICE_INBOUND_URL");
		String personnummer = TEST_ID_ONE_BOOKING;

		RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(serviceAddress);
		Holder<GetSubjectOfCareScheduleResponseType> responseHolder = new Holder<GetSubjectOfCareScheduleResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
		long now = System.currentTimeMillis();
		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size() + " in " + (System.currentTimeMillis() - now) + " ms.");

		now = System.currentTimeMillis();
		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size() + " in " + (System.currentTimeMillis() - now) + " ms.");
	
		now = System.currentTimeMillis();
		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size() + " in " + (System.currentTimeMillis() - now) + " ms.");
	
		now = System.currentTimeMillis();
		consumer.callService("logical-adress", personnummer, processingStatusHolder, responseHolder);
		log.info("Returned #timeslots = " + responseHolder.value.getTimeslotDetail().size() + " in " + (System.currentTimeMillis() - now) + " ms.");
	
	}
	
	public RequestActivitiesTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(GetSubjectOfCareScheduleResponderInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		// Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = RequestActivitiesTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}

		_service  = (GetSubjectOfCareScheduleResponderInterface) proxyFactory.create(); 

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

		GetSubjectOfCareScheduleResponseType response = _service.getSubjectOfCareSchedule(logicalAddress, actor, request);
		responseHolder.value = response;
		
		processingStatusHolder.value = SoapHeaderCxfInterceptor.getLastFoundProcessingStatus();
	}
}