package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.description.getaggregatedcaredocumentation.integrationtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.clinicalprocess.healthcond.description.enums.v2.ClinicalDocumentNoteCodeEnum;
import se.riv.clinicalprocess.healthcond.description.getcaredocumentationresponder.v2.GetCareDocumentationResponseType;
import se.riv.clinicalprocess.healthcond.description.v2.AuthorType;
import se.riv.clinicalprocess.healthcond.description.v2.CareDocumentationBodyType;
import se.riv.clinicalprocess.healthcond.description.v2.CareDocumentationType;
import se.riv.clinicalprocess.healthcond.description.v2.ClinicalDocumentNoteType;
import se.riv.clinicalprocess.healthcond.description.v2.PatientIdType;
import se.riv.clinicalprocess.healthcond.description.v2.PatientSummaryHeaderType;
import se.skltp.agp.test.producer.TestProducerDb;

public class CareDocumentationTestProducerDb extends TestProducerDb {

    private static final Logger log = LoggerFactory.getLogger(CareDocumentationTestProducerDb.class);

    @Override
    public Object createResponse(Object... responseItems) {
        log.debug("Creates a response with {} items", responseItems);
        GetCareDocumentationResponseType response = new GetCareDocumentationResponseType();
        for (int i = 0; i < responseItems.length; i++) {
            response.getCareDocumentation().add((CareDocumentationType)responseItems[i]);
        }
        return response;
    }

    public static final String TEST_REASON_DEFAULT = "default reason";
    public static final String TEST_REASON_UPDATED = "updated reason";

    @Override
    public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {

        log.debug("Created one response item for logical-address {}, registeredResidentId {} and businessObjectId {}",
                new Object[] {logicalAddress, registeredResidentId, businessObjectId});

        CareDocumentationType response = new CareDocumentationType();
        PatientSummaryHeaderType header = new PatientSummaryHeaderType();
        PatientIdType patientId = new PatientIdType();
        patientId.setId(registeredResidentId);
        patientId.setType("1.2.752.129.2.1.3.1"); 
        header.setPatientId(patientId);
        header.setApprovedForPatient(true);
        header.setDocumentTime(time);
        AuthorType author = new AuthorType();
        author.setCareUnitHSAid(logicalAddress);

        if(TestProducerDb.TEST_LOGICAL_ADDRESS_1.equals(logicalAddress)){
            author.setAuthorOrgUnitName("Vårdcentralen Kusten, Kärna");
        } else if(TestProducerDb.TEST_LOGICAL_ADDRESS_2.equals(logicalAddress)){
            author.setAuthorOrgUnitName("Vårdcentralen Molnet");
        } else {
            author.setAuthorOrgUnitName("Vårdcentralen Stacken");
        }
        
        header.setAuthor(author);
        header.setSourceSystemHSAid(logicalAddress);
        header.setDocumentId(businessObjectId);
        response.setCareDocumentationHeader(header);

        CareDocumentationBodyType body = new CareDocumentationBodyType();
        ClinicalDocumentNoteType note = new ClinicalDocumentNoteType();
        note.setClinicalDocumentNoteCode(ClinicalDocumentNoteCodeEnum.ATB);
        note.setClinicalDocumentNoteText("text");
        note.setClinicalDocumentNoteTitle("title");
        body.setClinicalDocumentNote(note);
        response.setCareDocumentationBody(body);
        return response;
    }
}
