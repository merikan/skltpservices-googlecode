package se.skltp.agp.tidbokning;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;

import se.skltp.aggregatingservices.riv.crm.scheduling.getsubjectofcareschedule.QueryObjectFactoryImpl;
import se.skltp.agp.service.api.QueryObjectFactory;


public class QueryObjectFactoryTest {

	private QueryObjectFactory testObject = new QueryObjectFactoryImpl();
	
	@Test
	@Ignore
	public void testQueryObjectFactory() {
		
		Node node = null;
		testObject.createQueryObject(node);
		assertEquals("expected", "actual");
	}
}
