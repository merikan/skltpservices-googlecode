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
package se.skl.skltpservices.npoadapter.mapper;

import org.junit.Test;
import riv.ehr.patientsummary._1.*;
import riv.ehr.patientsummary._1.CD;
import riv.ehr.patientsummary._1.COMPOSITION;
import riv.ehr.patientsummary._1.EHREXTRACT;
import riv.ehr.patientsummary._1.ENTRY;
import riv.ehr.patientsummary._1.FUNCTIONALROLE;
import riv.ehr.patientsummary._1.II;
import riv.ehr.patientsummary._1.INT;
import riv.ehr.patientsummary._1.IVLTS;
import riv.ehr.patientsummary._1.SECTION;
import riv.ehr.patientsummary._1.ST;
import riv.ehr.patientsummary._1.TS;
import riv.ehr.patientsummary.getehrextractresponder._1.*;
import riv.ehr.patientsummary.getehrextractresponder._1.ParameterType;
import se.skl.skltpservices.npoadapter.test.Util;

import static org.junit.Assert.assertEquals;

/**
 * Created by Peter on 2014-08-15.
 */
public class EhrResponseMappingTest {

    @Test
    public void testEhrExtractMapping() {
        se.rivta.en13606.ehrextract.v11.EHREXTRACT baseline = Util.loadEhrTestData(Util.CARECONTACS_TEST_FILE);

        EHREXTRACT target = AbstractMapper.dozerBeanMapper.map(baseline, EHREXTRACT.class);
    }

    @Test
    public void testEhrResponseMapping() {

        final GetEhrExtractResponseType ehrExtractResponseType = new GetEhrExtractResponseType();

        final EHREXTRACT rivExtract = new EHREXTRACT();

        ehrExtractResponseType.getEhrExtract().add(rivExtract);

        rivExtract.setEhrSystem(createII());
        rivExtract.setAuthorisingParty(createII());
        rivExtract.setSubjectOfCare(createII());
        rivExtract.setTimeCreated(createTS("20140721150000"));

        final COMPOSITION composition = new COMPOSITION();
        rivExtract.getAllCompositions().add(composition);

        composition.setName(createST("name"));
        composition.setContributionId(createII());
        composition.setSessionTime(createIVLTS());
        composition.setContributionId(createII());

        final SECTION section = new SECTION();
        composition.getContent().add(section);
        section.setName(createST("section a"));
        section.setMeaning(createCD());
        section.setRcId(createII());
        section.setSensitivity(createINT());

        final ENTRY entry = new ENTRY();
        composition.getContent().add(entry);
        entry.setName(createST("section b"));
        entry.setMeaning(createCD());
        entry.setRcId(createII());
        entry.setSensitivity(createINT());

        final FUNCTIONALROLE functionalrole = new FUNCTIONALROLE();
        entry.setInfoProvider(functionalrole);

        functionalrole.setFunction(createCD());
        functionalrole.setPerformer(createII());

        se.rivta.en13606.ehrextract.v11.EHREXTRACT ehrExtract = AbstractMapper.dozerBeanMapper.map(rivExtract, se.rivta.en13606.ehrextract.v11.EHREXTRACT.class);

        assertEquals(rivExtract.getEhrSystem().getExtension(), ehrExtract.getEhrSystem().getExtension());

        assertEquals(1, ehrExtract.getAllCompositions().size());
        assertEquals(rivExtract.getAllCompositions().get(0).getContributionId().getExtension(), ehrExtract.getAllCompositions().get(0).getContributionId().getExtension());

    }

    //
    static ParameterType createParameter(String name, String value) {
        final ParameterType parameterType = new ParameterType();
        parameterType.setName(createST(name));
        parameterType.setValue(createST(value));

        return parameterType;
    }

    static ST createST(String value) {
        final ST val = new ST();
        val.setValue(value);
        return val;
    }

    static INT createINT() {
        final INT val = new INT();

        val.setValue(100);

        return val;
    }

    static CD createCD() {
        final CD val = new CD();
        val.setCode("voo");
        return val;
    }

    static TS createTS(String ts) {
        final TS value = new TS();
        value.setValue(ts);
        return value;
    }

    static IVLTS createIVLTS() {
        final IVLTS val = new IVLTS();
        val.setHigh(createTS("20080520"));
        val.setLow(createTS("20080101"));
        return val;
    }

    static II createII() {
        final II val = new II();
        val.setRoot("191212121212");
        val.setExtension("1.2.752.129.2.1.3");
        return val;
    }
}