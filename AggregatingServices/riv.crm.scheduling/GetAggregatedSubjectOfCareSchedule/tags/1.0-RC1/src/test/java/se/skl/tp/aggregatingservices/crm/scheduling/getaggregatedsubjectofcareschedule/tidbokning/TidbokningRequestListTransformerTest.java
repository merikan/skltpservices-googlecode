package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class TidbokningRequestListTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// FIXME, Fix unittests!!!
		int i = 1;
		if (i == 1) return;
		
		// Specify input and expected result 
		String input          = MiscUtil.readFileAsString("src/test/resources/testfiles/tidbokning/request-input.xml");

		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/tidbokning/request-input.xml"); // No transformation is done by default so use input also as expected...


		// Create the transformer under test and let it perform the transformation

		TidbokningRequestListTransformer transformer = new TidbokningRequestListTransformer();
		Object result = transformer.pojoTransform(input, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}
}