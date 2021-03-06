/**
* Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.riv.ehr.accesscontrol.assertcareengagement;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3.wsaddressing10.AttributedURIType;

import se.riv.ehr.accesscontrol.assertcareengagement.v1.rivtabp20.AssertCareEngagementResponderInterface;
import se.riv.ehr.accesscontrol.assertcareengagement.v1.rivtabp20.AssertCareEngagementResponderService;
import se.riv.ehr.accesscontrol.assertcareengagementresponder.v1.AssertCareEngagementResponseType;
import se.riv.ehr.accesscontrol.assertcareengagementresponder.v1.AssertCareEngagementType;

public final class AssertCareEngagementConsumer {

	// Use this one to connect via Virtualiseringsplattformen
//	private static final String LOGISK_ADDRESS = "/AssertCareEngagement/1/rivtabp20";
	// Use this one to connect directly (just for test)
	private static final String LOGISK_ADDRESS = "/AssertCareEngagement_Service";

	public static void main(String[] args) {
		// Use this one to connect via Virtualiseringsplattformen
//		String host = "localhost:21000/vp";
		// Use this one to connect directly (just for test)
		String host = "localhost:19000/test";

		if (args.length > 0) {
			host = args[0];
		}

		// Setup ssl info for the initial ?wsdl lookup...
		System.setProperty("javax.net.ssl.keyStore","../../certs/consumer.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		System.setProperty("javax.net.ssl.keyStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStore", "../../certs/truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "password");

		String adress = "https://" + host + LOGISK_ADDRESS;
		System.out.println("Consumer connecting to "  + adress);
		String p = callService(adress, "Test");
		System.out.println("Returned: " + p);
	}

	public static String callService(String serviceAddress, String logicalAddresss) {
		
		AssertCareEngagementResponderInterface service = new AssertCareEngagementResponderService(
			createEndpointUrlFromServiceAddress(serviceAddress)).getAssertCareEngagementResponderPort();

		AttributedURIType logicalAddressHeader = new AttributedURIType();
		logicalAddressHeader.setValue(logicalAddresss);

		AssertCareEngagementType request = new AssertCareEngagementType();
		
		// The below request is created to mimic the sample xml-message
		request.setCareGiverHsaIdentity("aaa");
		request.setCareUnitHsaIdentity("bbb");
		request.setPerformer("kallew");
		request.setSubjectOfCareId("ccc");
	
		try {
			AssertCareEngagementResponseType result = service.assertCareEngagement(logicalAddressHeader, request);

		    return ("AssertCareEngagement boolean response=" + result.isHasCareEngagement());

		} catch (Exception ex) {
			System.out.println("Exception=" + ex.getMessage());
			return null;
		}
	}

	public static URL createEndpointUrlFromServiceAddress(String serviceAddress) {
		try {
			return new URL(serviceAddress + "?wsdl");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}
}
