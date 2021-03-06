package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.stax.MapNamespaceContext;
import org.mule.module.xml.stax.ReversibleXMLStreamReader;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.riv.crm.scheduling.getsubjectofcarescheduleresponder.v1.GetSubjectOfCareScheduleType;

public class GetSubjectOfCareIdTransformer extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetSubjectOfCareIdTransformer.class);
	private static final JaxbUtil ju = new JaxbUtil(GetSubjectOfCareScheduleType.class);
	private static final Map<String, String> namespaceMap = new HashMap<String, String>();
	
	static {
		namespaceMap.put("soap",    "http://schemas.xmlsoap.org/soap/envelope/");
		namespaceMap.put("it-int",  "urn:riv:itintegration:registry:1");
		namespaceMap.put("interop", "urn:riv:interoperability:headers:1");
		namespaceMap.put("service", "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");
	}
	
    /**
     * Message aware transformer that ...
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

        // Perform any message aware processing here, otherwise delegate as much as possible to pojoTransform() for easier unit testing
        return pojoTransform(message.getPayload(), outputEncoding);
    }

	/**
     * Simple pojo transformer method that can be tested with plain unit testing...
	 */
	protected Object pojoTransform(Object src, String encoding) throws TransformerException {


		String xml = XmlUtil.convertReversibleXMLStreamReaderToString((ReversibleXMLStreamReader)src, "UTF-8");
		log.debug("Transforming payload: {}", xml);

		Object result;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
		    xpath.setNamespaceContext(new MapNamespaceContext(namespaceMap));

//			XPathExpression xpathLogicalAddress = xpath.compile("/soap:Envelope/soap:Header/it-int:LogicalAddress");
//			XPathExpression xpathActor = xpath.compile("/soap:Envelope/soap:Header/interop:Actor");
			XPathExpression xpathRequest = xpath.compile("/soap:Envelope/soap:Body/service:GetSubjectOfCareSchedule");

			
			//			Document reqDoc = XMLUtils.toW3cDocument(xml);
			Document reqDoc = createDocument(xml, "UTF-8");
			result = xpathRequest.evaluate(reqDoc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		NodeList list = (NodeList)result; 
		Node node = list.item(0);
				
		// Lookup the fragment...
		GetSubjectOfCareScheduleType reqIn = (GetSubjectOfCareScheduleType)ju.unmarshal(node);
		
		String subjectofCareId = reqIn.getSubjectOfCare();
		
		log.debug("Transformed payload: pid: {}", subjectofCareId);
		
		return subjectofCareId;
	}

	private Document createDocument(String content, String charset) {
		try {
			InputStream is = new ByteArrayInputStream(content.getBytes(charset));
			return getBuilder().parse(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DocumentBuilder getBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		return builder;
	}
}