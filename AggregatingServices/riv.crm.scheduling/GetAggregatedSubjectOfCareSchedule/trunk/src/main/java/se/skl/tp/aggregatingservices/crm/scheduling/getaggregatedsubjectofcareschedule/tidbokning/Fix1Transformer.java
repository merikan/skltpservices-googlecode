package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fix1Transformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(Fix1Transformer.class);

    /**
     * Message aware transformer that ...
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

    	String logicalAddress = message.getInvocationProperty("logical-address");
    	
//    	message.setOutboundProperty("logical-address", logicalAddress);
//    	System.err.println("java-out-p: " + message.getOutboundProperty("logical-address"));
    	return new Object[] {logicalAddress, message.getPayload()};
	}
}