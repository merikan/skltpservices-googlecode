/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skl.skltpservices.adapter.fk.regmedcert;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygResponseType;
import se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v3.RegisterMedicalCertificateResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ErrorIdEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;
import se.skl.skltpservices.adapter.common.processor.FkAdapterUtil;

public class Fk2VardTransformer extends AbstractMessageTransformer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public Fk2VardTransformer() {
		super();
		registerSourceType(DataTypeFactory.create(Object.class));
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		logger.info("Entering fk2vard register medical certificate transform");

		boolean faultDetected = false;
		boolean validationErrorDetected = false;

		Object src = message.getPayload();
		try {
			// Take care of any error message and send it back as a SOAP Fault!
			if (src instanceof NullPayload) {
				// We got a null-payload, let's see if there is an
				// exception-payload instead...
				ExceptionPayload ep = message.getExceptionPayload();
				if (ep != null) {
					String errorMessage = ep.getCode() + ": " + ep.getMessage();

					Throwable t = ep.getException();
					if (t instanceof ResponseTimeoutException) {
						src = errorMessage + ". Timeout.";
					} else {
						// Check if we have a validation error!
						if (errorMessage.contains("Validation error")) {
							validationErrorDetected = true;
							src = ep.getMessage();
						} else {
							src = errorMessage + ". Unknown error.";
						}
					}

					// Remove the ExceptionPayload!
					message.setExceptionPayload(null);
				}
				faultDetected = true;
			} else if (!(src instanceof TaEmotLakarintygResponseType)) {
				src = "Payload type not supported: " + message.getPayload().getClass();
				faultDetected = true;
			}

			StringBuffer result = new StringBuffer();

			// First create the content in the body, either a fault or the
			// response
			if (faultDetected && !validationErrorDetected) {
				// Strip off xml processing instructions if any
				String payload = (String) src;
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				result.append(FkAdapterUtil.generateErrorCallingServiceProducerSoapFaultWithCause(payload));
			} else {
				// Create new JAXB object for the outgoing data
				RegisterMedicalCertificateResponseType outResponse = new RegisterMedicalCertificateResponseType();
				ResultOfCall resultOfCall = new ResultOfCall();
				outResponse.setResult(resultOfCall);

				if (validationErrorDetected) {
					resultOfCall.setResultCode(ResultCodeEnum.ERROR);
					resultOfCall.setErrorId(ErrorIdEnum.VALIDATION_ERROR);
					resultOfCall.setErrorText((String) src);
				} else {
					TaEmotLakarintygResponseType inResponse = (TaEmotLakarintygResponseType) src;
					if (inResponse != null) {
						resultOfCall.setResultCode(ResultCodeEnum.OK);
					} else {
						resultOfCall.setResultCode(ResultCodeEnum.ERROR);
						resultOfCall.setErrorId(ErrorIdEnum.TECHNICAL_ERROR);
					}
				}

				// Transform the JAXB object into a XML payload
				StringWriter writer = new StringWriter();
				Marshaller marshaller = JAXBContext.newInstance(RegisterMedicalCertificateResponseType.class)
						.createMarshaller();
				marshaller.marshal(new JAXBElement(new QName(
						"urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificateResponder:3",
						"RegisterMedicalCertificateResponse"), RegisterMedicalCertificateResponseType.class,
						outResponse), writer);
				logger.debug("Extracted information: {}", writer.toString());
				String payload = (String) writer.toString();
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}
				writer.close();
				result.append(payload);
			}

			// Done, return the string
			String resultStr = result.toString();
			logger.debug("Return SOAP Body: {}", resultStr);

			logger.info("Exiting fk2vard register medical certificate transform");
			return resultStr;

		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}
}