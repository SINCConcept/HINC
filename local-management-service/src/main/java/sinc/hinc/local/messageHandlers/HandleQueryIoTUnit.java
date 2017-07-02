/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.local.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import sinc.hinc.common.metadata.HINCMessageType;
import sinc.hinc.common.metadata.HincLocalMeta;
import sinc.hinc.common.utils.HincConfiguration;
import sinc.hinc.communication.processing.HincMessage;
import sinc.hinc.repository.DAO.orientDB.IoTUnitDAO;
import sinc.hinc.communication.processing.HINCMessageHander;
import sinc.hinc.local.Main;
import sinc.hinc.model.API.WrapperIoTUnit;
import sinc.hinc.model.VirtualComputingResource.IoTUnit;

/**
 *
 * @author hungld
 */
public class HandleQueryIoTUnit implements HINCMessageHander {

    static Logger logger = HincConfiguration.getLogger();

    @Override
    public HincMessage handleMessage(HincMessage msg) {
        logger.debug("Server get request for IoTUnit information: " + msg.toJson());

        // check infobase
        if (msg.getExtra() != null && msg.getExtra().containsKey("infoBases")) {
            List<String> infoBasesList = Arrays.asList(msg.getExtra().get("infoBases").split(","));
            List<String> myInfoBases = HincConfiguration.getLocalMeta().getInfoBase();
            boolean found = false;
            for (String s : infoBasesList) {
                if (myInfoBases.contains(s.trim())){                
                    found = true;
                    break;
                }
            }
            if (found == false) {
                // this local service does not in the Information Bases of the query, so just pass.
                return null;
            }
        }

        // processing
        Long timeStamp2 = (new Date()).getTime();

        if (msg.getPayload().contains("rescan")) {
            try {
                Main.scanOnce();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        Long timeStamp3 = (new Date()).getTime();
        IoTUnitDAO unitDao = new IoTUnitDAO();
        int limit = -1;
        if (msg.getExtra() != null && msg.getExtra().containsKey("limit")) {
            try {
                limit = Integer.parseInt(msg.getExtra().get("limit"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        List<IoTUnit> units = unitDao.readAll(limit);
        logger.debug("Local service read all : " + units.size() + " items, which will be send back.");
        WrapperIoTUnit wrapper = new WrapperIoTUnit(units);
        ObjectMapper mapper = new ObjectMapper();

        try {
            String replyPayload = mapper.writeValueAsString(wrapper);

            logger.debug("Size of the reply message: " + (replyPayload.length() / 1024) + "KB");
            HincMessage replyMsg = new HincMessage(HINCMessageType.UPDATE_INFORMATION_SINGLEIOTUNIT.toString(), HincConfiguration.getMyUUID(), msg.getFeedbackTopic(), "", replyPayload);

            System.out.println("Now send the message back global via topic: " + msg.getFeedbackTopic());

            Long timeStamp4 = (new Date()).getTime(); // time3 -> time4: local service process the data

            replyMsg.hasExtra("timeStamp2", timeStamp2.toString());
            replyMsg.hasExtra("timeStamp3", timeStamp3.toString());
            replyMsg.hasExtra("timeStamp4", timeStamp4.toString());

//            MessageClientFactory FACTORY = new MessageClientFactory(HincConfiguration.getBroker(), HincConfiguration.getBrokerType());
//            FACTORY.getMessagePublisher().pushMessage(replyMsg);
            System.out.println("Return the IoT unit data to the callee: \n" + replyMsg);
            return replyMsg;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }

    }

}