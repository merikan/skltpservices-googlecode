<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated mapFile="xslt/MapProfdocHISMessageGetTimeTypesToMVKInternalIF.map" md5sum="c71c6bb350df7771196d643948690442" version="7.0.400" -->
<!--
*****************************************************************************
*   This file has been generated by the IBM XML Mapping Editor V7.0.400
*
*   Mapping file:		MapProfdocHISMessageGetTimeTypesToMVKInternalIF.map
*   Map declaration(s):	MapProfdocHISMessageGetTimeTypesToMVKInternalIF
*   Input file(s):		smo://smo/name%3Dwsdl-primary/transientContext%3D%257Burn%253Amvk%253Aasb%253Atakecare%253Av100%253ATakeCareServiceBO%257DTakeCareServiceBO/message%3D%257Burn%253Amvk%253Aasb%253Atakecare%253Av100%253AgetTimeTypesInternalInterface%257DinvokeTakeCareResponseMsg/xpath%3D%252Fbody/smo.xsd
*   Output file(s):		smo://smo/name%3Dwsdl-primary/transientContext%3D%257Burn%253Amvk%253Aasb%253Atakecare%253Av100%253ATakeCareServiceBO%257DTakeCareServiceBO/message%3D%257Burn%253Amvk%253Aasb%253Atidbok%253Ainteraction%253AGetAllTimeTypes%253A1%253A0%257DGetAllTimeTypesResponse/xpath%3D%252Fbody/smo.xsd
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
    xmlns:in="urn:mvk:asb:takecare:v100:TakeCareCommonAttribute"
    xmlns:in2="wsdl.urn:mvk:asb:takecare:v100:getTimeTypesInternalInterface"
    xmlns:in3="urn:ProfdocHISMessage:GetTimeTypes:Response"
    xmlns:in4="urn:ProfdocHISMessage:Error"
    xmlns:in5="urn:mvk:asb:takecare:v100:getTimeTypesInternalInterface"
    xmlns:out="wsdl.urn:mvk:asb:tidbok:interaction:GetAllTimeTypes:1:0"
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
    xmlns:out4="urn:mvk:asb:tidbok:GetAllTimeTypesResponder:1:0"
    xmlns:map="http://MvkAsb_Timebooking_TakeCare_Adapter/xslt/MapProfdocHISMessageGetTimeTypesToMVKInternalIF"
    xmlns:msl="http://www.ibm.com/xmlmap"
    exclude-result-prefixes="set in msl math exsl in2 in3 date in4 in5 xalan str map"
    version="1.0">
  <xsl:output method="xml" encoding="UTF-8" indent="no"/>

  <!-- root wrapper template  -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="msl:datamap">
        <msl:datamap>
          <dataObject>
            <xsl:attribute name="xsi:type">
              <xsl:value-of select="'out:GetAllTimeTypesResponse'"/>
            </xsl:attribute>
            <xsl:call-template name="map:MapProfdocHISMessageGetTimeTypesToMVKInternalIF2">
              <xsl:with-param name="body" select="msl:datamap/dataObject[1]"/>
            </xsl:call-template>
          </dataObject>
        </msl:datamap>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="body" mode="map:MapProfdocHISMessageGetTimeTypesToMVKInternalIF"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- This rule represents an element mapping: "body" to "body".  -->
  <xsl:template match="body"  mode="map:MapProfdocHISMessageGetTimeTypesToMVKInternalIF">
    <body>
      <xsl:attribute name="xsi:type">
        <xsl:value-of select="'out:GetAllTimeTypesResponse'"/>
      </xsl:attribute>
      <out4:GetAllTimeTypesResponse>
        <!-- a for-each transform: "TimeType"(<Anonymous>) to "out4:ListOfTimeTypes"(TimeTypeType) -->
        <xsl:apply-templates select="in5:invokeTakeCareResponse/in3:ProfdocHISMessage/TimeTypes/TimeType" mode="localTimeTypeToListOfTimeTypes_586935755"/>
      </out4:GetAllTimeTypesResponse>
    </body>
  </xsl:template>

  <!-- This rule represents a type mapping: "body" to "body".  -->
  <xsl:template name="map:MapProfdocHISMessageGetTimeTypesToMVKInternalIF2">
    <xsl:param name="body"/>
    <out4:GetAllTimeTypesResponse>
      <!-- a for-each transform: "TimeType"(<Anonymous>) to "out4:ListOfTimeTypes"(TimeTypeType) -->
      <xsl:apply-templates select="$body/in5:invokeTakeCareResponse/in3:ProfdocHISMessage/TimeTypes/TimeType" mode="localTimeTypeToListOfTimeTypes_586935755"/>
    </out4:GetAllTimeTypesResponse>
  </xsl:template>

  <!-- This rule represents a for-each transform: "TimeType" to "out4:ListOfTimeTypes".  -->
  <xsl:template match="TimeType"  mode="localTimeTypeToListOfTimeTypes_586935755">
    <out4:ListOfTimeTypes>
      <!-- a simple data mapping: "TimeTypeName"(string) to "out2:timeTypeName"(string) -->
      <out2:timeTypeName>
        <xsl:value-of select="TimeTypeName"/>
      </out2:timeTypeName>
      <!-- a simple data mapping: "TimeTypeId"(unsignedShort) to "out2:timeTypeId"(TimeTypeIDType) -->
      <out2:timeTypeId>
        <xsl:value-of select="TimeTypeId"/>
      </out2:timeTypeId>
    </out4:ListOfTimeTypes>
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