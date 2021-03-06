package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.itintegration.engagementindex._1.EngagementType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;
import se.riv.interoperability.headers.v1.ActorType;
import se.riv.interoperability.headers.v1.ActorTypeEnum;
import se.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;

public class TidbokningRequestListTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(TidbokningRequestListTransformer.class);

    /**
     * Message aware transformer that ...
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

        // Perform any message aware processing here, otherwise delegate as much as possible to pojoTransform() for easier unit testing
    	List<Object[]> transformedPayload = pojoTransform(message.getPayload(), outputEncoding);

    	// Set the expected number of responses so that the aggregator knows when to stop, update the message payload and return the message for further processing
    	message.setCorrelationGroupSize(transformedPayload.size());
    	message.setPayload(transformedPayload);
    	return message;
    }

	/**
     * Simple pojo transformer method that can be tested with plain unit testing...
	 */
	protected List<Object[]> pojoTransform(Object src, String encoding) throws TransformerException {

		FindContentResponseType inResp = (FindContentResponseType)src;
		List<EngagementType> inEngagements = inResp.getEngagement();
		
		log.info("Got {} hits in the engagement index", inEngagements.size());

		// Since we are using the GetSubjectOfCareSchedule that returns all bookings from a logical-address in one call we can reduce multiple hits in the index for the same logical-address to lower the number of calls required
		Map<String, String> uniqueLogicalAddresses = new HashMap<String, String>();
		for (EngagementType inEng : inEngagements) {
			uniqueLogicalAddresses.put(inEng.getLogicalAddress(), inEng.getRegisteredResidentIdentification());
		}


		// Prepare the result of the transformation as a list of request-payloads, 
		// one payload for each unique logical-address from the Set uniqueLogicalAddresses,
		// each payload built up as an object-array according to the JAXB-signature for the method in the service interface
		List<Object[]> reqList = new ArrayList<Object[]>();
		
		for (Entry<String, String> entry : uniqueLogicalAddresses.entrySet()) {

			String logicalAdress = entry.getKey();
			String subjectOfCare = entry.getValue();

			log.info("Calling source system using logical address {} for subject of care id {}", logicalAdress, subjectOfCare);

			// FIX ME. Get Actor from some invocation variable
			ActorType actor = new ActorType();
			actor.setActorId("999");
			actor.setActorType(ActorTypeEnum.SUBJECT_OF_CARE);

			GetSubjectOfCareScheduleType request = new GetSubjectOfCareScheduleType();
			request.setHealthcareFacility(logicalAdress);
			request.setSubjectOfCare(subjectOfCare);

			Object[] reqArr = new Object[] {logicalAdress, actor, request};
			
			reqList.add(reqArr);
		}

		log.debug("Transformed payload: {}", reqList);

		return reqList;
	}
}