package se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_1;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_2;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_BOOKING_ID_MANY_BOOKINGS_3;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_BOOKING_ID_ONE_BOOKING;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_ID_FAULT_INVALID_ID;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_ID_MANY_BOOKINGS;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_ID_ONE_BOOKING;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_ID_ZERO_BOOKINGS;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_LOGICAL_ADDRESS_1;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_LOGICAL_ADDRESS_1_RESPONSE_TIME;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_LOGICAL_ADDRESS_2;
import static se.skltp.aggregatingservices.riv.crm.requeststatus.getrequestactivities.RequestActivitiesTestProducer.TEST_LOGICAL_ADDRESS_3;
import static se.skltp.agp.riv.interoperability.headers.v1.CausingAgentEnum.VIRTUALIZATION_PLATFORM;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_CACHE;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.NO_DATA_SYNCH_FAILED;

import java.util.List;

import javax.xml.ws.Holder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.AbstractJmsTestUtil;
import org.soitoolkit.commons.mule.test.ActiveMqJmsTestUtil;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;

import se.riv.crm.requeststatus.getrequestactivitiesresponder.v1.GetRequestActivitiesResponseType;
import se.riv.crm.requeststatus.v1.RequestActivityType;
import se.skltp.agp.riv.interoperability.headers.v1.CausingAgentEnum;
import se.skltp.agp.riv.interoperability.headers.v1.LastUnsuccessfulSynchErrorType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;


public class RequestActivitiesIntegrationTest extends AbstractTestCase {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RequestActivitiesIntegrationTest.class);
	 
	private static final String LOGICAL_ADDRESS = "logical-address";
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
	private static final String EXPECTED_ERR_INVALID_ID_MSG = "Invalid Id: -1";
	private static final String DEFAULT_SERVICE_ADDRESS = RequestActivitiesMuleServer.getAddress("SERVICE_INBOUND_URL");
  
	private static final String ERROR_LOG_QUEUE = "SOITOOLKIT.LOG.ERROR";
	private AbstractJmsTestUtil jmsUtil = null;

    public RequestActivitiesIntegrationTest() {
	    // Only start up Mule once to make the tests run faster...
	    // Set to false if tests interfere with each other when Mule is started only once.
	    setDisposeContextPerClass(true);
    }

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
  
		"GetAggregatedRequestActivities-common.xml," +
		"aggregating-services-common.xml," + 
        "aggregating-service.xml," +
		"teststub-services/engagemangsindex-teststub-service.xml," + 
		"teststub-services/service-producer-teststub-service.xml";
    }

    @Override
	protected void doSetUp() throws Exception {
		super.doSetUp();

//		TODO: Mule EE dependency
//		CacheMemoryStoreImpl<MuleEvent> cache = getCache(muleContext);
//		cache.reset();

		doSetUpJms();
  
     }

	private void doSetUpJms() {
		// TODO: Fix lazy init of JMS connection et al so that we can create jmsutil in the declaration
		// (The embedded ActiveMQ queue manager is not yet started by Mule when jmsutil is delcared...)
		if (jmsUtil == null) jmsUtil = new ActiveMqJmsTestUtil();
		
 
		// Clear queues used for error handling
		jmsUtil.clearQueues(ERROR_LOG_QUEUE);
    }


    @Test
    public void test_ok_zero_bookings() {
    	do_test_ok_zero_bookings(TEST_ID_ZERO_BOOKINGS);		
    }

    @Test
    public void test_ok_one_booking() {
    	String id = TEST_ID_ONE_BOOKING;
    	String expectedBookingId = TEST_BOOKING_ID_ONE_BOOKING;
		String expectedLogicalAddress = TEST_LOGICAL_ADDRESS_1;
    	
    	ProcessingStatusType statusList = do_test_ok_one_booking(id, expectedBookingId, expectedLogicalAddress);
		
		assertProcessingStatusDataFromSource(statusList.getProcessingStatusList().get(0), expectedLogicalAddress);
    }

	private ProcessingStatusType do_test_ok_zero_bookings(String id) {
		RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetRequestActivitiesResponseType> responseHolder = new Holder<GetRequestActivitiesResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	GetRequestActivitiesResponseType response = responseHolder.value;
		assertEquals(0, response.getRequestActivity().size());
		
		ProcessingStatusType statusList = processingStatusHolder.value;
		assertEquals(0, statusList.getProcessingStatusList().size());
		return statusList;
	}

	private ProcessingStatusType do_test_ok_one_booking(String id,
			String expectedBookingId, String expectedLogicalAddress) {
		RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetRequestActivitiesResponseType> responseHolder = new Holder<GetRequestActivitiesResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	GetRequestActivitiesResponseType response = responseHolder.value;
		assertEquals(1, response.getRequestActivity().size());
		
		RequestActivityType responseElement = response.getRequestActivity().get(0);
		assertEquals(id, responseElement.getSubjectOfCareId());		
		assertEquals(expectedBookingId, responseElement.getSenderRequestId());		
		assertEquals(expectedLogicalAddress, responseElement.getCareUnit());		

		ProcessingStatusType statusList = processingStatusHolder.value;
		assertEquals(1, statusList.getProcessingStatusList().size());
		return statusList;
	}

    @Test
    public void test_ok_many_bookings_with_partial_timeout() {
    	String id = TEST_ID_MANY_BOOKINGS;
    	RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetRequestActivitiesResponseType> responseHolder = new Holder<GetRequestActivitiesResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);

    	// Verify the response, expect one booking from source #1, two from source #2 and a timeout from source #3
    	GetRequestActivitiesResponseType response = responseHolder.value;
		assertEquals(3, response.getRequestActivity().size());
		
		RequestActivityType responseElement = response.getRequestActivity().get(0);
		assertEquals(id, responseElement.getSubjectOfCareId());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_1, responseElement.getSenderRequestId());		
		assertEquals(TEST_LOGICAL_ADDRESS_1, responseElement.getCareUnit());		
		
		responseElement = response.getRequestActivity().get(1);
		assertEquals(id, responseElement.getSubjectOfCareId());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_2, responseElement.getSenderRequestId());		
		assertEquals(TEST_LOGICAL_ADDRESS_2, responseElement.getCareUnit());		
		
		responseElement = response.getRequestActivity().get(2);
		assertEquals(id, responseElement.getSubjectOfCareId());		
		assertEquals(TEST_BOOKING_ID_MANY_BOOKINGS_3, responseElement.getSenderRequestId());		
		assertEquals(TEST_LOGICAL_ADDRESS_2, responseElement.getCareUnit());

    
    	// Verify the Processing Status
		List<ProcessingStatusRecordType> statusList = processingStatusHolder.value.getProcessingStatusList();
		assertEquals(3, statusList.size());
		
		
		assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
		assertProcessingStatusDataFromSource(statusList.get(1), TEST_LOGICAL_ADDRESS_2);
		assertProcessingStatusNoDataSynchFailed(statusList.get(2), TEST_LOGICAL_ADDRESS_3, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_TIMEOUT_MSG);
    }

    @Test
	public void test_fault_invalidInput() throws Exception {
    	String id = TEST_ID_FAULT_INVALID_ID;
    	RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(DEFAULT_SERVICE_ADDRESS);
		Holder<GetRequestActivitiesResponseType> responseHolder = new Holder<GetRequestActivitiesResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);
    	GetRequestActivitiesResponseType response = responseHolder.value;

    	// Expect a response with zero booking and error information in the processingstatus
		assertEquals(0, response.getRequestActivity().size());
		
    	// Verify the Processing Status
		List<ProcessingStatusRecordType> statusList = processingStatusHolder.value.getProcessingStatusList();
		assertEquals(1, statusList.size());
		
		assertProcessingStatusNoDataSynchFailed(statusList.get(0), TEST_LOGICAL_ADDRESS_1, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_INVALID_ID_MSG);
	}

//	TODO: Mule EE dependency
//    @Test
    public void test_ok_caching() {
    	String id = TEST_ID_ONE_BOOKING;
    	long   expectedProcessingTime = TEST_LOGICAL_ADDRESS_1_RESPONSE_TIME;
    	String expectedBookingId      = TEST_BOOKING_ID_ONE_BOOKING;
		String expectedLogicalAddress = TEST_LOGICAL_ADDRESS_1;

		long ts = System.currentTimeMillis();
    	ProcessingStatusType statusList = do_test_ok_one_booking(id, expectedBookingId, expectedLogicalAddress);
		ts = System.currentTimeMillis() - ts;
		assertProcessingStatusDataFromSource(statusList.getProcessingStatusList().get(0), expectedLogicalAddress);
		assertTrue("Expected a long processing time (i.e. a non cached response)", ts > expectedProcessingTime);

		ts = System.currentTimeMillis();
    	statusList = do_test_ok_one_booking(id, expectedBookingId, expectedLogicalAddress);
		ts = System.currentTimeMillis() - ts;
		assertProcessingStatusDataFromCache(statusList.getProcessingStatusList().get(0), expectedLogicalAddress);
		assertTrue("Expected a short processing time (i.e. a cached response)", ts < expectedProcessingTime);
    }
    
    /* Timeout aspects are covered in the test test_ok_many_bookings_with_partial_timeout
    @Test
	public void test_fault_timeout() {
        try {
	    	String id = TEST_ID_FAULT_TIMEOUT;
	    	RequestActivitiesTestConsumer consumer = new RequestActivitiesTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Holder<GetRequestActivitiesResponseType> responseHolder = new Holder<GetRequestActivitiesResponseType>();
			Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
	    	consumer.callService(LOGICAL_ADDRESS, id, processingStatusHolder, responseHolder);
	    	GetRequestActivitiesResponseType response = responseHolder.value;
	        fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));

        } catch (SOAPFaultException e) {
            assertTrue("Unexpected error message: " + e.getMessage(), e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
        }

		// Sleep for a short time period  to allow the JMS response message to be delivered, otherwise ActiveMQ data store seems to be corrupt afterwards...
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
    }
    */
 
	private void assertProcessingStatusDataFromSource(ProcessingStatusRecordType status, String logicalAddress) {
		assertEquals(logicalAddress, status.getLogicalAddress());
		assertEquals(DATA_FROM_SOURCE, status.getStatusCode());
		assertFalse(status.isIsResponseFromCache());
		assertTrue(status.isIsResponseInSynch());
		assertNotNull(status.getLastSuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynchError());
	}

	private void assertProcessingStatusDataFromCache(ProcessingStatusRecordType status, String logicalAddress) {
		assertEquals(logicalAddress, status.getLogicalAddress());
		assertEquals(DATA_FROM_CACHE, status.getStatusCode());
		assertTrue(status.isIsResponseFromCache());
		assertTrue(status.isIsResponseInSynch());
		assertNotNull(status.getLastSuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynch());
		assertNull(status.getLastUnsuccessfulSynchError());
	}

	private void assertProcessingStatusNoDataSynchFailed(ProcessingStatusRecordType status, String logicalAddress, CausingAgentEnum agent, String expectedErrStartingWith) {
		assertEquals(logicalAddress, status.getLogicalAddress());
		assertEquals(NO_DATA_SYNCH_FAILED, status.getStatusCode());
		assertFalse(status.isIsResponseFromCache());
		assertFalse(status.isIsResponseInSynch());
		assertNull(status.getLastSuccessfulSynch());
		assertNotNull(status.getLastUnsuccessfulSynch());

		LastUnsuccessfulSynchErrorType error = status.getLastUnsuccessfulSynchError();
		assertNotNull(error);
		assertEquals(agent, error.getCausingAgent());
		assertNotNull(error.getCode());
		assertTrue("Missing expected [" + expectedErrStartingWith + "] in the beginning if the error message [" + error.getText() + "]", error.getText().startsWith(expectedErrStartingWith));
	}
}