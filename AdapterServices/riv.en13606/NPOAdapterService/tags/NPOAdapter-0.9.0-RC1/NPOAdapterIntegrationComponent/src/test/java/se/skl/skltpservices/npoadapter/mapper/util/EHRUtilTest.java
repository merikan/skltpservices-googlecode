/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
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
package se.skl.skltpservices.npoadapter.mapper.util;

import org.junit.Test;

import riv.clinicalprocess.healthcond.description._2.DatePeriodType;
import riv.clinicalprocess.healthcond.description._2.PersonIdType;
import riv.clinicalprocess.healthcond.description._2.ResultType;
import riv.clinicalprocess.healthcond.description.enums._2.ResultCodeEnum;
import riv.clinicalprocess.healthcond.description.getdiagnosisresponder._2.GetDiagnosisType;
import se.rivta.en13606.ehrextract.v11.*;
import se.skl.skltpservices.npoadapter.mapper.error.MapperException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class EHRUtilTest {
	
	private static final String TEST_CODE_SYSTEM = "1.2.752.129.2.2.2.1";
	private static final String TEST_CODE = "voo";
	
	private static final String TEST_VALUE_1 = UUID.randomUUID().toString();
	private static final String TEST_VALUE_2 = UUID.randomUUID().toString();
	private static final String TEST_VALUE_3 = UUID.randomUUID().toString();
	private static final String TEST_VALUE_4 = UUID.randomUUID().toString();
	private static final String TEST_VALUE_5 = UUID.randomUUID().toString();
	
	private static final int TEST_INT_VALUE_1 = 500;


    @Test
    public void testGenericMapping() {
        final PersonIdType personIdType1 = new PersonIdType();
        personIdType1.setType("1234");
        personIdType1.setId("id");
        final II ii = EHRUtil.iiType(personIdType1);
        assertEquals(personIdType1.getId(), ii.getExtension());
        assertEquals(personIdType1.getType(), ii.getRoot());

        riv.clinicalprocess.healthcond.actoutcome._3.PersonIdType personIdType2 = new riv.clinicalprocess.healthcond.actoutcome._3.PersonIdType();
        personIdType2.setType(personIdType1.getType());
        personIdType2.setId(personIdType1.getId());
        final II ii2 = EHRUtil.iiType(personIdType1);
        assertEquals(personIdType2.getId(), ii2.getExtension());
        assertEquals(personIdType2.getType(), ii2.getRoot());

        final ResponseDetailType detail = new ResponseDetailType();

        detail.setText(createST("value"));
        detail.setTypeCode(ResponseDetailTypeCodes.I);

        final ResultType resultType = EHRUtil.resultType("logId", Arrays.asList(detail), ResultType.class);

        assertEquals(detail.getText().getValue(), resultType.getMessage());
        assertEquals(ResultCodeEnum.INFO, resultType.getResultCode());
        assertEquals("logId", resultType.getLogId());


    }

	@Test
	public void testIntType() {
		INT intType = EHRUtil.intType(TEST_INT_VALUE_1);
		assertEquals(TEST_INT_VALUE_1, intType.getValue().intValue());
	}
		
	@Test
	public void testTsType() {
		TS ts = EHRUtil.tsType(TEST_VALUE_1);
		assertEquals(ts.getValue(), TEST_VALUE_1);
		ts = EHRUtil.tsType(null);
		assertNull(ts);
	}

	@Test
	public void testGetElementTextValue() throws Exception {
		ELEMENT e = new ELEMENT();
		e.setValue(new ST());
		((ST)e.getValue()).setValue(TEST_VALUE_1);
		assertEquals(TEST_VALUE_1, EHRUtil.getElementTextValue(e));
	}

	@Test
	public void testStType() throws Exception {
		assertNull(EHRUtil.stType(null));
		assertEquals(TEST_VALUE_1, EHRUtil.stType(TEST_VALUE_1).getValue());
	}

	@Test
	public void testGetPartValue() throws Exception {
		List<EN> list = new ArrayList<EN>();
		list.add(new EN());
		list.get(0).getPart().add(new ENXP());
		list.get(0).getPart().get(0).setValue(TEST_VALUE_2);
		assertEquals(TEST_VALUE_2, EHRUtil.getPartValue(list));
	}

	@Test
	public void testFirstItem() throws Exception {
		List<String> list = new ArrayList<String>();
		list.add(TEST_VALUE_1);
		list.add(TEST_VALUE_2);
		list.add(TEST_VALUE_3);
		assertEquals(TEST_VALUE_1, EHRUtil.firstItem(list));
	}

	@Test
	public void testGetCDCode() throws Exception {
		final CD cd = new CD();
		cd.setCode(TEST_VALUE_1);
		assertEquals(TEST_VALUE_1, EHRUtil.getCDCode(cd));
	}

	@Test
	public void testLookupDemographicIdentity() throws Exception {
		List<IDENTIFIEDENTITY> list = new ArrayList<IDENTIFIEDENTITY>();
		final String HSA_ID = TEST_VALUE_1;
		list.add(new PERSON());
		list.add(new ORGANISATION());
		list.add(new SOFTWAREORDEVICE());
		list.get(0).setExtractId(new II());
		list.get(0).getExtractId().setExtension(TEST_VALUE_2);
		list.get(1).setExtractId(new II());
		list.get(1).getExtractId().setExtension(HSA_ID);
		list.get(2).setExtractId(new II());
		list.get(2).getExtractId().setExtension(TEST_VALUE_3);
		assertEquals(HSA_ID, EHRUtil.lookupDemographicIdentity(list, HSA_ID).getExtractId().getExtension());
	}

	@Test
	public void testCreateParameter() throws Exception {
		ParameterType param = EHRUtil.createParameter(TEST_VALUE_1, TEST_VALUE_2);
		assertEquals(TEST_VALUE_1, param.getName().getValue());
		assertEquals(TEST_VALUE_2, param.getValue().getValue());
	}

	@Test
	public void testFindElement() throws Exception {
		List<CONTENT> list = new ArrayList<CONTENT>();
		list.add(new ENTRY());
		list.add(new SECTION());
		list.add(new ENTRY());
		((ENTRY)list.get(0)).getItems().add(new ELEMENT());
		((ENTRY)list.get(2)).getItems().add(new ELEMENT());
		((ENTRY)list.get(0)).getItems().get(0).setMeaning(createCD(TEST_VALUE_1));
		((ENTRY)list.get(2)).getItems().get(0).setMeaning(createCD(TEST_VALUE_2));
		assertEquals(TEST_VALUE_1, EHRUtil.findEntryElement(list, TEST_VALUE_1).getMeaning().getCode());
	}
	
	private CD createCD(final String code) {
		final CD cd = new CD();
		cd.setCode(code);
		return cd;
	}

    private ST createST(final String value) {
        final ST st = new ST();
        st.setValue(value);
        return st;
    }

    //
    private GetDiagnosisType createDiagnosisTestRequest() {
        GetDiagnosisType req = new GetDiagnosisType();
        req.setPatientId(new PersonIdType());
        req.getPatientId().setId("PatientIdUnitTest");
        req.getPatientId().setType("PatientTypeUnitTest");
        req.setSourceSystemHSAId("SourceSystemHSAUnitTest");
        req.setTimePeriod(new DatePeriodType());
        req.getTimePeriod().setEnd("EndUnitTest");
        req.getTimePeriod().setStart("StartUnitTest");
        req.getCareUnitHSAId().add("CareUnitHSAIdUnitTest");
        return req;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequestWithTooManyHSAId() throws MapperException {
        final CD purpose = createCD("codeUnitTest");
        purpose.setCodeSystem("codeSystemUnitTest");

        final GetDiagnosisType req = createDiagnosisTestRequest();

        req.getCareUnitHSAId().add("anotherCareUnitHSAIdUnitTest");
        final RIV13606REQUESTEHREXTRACTRequestType reqOut = EHRUtil.requestType(req, purpose);
    }

    @Test
    public void testRequestWithNoneHSAId() throws MapperException {
        final CD purpose = createCD("codeUnitTest");
        purpose.setCodeSystem("codeSystemUnitTest");


        final GetDiagnosisType req = createDiagnosisTestRequest();
        req.getCareUnitHSAId().clear();
        final RIV13606REQUESTEHREXTRACTRequestType reqOut = EHRUtil.requestType(req, purpose);
        assertEquals(1, reqOut.getParameters().size());
    }

    @Test
	public void testRequestType() throws Exception {
        final CD purpose = createCD("codeUnitTest");
        purpose.setCodeSystem("codeSystemUnitTest");

        final GetDiagnosisType req = createDiagnosisTestRequest();

		final RIV13606REQUESTEHREXTRACTRequestType reqOut = EHRUtil.requestType(req, purpose);

		assertEquals(reqOut.getMeanings().get(0).getCode(), purpose.getCode());
		assertEquals(reqOut.getMeanings().get(0).getCodeSystem(), purpose.getCodeSystem());
		assertEquals(reqOut.getSubjectOfCareId().getRoot(), req.getPatientId().getType());
		assertEquals(reqOut.getSubjectOfCareId().getExtension(), req.getPatientId().getId());
		assertEquals(reqOut.getTimePeriod().getHigh().getValue(), req.getTimePeriod().getEnd());
		assertEquals(reqOut.getTimePeriod().getLow().getValue(), req.getTimePeriod().getStart());
		assertEquals(2, reqOut.getParameters().size());
		for(ParameterType param : reqOut.getParameters()) {
			if(param.getName().getValue().equals("version")) {
				assertEquals("1.1", param.getValue().getValue());
			} else if(param.getName().getValue().equals("hsa_id")) {
				assertEquals(req.getCareUnitHSAId().get(0), param.getValue().getValue());
			} else {
				fail(String.format("Expected paramaters are version or hsa_id got: <%s>", param.getName().getValue()));
			}
		}
	}
    
}
