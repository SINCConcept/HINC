/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.global.TESTRUN;

import sinc.hinc.global.API.ResourcesManagementAPIImpl;
import sinc.hinc.global.management.CommunicationManager;
import sinc.hinc.model.API.ResourcesManagementAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author hungld
 */
public class Experiment1_Broadcast_ResponseTime {

    //     public static void main(String[] args) throws Exception {
//         
//        AbstractDAO<SoftwareDefinedGateway> gwDAO = new AbstractDAO<>(SoftwareDefinedGateway.class);
//        
//        SoftwareDefinedGateway gw = new SoftwareDefinedGateway();
//        gw.setName("testGW");
//        gw.setUuid("uuid1");
//        gwDAO.save(gw);
//        
//        for (SoftwareDefinedGateway gw1: gwDAO.readAll()){
//            System.out.println("Gateway UUID: " + gw1.getUuid());
//        }
//    }
    public static void main(String[] args) {
        //Linh: document this
        CommunicationManager client = new CommunicationManager(args[0], args[1], args[2]);
        ResourcesManagementAPI api = new ResourcesManagementAPIImpl();
        //client.synDelise(3000);

        // deploy new VM
//        for (int k = 1; k < 6; k++) {
//            deploy10Gateways();
//            // for each VM query 3 times
//            for (int i = 1; i < 4; i++) {
//                client.querySoftwareDefinedGateway_Broadcast(10000);
//            }
//        }
//        for (int i = 1; i < 4; i++) {
//            client.querySoftwareDefinedGateway_Broadcast(70000);
        api.querySoftwareDefinedGateways(70000, null);
//        }
    }

    static private void deploy10Gateways() {
        // /services/{serviceId}/nodes/{nodeId}/instance-count/{quantity}
        String url1 = "http://128.130.172.215:8080/salsa-engine/rest/"
                + "services/IoTSensors/"
                + "nodes/gatewayUnit/"
                + "instance-count/1";

        querySALSA(url1, HttpVerb.POST, "", "", "");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        try { // wait 1.5 mins for the VM is up, docker is deployed
            System.out.println("wating 5 mins for VMs deployed");
            Thread.sleep(300000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    static private String querySALSA(String input_url, HttpVerb method, String data, String type, String accept) {
        try {
            URL url = new URL(input_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());

            if (accept.equals("")) {
                conn.setRequestProperty("Accept", "\"text/plain\"");
            } else {
                conn.setRequestProperty("Accept", accept);
            }

            if (type.equals("")) {
                conn.setRequestProperty("Type", "\"text/plain\"");
            } else {
                conn.setRequestProperty("Type", type);
            }
            System.out.println("Execute a query. URL: " + url + ". Method: " + method + ". Data: " + data + ". Sending type:" + type + ". Recieving type: " + accept);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String result = "";
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                result += output;
            }
            conn.disconnect();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static enum HttpVerb {

        GET, POST, PUT, DELETE, OTHER;

        public static HttpVerb fromString(String method) {
            try {
                return HttpVerb.valueOf(method.toUpperCase());
            } catch (Exception e) {
                return OTHER;
            }
        }
    }
}