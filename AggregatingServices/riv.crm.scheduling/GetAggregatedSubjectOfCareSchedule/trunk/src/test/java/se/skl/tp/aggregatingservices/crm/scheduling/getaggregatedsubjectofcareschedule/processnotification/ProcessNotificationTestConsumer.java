package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.processnotification;

import static se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.GetAggregatedSubjectOfCareScheduleMuleServer.getAddress;

import java.net.URL;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import org.soitoolkit.refapps.sd.sample.schema.v1.Sample;
import org.soitoolkit.refapps.sd.sample.schema.v1.SampleResponse;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.SampleInterface;

import riv.itintegration.engagementindex._1.EngagementTransactionType;
import riv.itintegration.engagementindex._1.EngagementType;
import se.riv.itintegration.engagementindex.processnotification.v1.rivtabp21.ProcessNotificationResponderInterface;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationResponseType;
import se.riv.itintegration.engagementindex.processnotificationresponder.v1.ProcessNotificationType;

public class ProcessNotificationTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(ProcessNotificationTestConsumer.class);

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedSubjectOfCareSchedule-config");

	private ProcessNotificationResponderInterface _service = null;
	    
    public ProcessNotificationTestConsumer(String serviceAddress) {
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(ProcessNotificationResponderInterface.class);
		proxyFactory.setAddress(serviceAddress);
		
		//Used for HTTPS
		SpringBusFactory bf = new SpringBusFactory();
		URL cxfConfig = ProcessNotificationTestConsumer.class.getClassLoader().getResource("cxf-test-consumer-config.xml");
		if (cxfConfig != null) {
			proxyFactory.setBus(bf.createBus(cxfConfig));
		}
		
		_service  = (ProcessNotificationResponderInterface) proxyFactory.create(); 
    }

    public static void main(String[] args) throws Fault {
            String serviceAddress = getAddress("PROCESS-NOTIFICATION_INBOUND_URL");
            String logicalAddress = "HSA-1";
            String personnummer = "1234567890";

            ProcessNotificationTestConsumer consumer = new ProcessNotificationTestConsumer(serviceAddress);
            ProcessNotificationResponseType response = consumer.callService(logicalAddress, personnummer);
            log.info("Returned value = " + response.getResultCode());
    }

    public ProcessNotificationResponseType callService(String logicalAddress, String id) throws Fault {
            log.debug("Calling sample-soap-service with id = {}", id);
            ProcessNotificationType request = new ProcessNotificationType();
            EngagementTransactionType transaction = new EngagementTransactionType();
            EngagementType e = new EngagementType();
            e.setRegisteredResidentIdentification(id);
			transaction.setEngagement(e );
			request.getEngagementTransaction().add(transaction );
            return _service.processNotification(logicalAddress, request);
    }	
}