package se.skltp.aggregatingservices.riv.crm.scheduling.getaggregatedrequestactivities;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.ObjectFactory;
import se.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.service.api.ResponseListFactory;

public class ResponseListFactoryImpl implements ResponseListFactory {

	private static final Logger log = LoggerFactory.getLogger(ResponseListFactoryImpl.class);
	private static final JaxbUtil jaxbUtil = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class, ProcessingStatusType.class);
	private static final ObjectFactory OF = new ObjectFactory();
	
	@Override
	public String getXmlFromAggregatedResponse(List<Object> aggregatedResponseList) {
	    GetSubjectOfCareScheduleResponseType aggregatedResponse = new GetSubjectOfCareScheduleResponseType();

	    for (Object object : aggregatedResponseList) {
		    GetSubjectOfCareScheduleResponseType response = (GetSubjectOfCareScheduleResponseType)object;
			aggregatedResponse.getTimeslotDetail().addAll(response.getTimeslotDetail());
		}

	    if (log.isInfoEnabled()) {
    		String subjectOfCareId = "";
        	if (aggregatedResponse.getTimeslotDetail().size() > 0) {
        		subjectOfCareId = aggregatedResponse.getTimeslotDetail().get(0).getSubjectOfCare();
        	}
        	log.info("Returning {} aggregated schedules for subject of care id {}", aggregatedResponse.getTimeslotDetail().size() ,subjectOfCareId);
        }
        
        // Since the class GetSubjectOfCareScheduleResponseType don't have an @XmlRootElement annotation
        // we need to use the ObjectFactory to add it.
        return jaxbUtil.marshal(OF.createGetSubjectOfCareScheduleResponse(aggregatedResponse));
	}
}