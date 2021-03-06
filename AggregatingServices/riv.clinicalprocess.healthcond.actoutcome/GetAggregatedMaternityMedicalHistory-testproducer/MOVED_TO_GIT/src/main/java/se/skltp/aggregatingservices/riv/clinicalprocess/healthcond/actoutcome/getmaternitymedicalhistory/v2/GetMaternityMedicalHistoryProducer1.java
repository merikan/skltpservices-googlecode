package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getmaternitymedicalhistory.v2;

import java.util.List;

import javax.jws.WebService;

import se.riv.clinicalprocess.healthcond.actoutcome.getmaternitymedicalhistory.v2.rivtabp21.GetMaternityMedicalHistoryResponderInterface;
import se.riv.clinicalprocess.healthcond.actoutcome.getmaternitymedicalhistoryresponder.v2.GetMaternityMedicalHistoryResponseType;
import se.riv.clinicalprocess.healthcond.actoutcome.getmaternitymedicalhistoryresponder.v2.GetMaternityMedicalHistoryType;
import se.riv.clinicalprocess.healthcond.actoutcome.v2.MaternityMedicalRecordType;

@WebService
public class GetMaternityMedicalHistoryProducer1 implements GetMaternityMedicalHistoryResponderInterface {

	private final static String MAGDALENA = "194606109108";
	private final static String NINA = "197309069289";
	private final static String FRIDA = "197705232382";
	private final static String ULLA = "198611062384";
	private final static String HSAPRODUCER1 = "HSAPRODUCER1";

	@Override
	public GetMaternityMedicalHistoryResponseType getMaternityMedicalHistory(String logicalAddress,
			GetMaternityMedicalHistoryType parameters) {

		GetMaternityMedicalHistoryBuilder builder = new GetMaternityMedicalHistoryBuilder();

		String patientId = parameters.getPatientId().getId();

		GetMaternityMedicalHistoryResponseType response = new GetMaternityMedicalHistoryResponseType();
		List<MaternityMedicalRecordType> maternityMedicalRecords = response.getMaternityMedicalRecord();

		if (MAGDALENA.equals(patientId)) {
			for (int i = 1; i < 4; i++) {
				maternityMedicalRecords.add(builder.createMaternityMedicalRecord(parameters, HSAPRODUCER1, "2013" + i
						+ "01020202"));
			}
		} else if (NINA.equals(patientId)) {
			for (int i = 1; i < 3; i++) {
				maternityMedicalRecords.add(builder.createMaternityMedicalRecord(parameters, HSAPRODUCER1, "2013" + i
						+ "01020202"));
			}
		} else if (FRIDA.equals(patientId)) {
			maternityMedicalRecords.add(builder
					.createMaternityMedicalRecord(parameters, HSAPRODUCER1, "20130401020202"));
		} else if (ULLA.equals(patientId)) {
			for (int i = 1; i < 5; i++) {
				maternityMedicalRecords.add(builder.createMaternityMedicalRecord(parameters, HSAPRODUCER1, "2013" + i
						+ "01020202"));
			}
		}
		
		return response;
	}

}
