
package se.sll.engagemangsindex.integration;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "GetLogicalAddresseesByServiceContractResponderInterface", targetNamespace = "urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContract:1:rivtabp21")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface GetLogicalAddresseesByServiceContractResponderInterface {


    /**
     * 
     * @param logicalAddress
     * @param parameters
     * @return
     *     returns se.sll.engagemangsindex.integration.GetLogicalAddresseesByServiceContractResponseType
     */
    @WebMethod(operationName = "GetLogicalAddresseesByServiceContract", action = "urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1:GetLogicalAddresseesByServiceContract")
    @WebResult(name = "GetLogicalAddresseesByServiceContractResponse", targetNamespace = "urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1", partName = "parameters")
    public GetLogicalAddresseesByServiceContractResponseType getLogicalAddresseesByServiceContract(
        @WebParam(name = "LogicalAddress", targetNamespace = "urn:riv:itintegration:registry:1", header = true, partName = "LogicalAddress")
        String logicalAddress,
        @WebParam(name = "GetLogicalAddresseesByServiceContract", targetNamespace = "urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1", partName = "parameters")
        GetLogicalAddresseesByServiceContractType parameters);

}