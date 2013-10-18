<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated mapFile="xslt/MapProfdocHISMessageToMvkRiv_CancelBooking_res.map" md5sum="74272eb6c7c7a850bf26a70486485fbd" version="7.0.400" -->
<!--
*****************************************************************************
*   This file has been generated by the IBM XML Mapping Editor V7.0.400
*
*   Mapping file:		MapProfdocHISMessageToMvkRiv_CancelBooking_res.map
*   Map declaration(s):	MapProfdocHISMessageToMvkRiv_CancelBooking_res
*   Input file(s):		smo://smo/name%3Dwsdl-primary/message%3D%257Burn%253Amvk%253Aasb%253Atakecare%253Av100.cancelBookingInternalInterface%257DinvokeTakeCareResponseMsg/xpath%3D%252Fbody/smo.xsd
*   Output file(s):		smo://smo/name%3Dwsdl-primary/message%3D%257Burn%253Amvk%253Aasb%253Atidbok%253Ainteraction%253ACancelBooking%253A1%253A0%257DCancelBookingResponse/xpath%3D%252Fbody/smo.xsd
*
*   Note: Do not modify the contents of this file as it is overwritten
*         each time the mapping model is updated.
*****************************************************************************
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:str="http://exslt.org/strings"
    xmlns:set="http://exslt.org/sets"
    xmlns:math="http://exslt.org/math"
    xmlns:exsl="http://exslt.org/common"
    xmlns:date="http://exslt.org/dates-and-times"
    xmlns:in2="wsdl.urn:mvk:asb:takecare:v100.cancelBookingInternalInterface"
    xmlns:in5="urn:ProfdocHISMessage:CancelBooking:Response"
    xmlns:in="urn:mvk:asb:takecare:v100:TakeCareCommonAttribute"
    xmlns:in3="urn:mvk:asb:takecare:v100.cancelBookingInternalInterface"
    xmlns:in4="urn:ProfdocHISMessage:Error"
    xmlns:out="urn:mvk:asb:tidbok:CancelBookingResponder:1:0"
    xmlns:io="http://www.w3.org/2003/05/soap-envelope"
    xmlns:io3="http://www.ibm.com/xmlns/prod/websphere/mq/sca/6.0.0"
    xmlns:io2="http://www.ibm.com/websphere/sibx/smo/v6.0.1"
    xmlns:io4="http://schemas.xmlsoap.org/ws/2004/08/addressing"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:out2="urn:mvk:asb:tidbok:1:0"
    xmlns:io5="http://www.ibm.com/xmlns/prod/websphere/http/sca/6.1.0"
    xmlns:out3="urn:mvk:asb:common:base:1"
    xmlns:xsd4xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:io6="http://www.w3.org/2005/08/addressing"
    xmlns:out4="wsdl.urn:mvk:asb:tidbok:interaction:CancelBooking:1:0"
    xmlns:XmlHandler="xalan://se.modul1.mvk.asb.timebooking.takecare.java.XmlHandler"
    xmlns:map="http://Mvk_Scheduling_TakeCare_Adapter/xslt/MapProfdocHISMessageToMvkRiv_CancelBooking_res"
    xmlns:msl="http://www.ibm.com/xmlmap"
    exclude-result-prefixes="set in msl math exsl in2 in3 date in4 in5 xalan str XmlHandler map"
    version="1.0">
  <xsl:output method="xml" encoding="UTF-8" indent="no"/>

  <!-- root wrapper template  -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="msl:datamap">
        <msl:datamap>
          <dataObject>
            <xsl:attribute name="xsi:type">
              <xsl:value-of select="'out4:CancelBookingResponse'"/>
            </xsl:attribute>
            <xsl:call-template name="map:MapProfdocHISMessageToMvkRiv_CancelBooking_res2">
              <xsl:with-param name="body" select="msl:datamap/dataObject[1]"/>
            </xsl:call-template>
          </dataObject>
        </msl:datamap>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="body" mode="map:MapProfdocHISMessageToMvkRiv_CancelBooking_res"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- This rule represents an element mapping: "body" to "body".  -->
  <xsl:template match="body"  mode="map:MapProfdocHISMessageToMvkRiv_CancelBooking_res">
    <body>
      <xsl:attribute name="xsi:type">
        <xsl:value-of select="'out4:CancelBookingResponse'"/>
      </xsl:attribute>
      <out:CancelBookingResponse>
        <!-- a simple mapping with no associated source:  to "out:resultCode"(ResultCodeEnum) -->
        <out:resultCode>
          <xsl:text>OK</xsl:text>
        </out:resultCode>
        <!-- a simple mapping with no associated source:  to "out:resultText"(string) -->
        <out:resultText>
          <xsl:value-of select="XmlHandler:setEmptyStringInXmlElement()"/>
        </out:resultText>
      </out:CancelBookingResponse>
    </body>
  </xsl:template>

  <!-- This rule represents a type mapping: "body" to "body".  -->
  <xsl:template name="map:MapProfdocHISMessageToMvkRiv_CancelBooking_res2">
    <xsl:param name="body"/>
    <out:CancelBookingResponse>
      <!-- a simple mapping with no associated source:  to "out:resultCode"(ResultCodeEnum) -->
      <out:resultCode>
        <xsl:text>OK</xsl:text>
      </out:resultCode>
      <!-- a simple mapping with no associated source:  to "out:resultText"(string) -->
      <out:resultText>
        <xsl:value-of select="XmlHandler:setEmptyStringInXmlElement()"/>
      </out:resultText>
    </out:CancelBookingResponse>
  </xsl:template>

  <!-- *****************    Utility Templates    ******************  -->
  <!-- copy the namespace declarations from the source to the target -->
  <xsl:template name="copyNamespaceDeclarations">
    <xsl:param name="root"/>
    <xsl:for-each select="$root/namespace::*">
      <xsl:copy/>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>