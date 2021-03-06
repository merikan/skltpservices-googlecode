package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.basic.getaggregatedmeasurement.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_1;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_2;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_MANY_HITS_3;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_BO_ID_ONE_HIT;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_LOGICAL_ADDRESS_1;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_LOGICAL_ADDRESS_2;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_LOGICAL_ADDRESS_3;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_FAULT_INVALID_ID;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_MANY_HITS;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_ONE_HIT;
import static se.skltp.agp.test.producer.TestProducerDb.TEST_RR_ID_ZERO_HITS;
import static se.skltp.agp.riv.interoperability.headers.v1.CausingAgentEnum.VIRTUALIZATION_PLATFORM;
import static se.skltp.agp.test.consumer.AbstractTestConsumer.SAMPLE_ORIGINAL_CONSUMER_HSAID;
import static se.skltp.agp.test.consumer.AbstractTestConsumer.SAMPLE_SENDER_ID;

import java.util.List;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.riv.clinicalprocess.healthcond.basic.getmeasurementresponder.v1.GetMeasurementResponseType;
import se.riv.clinicalprocess.healthcond.basic.v1.MeasurementType;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.basic.getaggregatedmeasurement.GetAggregatedMeasurementMuleServer;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.test.consumer.AbstractAggregateIntegrationTest;
import se.skltp.agp.test.consumer.ExpectedTestData;
import se.skltp.agp.test.producer.EngagemangsindexTestProducerLogger;
import se.skltp.agp.test.producer.TestProducerLogger;

public class GetAggregatedMeasurementIntegrationTest extends AbstractAggregateIntegrationTest {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(GetAggregatedMeasurementIntegrationTest.class);
	 
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedMeasurement-config");
	private static final String SKLTP_HSA_ID = rb.getString("SKLTP_HSA_ID");

    private static final String LOGICAL_ADDRESS = "logical-address";
    private static final String NO_LOGICAL_ADDRESS = null;
	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
	private static final String EXPECTED_ERR_INVALID_ID_MSG = "Invalid Id: " + TEST_RR_ID_FAULT_INVALID_ID;
	private static final String DEFAULT_SERVICE_ADDRESS = GetAggregatedMeasurementMuleServer.getAddress("SERVICE_INBOUND_URL");

	protected String getConfigResources() {
		return 
			"soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
	  		"GetAggregatedMeasurement-common.xml," +
//          Only load GetAggregatedMeasurement-common.xml, it will import the other config files since mule-deploy.properties can't load config-files from jar-files on the classpath, e.g. agp-core.jar
//			"aggregating-services-common.xml," + 
//	        "aggregating-service.xml," +
			"teststub-services/engagemangsindex-teststub-service.xml," + 
			"teststub-services/service-producer-teststub-service.xml";
    }

	/**
	 * Perform a test that is expected to return zero hits
	 */
    @Test
    public void test_ok_zero_hits() {
    	doTest(LOGICAL_ADDRESS, TEST_RR_ID_ZERO_HITS, 0);		
    }
    
    /**
	 * Perform a test that is expected to return an exception due to missing mandatory http headers (sender-id and original-consumer-id)
	 */
    @Test
    public void test_fault_missing_http_headers() {
    	try {
			doTest(LOGICAL_ADDRESS, TEST_RR_ID_ZERO_HITS, null, SAMPLE_ORIGINAL_CONSUMER_HSAID, 0, false);
			fail("This one should fail on missing http header");
		} catch (SOAPFaultException e) {
			assertEquals("Mandatory HTTP header x-vp-sender-id is missing", e.getMessage());
		}

    	try {
	    	doTest(LOGICAL_ADDRESS, TEST_RR_ID_ZERO_HITS, SAMPLE_SENDER_ID, null, 0, false);		
	       	fail("This one should fail on missing http header");
		} catch (SOAPFaultException e) {
			assertEquals("Mandatory HTTP header x-rivta-original-serviceconsumer-hsaid is missing", e.getMessage());
		}

    	try {
	       	doTest(LOGICAL_ADDRESS, TEST_RR_ID_ZERO_HITS, null, null, 0, false);		
	       	fail("This one should fail on missing http header");
		} catch (SOAPFaultException e) {
			assertEquals("Mandatory HTTP headers x-vp-sender-id and x-rivta-original-serviceconsumer-hsaid are missing", e.getMessage());
		}
    }

	/**
	 * Perform a test that is expected to return one hit with data from one source system
	 */
    @Test
    public void test_ok_one_hit() {
    	
    	List<ProcessingStatusRecordType> statusList = doTest(TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_ONE_HIT, 1, new ExpectedTestData(TEST_BO_ID_ONE_HIT, TEST_LOGICAL_ADDRESS_1));

    	assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
    }
    
    @Test
    public void test_ok_one_hit_all_request_params() {
    	
    	List<ProcessingStatusRecordType> statusList = doTestWithAllRequestParams(TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_ONE_HIT, 1, new ExpectedTestData(TEST_BO_ID_ONE_HIT, TEST_LOGICAL_ADDRESS_1));

    	assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
    }
    
    /**
	 * Perform a test that is expected to return one hit with data from one source system, where 
	 * patient have data in several source systems.
	 */
    @Test
    public void test_ok_one_hit_patient_with_engagements_in_several_source_systems() {
    	
    	List<ProcessingStatusRecordType> statusList = doTest(TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_MANY_HITS, 1, new ExpectedTestData(TEST_BO_ID_ONE_HIT, TEST_LOGICAL_ADDRESS_1));

    	assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
    }

	/**
	 * Perform a test that is expected to return three hit with data from two source systems and one source system that cause a timeout
	 */
    @Test
    public void test_ok_many_hits_with_partial_timeout() {

    	// Setup call and verify the response, expect one booking from source #1, two from source #2 and a timeout from source #3
    	List<ProcessingStatusRecordType> statusList = doTest(NO_LOGICAL_ADDRESS, TEST_RR_ID_MANY_HITS, 3, 
    		new ExpectedTestData(TEST_BO_ID_MANY_HITS_1, TEST_LOGICAL_ADDRESS_1),
    		new ExpectedTestData(TEST_BO_ID_MANY_HITS_2, TEST_LOGICAL_ADDRESS_2),
    		new ExpectedTestData(TEST_BO_ID_MANY_HITS_3, TEST_LOGICAL_ADDRESS_2));
		
    	// Verify the Processing Status, expect ok from source system #1 and #2 but a timeout from #3
		assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
		assertProcessingStatusDataFromSource(statusList.get(1), TEST_LOGICAL_ADDRESS_2);
		assertProcessingStatusNoDataSynchFailed(statusList.get(2), TEST_LOGICAL_ADDRESS_3, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_TIMEOUT_MSG);
    }

	/**
	 * Perform a test that is expected to casue the source system to fail with its processing
	 */
    @Test
	public void test_fault_invalidInput() throws Exception {

    	List<ProcessingStatusRecordType> statusList = doTest(TEST_LOGICAL_ADDRESS_1, TEST_RR_ID_FAULT_INVALID_ID, 1);
		
    	// Verify the Processing Status, expect a processing failure from the source system
		assertProcessingStatusNoDataSynchFailed(statusList.get(0), TEST_LOGICAL_ADDRESS_1, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_INVALID_ID_MSG);
	}
    
    /**
     * Helper method for performing a call to the aggregating service and perform some common validations of the result
     * 
     * @param logicalAddress
     * @param registeredResidentId
     * @param expectedProcessingStatusSize
     * @param testData
     * @return
     */
	private List<ProcessingStatusRecordType> doTest(String logicalAddress, String registeredResidentId, int expectedProcessingStatusSize, ExpectedTestData... testData) {
		return doTest(logicalAddress, registeredResidentId, SAMPLE_SENDER_ID, SAMPLE_ORIGINAL_CONSUMER_HSAID, expectedProcessingStatusSize, false, testData);
    }
	
	/**
     * Helper method for performing a call to the aggregating service and perform some common validations of the result.
     * 
     * Includes values in all request params.
     * 
     * @param logicalAddress
     * @param registeredResidentId
     * @param expectedProcessingStatusSize
     * @param testData
     * @return
     */
	private List<ProcessingStatusRecordType> doTestWithAllRequestParams(String logicalAddress, String registeredResidentId, int expectedProcessingStatusSize, ExpectedTestData... testData) {
		return doTest(logicalAddress, registeredResidentId, SAMPLE_SENDER_ID, SAMPLE_ORIGINAL_CONSUMER_HSAID, expectedProcessingStatusSize, true, testData);
    }

	/**
     * Helper method for performing a call to the aggregating service and perform some common validations of the result
     * 
     * @param logicalAddress
     * @param registeredResidentId
     * @param senderId
     * @param originalConsumerHsaId
     * @param expectedProcessingStatusSize
     * @param inclAllReqParams
     * @param testData
     * @return
     */
	private List<ProcessingStatusRecordType> doTest(String logicalAddress, String registeredResidentId, String senderId, String originalConsumerHsaId, int expectedProcessingStatusSize, boolean inclAllReqParams, ExpectedTestData... testData) {

		// Setup and perform the call to the web service
		GetAggregatedMeasurementTestConsumer consumer = new GetAggregatedMeasurementTestConsumer(DEFAULT_SERVICE_ADDRESS, senderId ,originalConsumerHsaId);
		Holder<GetMeasurementResponseType> responseHolder = new Holder<GetMeasurementResponseType>();
		Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
    	
		if(inclAllReqParams){	
			consumer.callServiceIncludeValuesInAllReqParams(logicalAddress, registeredResidentId, "19791017160000", "19791017170000", processingStatusHolder, responseHolder);
		}else{
			consumer.callService(logicalAddress, registeredResidentId, processingStatusHolder, responseHolder);
		}

    	// Verify the response size and content
    	GetMeasurementResponseType response = responseHolder.value;
    	int expextedResponseSize = testData.length;
		assertEquals(expextedResponseSize, response.getMeasurement().size());
		
		for (int i = 0; i < testData.length; i++) {
			MeasurementType responseElement = response.getMeasurement().get(i);
			assertEquals(registeredResidentId, responseElement.getPatient().getId().getExtension());		

			// TODO: CHANGE GENERATED CODE - START
			//assertEquals(testData[i].getExpectedBusinessObjectId(), responseElement.getSenderRequestId());		
			//assertEquals(testData[i].getExpectedLogicalAddress(), responseElement.getCareUnit());		
			// TODO: CHANGE GENERATED CODE - END
		}

    	// Verify the size of the processing status and return it for further analysis
		ProcessingStatusType statusList = processingStatusHolder.value;
		assertEquals(expectedProcessingStatusSize, statusList.getProcessingStatusList().size());
		
		// Verify that correct "x-vp-sender-id" http header was passed to the engagement index
		assertEquals(SKLTP_HSA_ID, EngagemangsindexTestProducerLogger.getLastSenderId());
		
		// Verify that correct "x-rivta-original-serviceconsumer-hsaid" http header was passed to the engagement index
		assertEquals(SAMPLE_ORIGINAL_CONSUMER_HSAID, EngagemangsindexTestProducerLogger.getLastOriginalConsumer());

		// Verify that correct "x-vp-sender-id" and "x-rivta-original-serviceconsumer-hsaid" http header was passed to the service producer,
		// given that a service producer was called
		if (expectedProcessingStatusSize > 0) {
			assertEquals(SAMPLE_SENDER_ID, TestProducerLogger.getLastSenderId());
			assertEquals(SAMPLE_ORIGINAL_CONSUMER_HSAID, TestProducerLogger.getLastOriginalConsumer());
		}

		return statusList.getProcessingStatusList();
	}
}