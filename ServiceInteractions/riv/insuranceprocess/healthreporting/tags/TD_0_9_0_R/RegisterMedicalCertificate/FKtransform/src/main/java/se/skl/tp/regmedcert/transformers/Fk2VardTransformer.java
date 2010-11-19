package se.skl.tp.regmedcert.transformers;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.fk.vardgivare.sjukvard.taemotlakarintygresponder.v1.TaEmotLakarintygResponseType;
import se.skl.riv.insuranceprocess.healthreporting.registermedicalcertificateresponder.v2.RegisterMedicalCertificateResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v1.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v1.ResultOfCall;



public class Fk2VardTransformer extends AbstractMessageAwareTransformer
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public Fk2VardTransformer()
    {
        super();
        registerSourceType(Object.class);
        setReturnClass(Object.class);
    }
    
	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {

		boolean faultDetected = false;
		
    	Object src = message.getPayload();
		try {						
			// Take care of any error message and send it back as a SOAP Fault!
			if (src instanceof NullPayload) {
			    // We got a null-payload, let's see if there is an exception-payload instead...
		        ExceptionPayload ep = message.getExceptionPayload();
		        if (ep != null) {
		            String errorMessage = ep.getCode() + ": " + ep.getMessage();

		            Throwable t = ep.getException();
		            if (t instanceof ResponseTimeoutException) {
		            	src = errorMessage + ". Timeout.";
		            } else {
		                src = errorMessage + ". Unknown error.";
		            }
		            
		            // Remove the ExceptionPayload!
		            message.setExceptionPayload(null);
		        }				
	            faultDetected = true;
			} else if(!(src instanceof TaEmotLakarintygResponseType)) {
				src = "Payload type not supported: "+ message.getPayload().getClass();
				faultDetected = true;
			}

//			QName name = getRootElementQName(src);

			StringBuffer result = new StringBuffer();
			
			// First create the content in the body, either a fault or the response
			if (faultDetected) {
				// Strip off xml processing instructions if any
				String payload = (String)src;
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				createSoapFault(payload, result);
			} else {
	            TaEmotLakarintygResponseType inResponse = (TaEmotLakarintygResponseType)src;

	            // Create new JAXB object for the outgoing data
	            RegisterMedicalCertificateResponseType outResponse = new RegisterMedicalCertificateResponseType();
	            
	            ResultOfCall resultOfCall = new ResultOfCall();
	            
	            // Check result
	            if (inResponse != null) {
	            	resultOfCall.setResultCode(ResultCodeEnum.OK);
	            	outResponse.setResult(resultOfCall);
	            }
	            
	            // If payload already is a SoapFault How to use marshalling?
	            
				// Transform the JAXB object into a XML payload
	            StringWriter writer = new StringWriter();
	        	Marshaller marshaller = JAXBContext.newInstance(RegisterMedicalCertificateResponseType.class).createMarshaller();
	        	marshaller.marshal(new JAXBElement(new QName("urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificateResponder:2", "RegisterMedicalCertificateResponse"), RegisterMedicalCertificateResponseType.class, outResponse), writer);
				logger.debug("Extracted information: {}", writer.toString());
				String payload = (String)writer.toString();
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				result.append(payload);
			}

			// Done, return the string
			String resultStr = result.toString();
			logger.debug("Return SOAP Body: {}", resultStr);
			return resultStr;

		} catch (Exception e) {
	        throw new TransformerException(this, e);
		}
	}
			
	private void createSoapFault(String errorText, StringBuffer result) {
		result.append("<?xml version='1.0' encoding='UTF-8'?>");
		result.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		result.append("<soap:Body>");
		result.append("<soap:Fault>");
		result.append("<faultcode>soap:Server</faultcode>");
		result.append("<faultstring>VP009 Exception when calling the service producer: " + errorText + "</faultstring>");
		result.append("</soap:Fault>");
		result.append("</soap:Body>");
		result.append("</soap:Envelope>");
	}
}