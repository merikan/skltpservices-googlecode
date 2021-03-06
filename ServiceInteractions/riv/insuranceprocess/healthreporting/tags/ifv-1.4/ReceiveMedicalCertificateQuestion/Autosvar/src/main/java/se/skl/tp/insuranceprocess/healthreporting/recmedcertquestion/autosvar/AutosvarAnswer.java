package se.skl.tp.insuranceprocess.healthreporting.recmedcertquestion.autosvar;

import iso.v21090.dt.v1.II;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import org.mule.module.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.qa.v1.InnehallType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.LakarutlatandeEnkelType;
import se.skl.riv.insuranceprocess.healthreporting.qa.v1.VardAdresseringsType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificatequestionresponder.v1.QuestionFromFkType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.AnswerToFkType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerType;
import se.skl.riv.insuranceprocess.healthreporting.v2.EnhetType;
import se.skl.riv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.skl.riv.insuranceprocess.healthreporting.v2.PatientType;
import se.skl.riv.insuranceprocess.healthreporting.v2.VardgivareType;

public class AutosvarAnswer extends Thread {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	QuestionFromFkType question;
	
	public AutosvarAnswer(QuestionFromFkType question) {
		super();
		this.question = question;		
	}

	@Override
	public void run() {
		logger.debug("Sending autoanswer to question.");
				
		MuleClient client;
		try {
			client = new MuleClient();
			
			AttributedURIType logicalAddressHeader = new AttributedURIType();
			logicalAddressHeader.setValue("2021005521");
	
			SendMedicalCertificateAnswerType request = new SendMedicalCertificateAnswerType();

			// Set an answer 
			request.setAnswer(getAnswer(question));
			
			// Create payload to webservice
			Object[] payloadOut = new Object[] {logicalAddressHeader, request};

			client.send("autosvarEndpoint", payloadOut, null);
			logger.info("Autoanswer sent for careUnit with id: " + request.getAnswer().getAdressVard().getHosPersonal().getEnhet().getEnhetsId().getExtension());
			
		} catch (Exception e) {
			logger.error("Autoanswer exception: " + e.getMessage());
		}		
	}
	
	private static AnswerToFkType getAnswer(QuestionFromFkType question) throws Exception {
		AnswerToFkType meddelande = new AnswerToFkType();

		// Avsändare
		VardAdresseringsType avsandare = new VardAdresseringsType();		
		HosPersonalType hosPersonal = new HosPersonalType();
		EnhetType enhet = new EnhetType();	
		II enhetsId = new II();
		enhetsId.setRoot("1.2.752.129.2.1.4.1");
		enhetsId.setExtension(question.getAdressVard().getHosPersonal().getEnhet().getEnhetsId().getExtension());
		enhet.setEnhetsId(enhetsId);
		enhet.setEnhetsnamn(question.getAdressVard().getHosPersonal().getEnhet().getEnhetsnamn());
		VardgivareType vardgivare = new VardgivareType();
		vardgivare.setVardgivarnamn(question.getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivarnamn());
		II vardgivareId = new II();
		vardgivareId.setRoot("1.2.752.129.2.1.4.1");
		vardgivareId.setExtension(question.getAdressVard().getHosPersonal().getEnhet().getVardgivare().getVardgivareId().getExtension());
		vardgivare.setVardgivareId(vardgivareId);
		enhet.setVardgivare(vardgivare);
		hosPersonal.setEnhet(enhet);
		hosPersonal.setFullstandigtNamn(question.getAdressVard().getHosPersonal().getFullstandigtNamn());
		II personalId = new II();
		personalId.setRoot("1.2.752.129.2.1.4.1");
		personalId.setExtension(question.getAdressVard().getHosPersonal().getPersonalId().getExtension());
		hosPersonal.setPersonalId(personalId);
		avsandare.setHosPersonal(hosPersonal);
		meddelande.setAdressVard(avsandare);
				
		// Avsänt tidpunkt - nu
		meddelande.setAvsantTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		// Set läkarutlåtande enkel från vården
		meddelande.setVardReferensId("");
		LakarutlatandeEnkelType lakarutlatandeEnkel = new LakarutlatandeEnkelType();
		PatientType patient = new PatientType();
		II personId = new II();
		personId.setRoot("1.2.752.129.2.1.3.1"); // OID för samordningsnummer är 1.2.752.129.2.1.3.3.
		personId.setExtension(question.getLakarutlatande().getPatient().getPersonId().getExtension());
		patient.setPersonId(personId);
		patient.setFullstandigtNamn(question.getLakarutlatande().getPatient().getFullstandigtNamn()); 
		lakarutlatandeEnkel.setPatient(patient);
		lakarutlatandeEnkel.setLakarutlatandeId(question.getLakarutlatande().getLakarutlatandeId());
		lakarutlatandeEnkel.setSigneringsTidpunkt(question.getLakarutlatande().getSigneringsTidpunkt());
		meddelande.setLakarutlatande(lakarutlatandeEnkel);

		// Set Försäkringskassans id
		meddelande.setFkReferensId(question.getFkReferensId());
		meddelande.setVardReferensId("autosvar");

		// Set ämne
		meddelande.setAmne(question.getAmne());

		// Set meddelande	
		InnehallType fraga = new InnehallType();
		fraga.setMeddelandeText(question.getFraga().getMeddelandeText());
		fraga.setSigneringsTidpunkt(question.getFraga().getSigneringsTidpunkt());
		meddelande.setFraga(fraga);

		InnehallType svar = new InnehallType();
		StringBuffer autoSvar = new StringBuffer();
		autoSvar.append("Detta är en automatisk notifiering från vårdens nationella it-system.");
		autoSvar.append("/n");
		autoSvar.append("Just detta meddelande gick inte att leverera till avsedd mottagare på grund av ett fel och kommer därför inte att levereras.");
		autoSvar.append("/n");
		autoSvar.append("För att inte fördröja handläggningen, så ber vi er i detta fall vara vänlig att kommunicera med avsedd mottagare i vården enligt gällande manuella rutiner, dvs genom telefon eller brev.");
		svar.setMeddelandeText(autoSvar.toString());
		svar.setSigneringsTidpunkt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		meddelande.setSvar(svar);

		return meddelande;
	}	
}




