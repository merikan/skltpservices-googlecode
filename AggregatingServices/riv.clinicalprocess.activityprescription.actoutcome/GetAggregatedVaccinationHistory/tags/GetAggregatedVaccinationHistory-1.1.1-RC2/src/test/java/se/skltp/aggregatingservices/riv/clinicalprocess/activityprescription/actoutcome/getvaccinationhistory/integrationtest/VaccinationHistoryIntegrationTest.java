package se.skltp.aggregatingservices.riv.clinicalprocess.activityprescription.actoutcome.getvaccinationhistory.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skltp.aggregatingservices.VaccinationHistoryMuleServer.getAddress;
import static se.skltp.agp.riv.interoperability.headers.v1.CausingAgentEnum.VIRTUALIZATION_PLATFORM;
import static se.skltp.agp.test.consumer.AbstractTestConsumer.SAMPLE_ORIGINAL_CONSUMER_HSAID;
import static se.skltp.agp.test.consumer.AbstractTestConsumer.SAMPLE_SENDER_ID;
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

import java.util.List;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.riv.clinicalprocess.activityprescription.actoutcome.getvaccinationhistoryresponder.v1.GetVaccinationHistoryResponseType;
import se.riv.clinicalprocess.activityprescription.actoutcome.v1.VaccinationMedicalRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.test.consumer.AbstractAggregateIntegrationTest;
import se.skltp.agp.test.consumer.ExpectedTestData;
import se.skltp.agp.test.producer.EngagemangsindexTestProducerLogger;
import se.skltp.agp.test.producer.TestProducerLogger;

public class VaccinationHistoryIntegrationTest extends AbstractAggregateIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(VaccinationHistoryIntegrationTest.class);
    
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("GetAggregatedVaccinationHistory-config");
    private static final String SKLTP_HSA_ID = rb.getString("SKLTP_HSA_ID");
    
    private static final String LOGICAL_ADDRESS = "logical-address";
    private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
    private static final String EXPECTED_ERR_INVALID_ID_MSG = "Invalid Id: " + TEST_RR_ID_FAULT_INVALID_ID;;
    private static final String DEFAULT_SERVICE_ADDRESS = getAddress("SERVICE_INBOUND_URL");

    protected String getConfigResources() {
        return 
                "soitoolkit-mule-jms-connector-activemq-embedded.xml," + 
                "GetAggregatedVaccinationHistory-common.xml," +
                //			"aggregating-services-common.xml," + 
                //			"aggregating-service.xml," +
                "teststub-services/engagemangsindex-teststub-service.xml," + 
                "teststub-services/service-producer-teststub-service.xml";
    }

    /**
     * Perform a test that is expected to return zero hits
     */
    @Test
    public void test_ok_zero_hits() {
        doTest(TEST_RR_ID_ZERO_HITS, 0);		
    }
    
    /**
	 * Perform a test that is expected to return an exception due to missing mandatory http headers (sender-id and original-consumer-id)
	 */
    @Test
    public void test_fault_missing_http_headers() {
    	try {
			doTest(TEST_RR_ID_ZERO_HITS, null, SAMPLE_ORIGINAL_CONSUMER_HSAID, 0);
			fail("This one should fail on missing http header");
		} catch (SOAPFaultException e) {
			assertEquals("Mandatory HTTP header x-vp-sender-id is missing", e.getMessage());
		}

    	try {
	    	doTest(TEST_RR_ID_ZERO_HITS, SAMPLE_SENDER_ID, null, 0);		
	       	fail("This one should fail on missing http header");
		} catch (SOAPFaultException e) {
			assertEquals("Mandatory HTTP header x-rivta-original-serviceconsumer-hsaid is missing", e.getMessage());
		}

    	try {
	       	doTest(TEST_RR_ID_ZERO_HITS, null, null, 0);		
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

        List<ProcessingStatusRecordType> statusList = doTest(TEST_RR_ID_ONE_HIT, 1, new ExpectedTestData(TEST_BO_ID_ONE_HIT, TEST_LOGICAL_ADDRESS_1));

        assertProcessingStatusDataFromSource(statusList.get(0), TEST_LOGICAL_ADDRESS_1);
    }

    /**
     * Perform a test that is expected to return three hit with data from two source systems and one source system that cause a timeout
     */
    @Test
    public void test_ok_many_hits_with_partial_timeout() {

        // Setup call and verify the response, expect one booking from source #1, two from source #2 and a timeout from source #3
        List<ProcessingStatusRecordType> statusList = doTest(TEST_RR_ID_MANY_HITS, 3, 
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

        List<ProcessingStatusRecordType> statusList = doTest(TEST_RR_ID_FAULT_INVALID_ID, 1);

        // Verify the Processing Status, expect a processing failure from the source system
        assertProcessingStatusNoDataSynchFailed(statusList.get(0), TEST_LOGICAL_ADDRESS_1, VIRTUALIZATION_PLATFORM, EXPECTED_ERR_INVALID_ID_MSG);
    }

    //	TODO: Mule EE dependency
    //  @Test
    public void test_ok_caching() {
        String registeredResidentId   = TEST_RR_ID_ONE_HIT;
        long   expectedProcessingTime = getTestDb().getProcessingTime(TEST_LOGICAL_ADDRESS_1);
        String expectedBookingId      = TEST_BO_ID_ONE_HIT;
        String expectedLogicalAddress = TEST_LOGICAL_ADDRESS_1;

        long ts = System.currentTimeMillis();
        List<ProcessingStatusRecordType> statusList = doTest(registeredResidentId, 1, new ExpectedTestData(expectedBookingId, expectedLogicalAddress));
        ts = System.currentTimeMillis() - ts;
        assertProcessingStatusDataFromSource(statusList.get(0), expectedLogicalAddress);
        assertTrue("Expected a long processing time (i.e. a non cached response)", ts > expectedProcessingTime);

        ts = System.currentTimeMillis();
        statusList = doTest(registeredResidentId, 1, new ExpectedTestData(expectedBookingId, expectedLogicalAddress));
        ts = System.currentTimeMillis() - ts;
        assertProcessingStatusDataFromCache(statusList.get(0), expectedLogicalAddress);
        assertTrue("Expected a short processing time (i.e. a cached response)", ts < expectedProcessingTime);
    }
    
    /**
     * Helper method for performing a call to the aggregating service and perform some common validations of the result
     * 
     * @param registeredResidentId
     * @param expectedProcessingStatusSize
     * @param testData
     * @return
     */
	private List<ProcessingStatusRecordType> doTest(String registeredResidentId, int expectedProcessingStatusSize, ExpectedTestData... testData) {
		return doTest(registeredResidentId, SAMPLE_SENDER_ID, SAMPLE_ORIGINAL_CONSUMER_HSAID, expectedProcessingStatusSize, testData);
    }


    /**
     * Helper method for performing a call to the aggregating service and perform some common validations of the result
     * 
     * @param registeredResidentId
     * @param expectedProcessingStatusSize
     * @param testData
     * @return
     */
    private List<ProcessingStatusRecordType> doTest(String registeredResidentId, String senderId, String originalConsumerHsaId,  int expectedProcessingStatusSize, ExpectedTestData... testData) {

        // Setup and perform the call to the web service
        VaccinationHistoryTestConsumer consumer = new VaccinationHistoryTestConsumer(DEFAULT_SERVICE_ADDRESS, senderId, originalConsumerHsaId);
        Holder<GetVaccinationHistoryResponseType> responseHolder = new Holder<GetVaccinationHistoryResponseType>();
        Holder<ProcessingStatusType> processingStatusHolder = new Holder<ProcessingStatusType>();
        consumer.callService(LOGICAL_ADDRESS, registeredResidentId, processingStatusHolder, responseHolder);

        // Verify the response size and content
        GetVaccinationHistoryResponseType response = responseHolder.value;
        int expextedResponseSize = testData.length;
        assertEquals(expextedResponseSize, response.getVaccinationMedicalRecord().size());

        for (int i = 0; i < testData.length; i++) {
            VaccinationMedicalRecordType responseElement = response.getVaccinationMedicalRecord().get(i);
            assertEquals(registeredResidentId, responseElement.getVaccinationMedicalRecordHeader().getPatientId().getId());
            assertEquals(testData[i].getExpectedBusinessObjectId(), responseElement.getVaccinationMedicalRecordHeader().getDocumentId());
            assertEquals(testData[i].getExpectedLogicalAddress(), responseElement.getVaccinationMedicalRecordHeader().getAccountableHealthcareProfessional().getHealthcareProfessionalCareUnitHSAId());
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