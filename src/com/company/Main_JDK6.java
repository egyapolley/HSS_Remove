
package com.company;


import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main_JDK6 {

    public static void main(String[] args) {


        try {
            boolean deleteSuccess = false;
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("/hss/delete/config.properties")));
            String url = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");

            String input_dir_prop = properties.getProperty("file.input_dir");
            String output_dir_prop = properties.getProperty("file.output_dir");

            String file_name = properties.getProperty("file.name");

            String SQL = "delete from vodafoneAccts where msisdn= ?";
            Connection connection = DatabaseUtils.getConnection(url, username, password);
            if(connection != null){

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);


            try {
                File fileInput = new File(input_dir_prop + "/" + file_name);
                Scanner scanner = new Scanner(fileInput, "UTF-8");
                scanner.useDelimiter(Pattern.compile("\\n"));

                int counter =0;

                while (scanner.hasNext()) {

                    String line = scanner.nextLine();

                    String[] lineArray = line.trim().split(",");
                    DataSet dataSet = new DataSet();

                    dataSet.setMsisdn(lineArray[0].trim());
                    dataSet.setIccid(lineArray[1].trim());
                    dataSet.setImsi(lineArray[2].trim());
                    dataSet.setKeys(lineArray[3].trim());
                    dataSet.setProfileID(lineArray[1].trim().substring(12));



                    try {
                        Subscriber_Details subscriber_details = new Subscriber_Details(dataSet);
                        SOAPMessage soapRequestSubs = subscriber_details.createSOAPRequest();
                        SOAPMessage soapResponseSubs = subscriber_details.sendSOAPRequest(soapRequestSubs);

                        boolean subsDeleteStatus = subscriber_details.processSOAPResponse(soapResponseSubs);
                        if (subsDeleteStatus) {
                            Authentication_Keys authentication_keys = new Authentication_Keys(dataSet);
                            SOAPMessage soapRequestKeys = authentication_keys.createSOAPRequest();
                            SOAPMessage soapResponsekeys = authentication_keys.sendSOAPRequest(soapRequestKeys);

                            boolean keysDeletestatus = authentication_keys.processSOAPResponse(soapResponsekeys);
                            if (keysDeletestatus) {
                                deleteSuccess = true;
                                System.out.println(dataSet.getMsisdn() + "  successfully deleted on HSS");
                                counter++;
                            }


                        }


                    } catch (SOAPException e) {
                        e.printStackTrace();
                    }

                    if (deleteSuccess) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMddHHmmss");
                        String outputFile = output_dir_prop + "/" + simpleDateFormat.format(new Date()) + "-" + file_name;
                        fileInput.renameTo(new File(outputFile));
                            preparedStatement.setString(1, dataSet.getMsisdn());
                            preparedStatement.executeUpdate();
                        System.out.println(dataSet.getMsisdn()+ " successfully deleted on Local DB");
                    }
                    deleteSuccess = false;

                }
                System.out.println("\nTotal deleted: "+counter);


            } catch (IOException e) {
                e.printStackTrace();
            }

            }
            assert connection != null;
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}

