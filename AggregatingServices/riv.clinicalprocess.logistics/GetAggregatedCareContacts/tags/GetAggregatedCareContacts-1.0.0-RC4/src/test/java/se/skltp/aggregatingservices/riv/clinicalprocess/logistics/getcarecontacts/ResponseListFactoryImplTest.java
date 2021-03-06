package se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.clinicalprocess.logistics.getcarecontactsresponder.v2.GetCareContactsResponseType;
import se.riv.clinicalprocess.logistics.v2.CareContactType;
import se.skltp.aggregatingservices.riv.clinicalprocess.logistics.getcarecontacts.ResponseListFactoryImpl;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObject;

public class ResponseListFactoryImplTest {
    
    private static final JaxbUtil jaxbUtil = new JaxbUtil(GetCareContactsResponseType.class, ProcessingStatusType.class);
    
    @Test
    public void getXmlFromAggregatedResponse(){
        FindContentType fc = new FindContentType();     
        fc.setRegisteredResidentIdentification("1212121212");
        QueryObject queryObject = new QueryObject(fc, null);
        List<Object> responseList = new ArrayList<Object>(2);
        responseList.add(createGetCareContactsResponse());
        responseList.add(createGetCareContactsResponse());
        ResponseListFactoryImpl responseListFactory = new ResponseListFactoryImpl();
        
        String responseXML = responseListFactory.getXmlFromAggregatedResponse(queryObject, responseList);
        GetCareContactsResponseType response = (GetCareContactsResponseType)jaxbUtil.unmarshal(responseXML);
        assertEquals(2, response.getCareContact().size());
    }
    
    private GetCareContactsResponseType createGetCareContactsResponse(){
        GetCareContactsResponseType getCareDocResponse = new GetCareContactsResponseType();
        getCareDocResponse.getCareContact().add(new CareContactType());
        return getCareDocResponse;
    }
}
