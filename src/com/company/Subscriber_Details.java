package com.company;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Subscriber_Details {


    private String authXML ="" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:al=\"http://www.alcatel-lucent.com/soap_cm\" xmlns:bd=\"http://www.3gpp.org/ftp/Specs/archive/32_series/32607/schema/32607-700/BasicCMIRPData\" xmlns:bs=\"http://www.3gpp.org/ftp/Specs/archive/32_series/32607/schema/32607-700/BasicCMIRPSystem\" xmlns:gd=\"http://www.3gpp.org/ftp/Specs/archive/32_series/32317/schema/32317-700/GenericIRPData\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "   <SOAP-ENV:Body>\n" +
            "      <bd:deleteMO>\n" +
            "         <queryXpathExp>\n" +
            "            <al:baseObjectInstance>gsmServiceProfileId=1,suMSubscriptionProfileId=1,suMSubscriberProfileId=1-XXXX,subscriptionFunctionId=1,managedElementId=HSS1</al:baseObjectInstance>\n" +
            "            <al:scope>BASE_ALL</al:scope>\n" +
            "         </queryXpathExp>\n" +
            "      </bd:deleteMO>\n" +
            "   </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";

    private DataSet dataSet;

    public Subscriber_Details(DataSet dataSet) {

        this.dataSet = dataSet;
    }

    private String authXML_dataSubstition(){
        authXML = authXML.replace("XXXX", this.dataSet.getProfileID());
        return authXML;
    }

    public SOAPMessage createSOAPRequest() throws SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage SOAPmessage = messageFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(authXML_dataSubstition().getBytes()));
        SOAPmessage.saveChanges();
        return SOAPmessage;
    }

    public SOAPMessage sendSOAPRequest(SOAPMessage message) throws SOAPException, IOException {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnectionFactory.createConnection();
        String API_ENDPOINT = "http://172.21.7.6:18100/";
        return connection.call(message, API_ENDPOINT);



    }

    public boolean processSOAPResponse(SOAPMessage soapMessage){
        try {
            if (soapMessage.getSOAPBody().hasFault()) {
                return false;
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }




}
