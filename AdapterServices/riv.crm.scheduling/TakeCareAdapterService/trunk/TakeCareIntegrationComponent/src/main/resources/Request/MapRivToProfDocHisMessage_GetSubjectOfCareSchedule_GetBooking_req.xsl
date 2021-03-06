<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated mapFile="xslt/MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req.map" md5sum="ba2e6540736505f72584ac0c62bf1a0b" version="7.0.400" -->
<!--
*****************************************************************************
*   This file has been generated by the IBM XML Mapping Editor V7.0.400
*
*   Mapping file:		MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req.map
*   Map declaration(s):	MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req
*   Input file(s):		smo://smo/name%3Dwsdl-primary/message%3D%257Burn%253Amvk%253Aasb%253Atidbok%253Ainteraction%253AGetSubjectOfCareSchedule%253A1%253A0%257DGetSubjectOfCareScheduleRequest/xpath%3D%252Fbody/smo.xsd
*   Output file(s):		smo://smo/name%3Dwsdl-primary/message%3D%257Burn%253Amvk%253Aasb%253Atakecare%253Av100%253AgetbookingInternalInterface%257DinvokeTakeCareRequestMsg/xpath%3D%252Fbody/smo.xsd
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
    xmlns:in="wsdl.urn:mvk:asb:tidbok:interaction:GetSubjectOfCareSchedule:1:0"
    xmlns:in2="urn:mvk:asb:tidbok:1:0"
    xmlns:in3="urn:mvk:asb:tidbok:GetSubjectOfCareScheduleResponder:1:0"
    xmlns:in4="urn:mvk:asb:common:base:1"
    xmlns:out="urn:mvk:asb:takecare:v100:getbookingInternalInterface"
    xmlns:out2="urn:mvk:asb:takecare:v100:TakeCareCommonAttribute"
    xmlns:io="http://www.w3.org/2003/05/soap-envelope"
    xmlns:io3="http://www.ibm.com/xmlns/prod/websphere/mq/sca/6.0.0"
    xmlns:io2="http://www.ibm.com/websphere/sibx/smo/v6.0.1"
    xmlns:out3="urn:ProfdocHISMessage:GetBookings:Request"
    xmlns:io4="http://schemas.xmlsoap.org/ws/2004/08/addressing"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:out4="wsdl.urn:mvk:asb:takecare:v100:getbookingInternalInterface"
    xmlns:io5="http://www.ibm.com/xmlns/prod/websphere/http/sca/6.1.0"
    xmlns:xsd4xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:io6="http://www.w3.org/2005/08/addressing"
    xmlns:TimebookUtils="xalan://se.modul1.mvk.asb.timebooking.takecare.java.TimebookUtils"
    xmlns:map="http://Mvk_Scheduling_TakeCare_Adapter/xslt/MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req"
    xmlns:msl="http://www.ibm.com/xmlmap"
    exclude-result-prefixes="set in msl math exsl in2 in3 date in4 xalan str TimebookUtils map"
    version="1.0">
  <xsl:output method="xml" encoding="UTF-8" indent="no"/>

  <!-- root wrapper template  -->
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="msl:datamap">
        <msl:datamap>
          <dataObject>
            <xsl:attribute name="xsi:type">
              <xsl:value-of select="'out4:invokeTakeCareRequestMsg'"/>
            </xsl:attribute>
            <xsl:call-template name="map:MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req2">
              <xsl:with-param name="body" select="msl:datamap/dataObject[1]"/>
            </xsl:call-template>
          </dataObject>
        </msl:datamap>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="body" mode="map:MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- This rule represents an element mapping: "body" to "body".  -->
  <xsl:template match="body"  mode="map:MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req">
    <body>
      <xsl:attribute name="xsi:type">
        <xsl:value-of select="'out4:invokeTakeCareRequestMsg'"/>
      </xsl:attribute>
      <out:invokeTakeCare>
        <out3:ProfdocHISMessage>
          <!-- a simple mapping with no associated source:  to "InvokingSystem"(string) -->
          <xsl:attribute name="InvokingSystem">
            <xsl:value-of select="'InvSysMVK'"/>
          </xsl:attribute>
          <!-- a simple mapping with no associated source:  to "MsgType"(string) -->
          <xsl:attribute name="MsgType">
            <xsl:value-of select="'Request'"/>
          </xsl:attribute>
          <!-- a simple mapping with no associated source:  to "Time"(unsignedLong) -->
          <xsl:attribute name="Time">
            <xsl:value-of select="TimebookUtils:getTodayDatesAsYYYYMMDDHHMMSS()"/>
          </xsl:attribute>
          <!-- a simple data mapping: "in3:GetSubjectOfCareSchedule/in3:subject_of_care"(SubjectOfCareIdType) to "PatientId"(unsignedLong) -->
          <xsl:if test="in3:GetSubjectOfCareSchedule/in3:subject_of_care">
            <PatientId>
              <!-- variables for custom code -->
              <xsl:variable name="subject_of_care" select="in3:GetSubjectOfCareSchedule/in3:subject_of_care"/>
              <xsl:value-of select="TimebookUtils:convertStringValueToLong($subject_of_care)"/>
            </PatientId>
          </xsl:if>
          <!-- a simple mapping with no associated source:  to "CareUnitIdType"(string) -->
          <CareUnitIdType>
            <xsl:text>hsaid</xsl:text>
          </CareUnitIdType>
          <!-- a simple data mapping: "in3:GetSubjectOfCareSchedule/in3:healthcare_facility"(HsaIdType) to "CareUnitId"(string) -->
          <CareUnitId>
            <xsl:value-of select="in3:GetSubjectOfCareSchedule/in3:healthcare_facility"/>
          </CareUnitId>
        </out3:ProfdocHISMessage>
        <!-- a simple data mapping: "in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/serviceComponent/endpointAddress"(EndpointAddressType) to "TargetAdressURLString"(string) -->
        <TargetAdressURLString>
          <xsl:value-of select="in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/serviceComponent/endpointAddress"/>
        </TargetAdressURLString>
        <ProfdocHISMessageHeader>
          <!-- a simple mapping with no associated source:  to "TCUser"(string) -->
          <TCUser>
            <xsl:text></xsl:text>
          </TCUser>
          <!-- a simple mapping with no associated source:  to "TCPassword"(string) -->
          <TCPassword>
            <xsl:text></xsl:text>
          </TCPassword>
          <!-- a simple mapping with no associated source:  to "externalUser"(string) -->
          <externalUser>
            <xsl:text>ExtUsrMVK</xsl:text>
          </externalUser>
          <!-- a simple mapping with no associated source:  to "careUnitIdType"(string) -->
          <careUnitIdType>
            <xsl:text>HSAID</xsl:text>
          </careUnitIdType>
          <!-- a simple data mapping: "in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress"(LogicalAddressType) to "hsaId"(string) -->
          <xsl:if test="in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress">
            <hsaId>
              <xsl:value-of select="in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress"/>
            </hsaId>
          </xsl:if>
          <!-- a simple mapping with no associated source:  to "invokingSystem"(string) -->
          <invokingSystem>
            <xsl:text>InvSysMVK</xsl:text>
          </invokingSystem>
        </ProfdocHISMessageHeader>
      </out:invokeTakeCare>
    </body>
  </xsl:template>

  <!-- This rule represents a type mapping: "body" to "body".  -->
  <xsl:template name="map:MapRivToProfDocHisMessage_GetSubjectOfCareSchedule_GetBooking_req2">
    <xsl:param name="body"/>
    <out:invokeTakeCare>
      <out3:ProfdocHISMessage>
        <!-- a simple mapping with no associated source:  to "InvokingSystem"(string) -->
        <xsl:attribute name="InvokingSystem">
          <xsl:value-of select="'InvSysMVK'"/>
        </xsl:attribute>
        <!-- a simple mapping with no associated source:  to "MsgType"(string) -->
        <xsl:attribute name="MsgType">
          <xsl:value-of select="'Request'"/>
        </xsl:attribute>
        <!-- a simple mapping with no associated source:  to "Time"(unsignedLong) -->
        <xsl:attribute name="Time">
          <xsl:value-of select="TimebookUtils:getTodayDatesAsYYYYMMDDHHMMSS()"/>
        </xsl:attribute>
        <!-- a simple data mapping: "$body/in3:GetSubjectOfCareSchedule/in3:subject_of_care"(SubjectOfCareIdType) to "PatientId"(unsignedLong) -->
        <xsl:if test="$body/in3:GetSubjectOfCareSchedule/in3:subject_of_care">
          <PatientId>
            <!-- variables for custom code -->
            <xsl:variable name="subject_of_care" select="$body/in3:GetSubjectOfCareSchedule/in3:subject_of_care"/>
            <xsl:value-of select="TimebookUtils:convertStringValueToLong($subject_of_care)"/>
          </PatientId>
        </xsl:if>
        <!-- a simple mapping with no associated source:  to "CareUnitIdType"(string) -->
        <CareUnitIdType>
          <xsl:text>hsaid</xsl:text>
        </CareUnitIdType>
        <!-- a simple data mapping: "$body/in3:GetSubjectOfCareSchedule/in3:healthcare_facility"(HsaIdType) to "CareUnitId"(string) -->
        <CareUnitId>
          <xsl:value-of select="$body/in3:GetSubjectOfCareSchedule/in3:healthcare_facility"/>
        </CareUnitId>
      </out3:ProfdocHISMessage>
      <!-- a simple data mapping: "$body/in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/serviceComponent/endpointAddress"(EndpointAddressType) to "TargetAdressURLString"(string) -->
      <TargetAdressURLString>
        <xsl:value-of select="$body/in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/serviceComponent/endpointAddress"/>
      </TargetAdressURLString>
      <ProfdocHISMessageHeader>
        <!-- a simple mapping with no associated source:  to "TCUser"(string) -->
        <TCUser>
          <xsl:text></xsl:text>
        </TCUser>
        <!-- a simple mapping with no associated source:  to "TCPassword"(string) -->
        <TCPassword>
          <xsl:text></xsl:text>
        </TCPassword>
        <!-- a simple mapping with no associated source:  to "externalUser"(string) -->
        <externalUser>
          <xsl:text>ExtUsrMVK</xsl:text>
        </externalUser>
        <!-- a simple mapping with no associated source:  to "careUnitIdType"(string) -->
        <careUnitIdType>
          <xsl:text>HSAID</xsl:text>
        </careUnitIdType>
        <!-- a simple data mapping: "$body/in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress"(LogicalAddressType) to "hsaId"(string) -->
        <xsl:if test="$body/in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress">
          <hsaId>
            <xsl:value-of select="$body/in3:GetSubjectOfCareSchedule/in3:mvkAdressering/in2:configData/logicalAdressat/logicalAddress"/>
          </hsaId>
        </xsl:if>
        <!-- a simple mapping with no associated source:  to "invokingSystem"(string) -->
        <invokingSystem>
          <xsl:text>InvSysMVK</xsl:text>
        </invokingSystem>
      </ProfdocHISMessageHeader>
    </out:invokeTakeCare>
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
