/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.insuranceprocess.healthreporting.recmedcertanswer.producer;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.wsaddressing10.AttributedURIType;

import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswer.v1.rivtabp20.ReceiveMedicalCertificateAnswerResponderInterface;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.ReceiveMedicalCertificateAnswerResponseType;
import se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswerresponder.v1.ReceiveMedicalCertificateAnswerType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;

@WebService(serviceName = "ReceiveMedicalCertificateAnswerResponderService", endpointInterface = "se.skl.riv.insuranceprocess.healthreporting.receivemedicalcertificateanswer.v1.rivtabp20.ReceiveMedicalCertificateAnswerResponderInterface", portName = "ReceiveMedicalCertificateAnswerResponderPort", targetNamespace = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateAnswer:1:rivtabp20", wsdlLocation = "schemas/vard/interactions/ReceiveMedicalCertificateAnswerInteraction/ReceiveMedicalCertificateAnswerInteraction_1.0_rivtabp20.wsdl")
public class RecMedCertAnswerImpl implements
		ReceiveMedicalCertificateAnswerResponderInterface {

	private final Logger log = LoggerFactory.getLogger(getClass());

	public ReceiveMedicalCertificateAnswerResponseType receiveMedicalCertificateAnswer(
			AttributedURIType logicalAddress,
			ReceiveMedicalCertificateAnswerType parameters) {

		log.info("receiveMedicalCertificateAnswer({},{})", logicalAddress,
				parameters);

		try {
			ReceiveMedicalCertificateAnswerResponseType response = new ReceiveMedicalCertificateAnswerResponseType();

			ResultOfCall resCall = new ResultOfCall();

			String personId = parameters.getAnswer().getLakarutlatande()
					.getPatient().getPersonId().getExtension();

			log.info("Received personId: {}", personId);

			// Check if to send an ERROR
			if (parameters.getAnswer().getLakarutlatande().getPatient()
					.getPersonId().getExtension()
					.equalsIgnoreCase("19101010-1234")) {
				resCall.setResultCode(ResultCodeEnum.ERROR);
				response.setResult(resCall);
			} else {
				resCall.setResultCode(ResultCodeEnum.OK);
				response.setResult(resCall);
			}

			log.info("Response returned: {} ", resCall.getResultCode());

			return response;
		} catch (RuntimeException e) {
			log.error("Error occured: " + e);
			throw e;
		}
	}
}