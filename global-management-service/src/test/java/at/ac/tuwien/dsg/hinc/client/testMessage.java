/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.hinc.client;

import at.ac.tuwien.dsg.hinc.communication.messagePayloads.HincMeta;
import at.ac.tuwien.dsg.hinc.model.VirtualNetworkResource.VNF;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author hungld
 */
public class testMessage {

    public static void main(String[] args) throws Exception {
        /**
         * This part connect the client to the message queue, get the list of DElise
         */
        QueryManager client = new QueryManager("myClient", "amqp://128.130.172.215", "amqp");
        client.synDelise(2000);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(client.getListOfDelise()));

        System.out.println("SYN DONE, GOING TO PHASE 2");
        System.out.println("There are " + client.getListOfDelise().size() + " gateway");

        /**
         * Query information from each DElise, unicast pattern
         */
        for (HincMeta delise : client.getListOfDelise()) {
            System.out.println("QUERYING DELISE: " + delise.getUuid());
//            SoftwareDefinedGateway gw = client.querySoftwareDefinedGateway(delise.getUuid());
//            if (gw != null) {
//                System.out.println("Number of capabilities: " + gw.getCapabilities().size());
//                System.out.println("GW Info: " + gw.toJson());
//            } else {
//                System.out.println("GW is null !");
//            }

            System.out.println("NOW QUERYING NVF IF AVAILABLE...");
            VNF vnf = client.queryVNF_Unicast(delise.getUuid());
            if (vnf != null) {
                System.out.println(vnf.toJson());
            } else {
                System.out.println("VNF is null !");
            }

        }

        /**
         * Query information from all, broad cast pattern + filter
         */
        /**
         * Call the control point to reconfigure the component
         */
        System.out.println("Should quit here ! (or queue are not closed yet)");

    }
}