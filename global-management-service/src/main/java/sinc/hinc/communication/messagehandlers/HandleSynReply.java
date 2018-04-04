package sinc.hinc.communication.messagehandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sinc.hinc.common.metadata.HINCMessageType;
import sinc.hinc.common.metadata.HincLocalMeta;
import sinc.hinc.communication.GlobalCommunicationManager;
import sinc.hinc.communication.IMessageHandler;
import sinc.hinc.communication.processing.HincMessage;
import sinc.hinc.repository.DAO.orientDB.AbstractDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HandleSynReply implements IMessageHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private GlobalCommunicationManager globalCommunicationManager;

    public HandleSynReply(GlobalCommunicationManager globalCommunicationManager){
        this.globalCommunicationManager = globalCommunicationManager;
    }

    @Override
    public HINCMessageType getMessageType() {
        return HINCMessageType.SYN_REPLY;
    }

    @Override
    public void handleMessage(HincMessage hincMessage) {
        logger.debug("received " + hincMessage.toString());

        //TODO improve implementation --> hincMessage properties

        String group = null;
        if( hincMessage.getGroup() != null){
            group = hincMessage.getGroup();
        }else if(hincMessage.getTopic() !=null){
            group = hincMessage.getTopic();
        }

        if(hincMessage.getSenderID()!=null && group != null) {
            try {
                String queue = hincMessage.getGroup() + "." + hincMessage.getSenderID();
                globalCommunicationManager.addLocalManagementService(queue, hincMessage.getGroup(), hincMessage.getSenderID());


                if(hincMessage.getPayload() != null) {
                    List<HincLocalMeta> listOfHINCLocal = new ArrayList<>();
                    HincLocalMeta meta = HincLocalMeta.fromJson(hincMessage.getPayload());
                    logger.debug("  --> Meta: " + meta.toJson());
                    // remove the exist meta if need before update
                    Iterator<HincLocalMeta> metas = listOfHINCLocal.iterator();
                    while (metas.hasNext()) {
                        HincLocalMeta existed = metas.next();
                        if (meta.getUuid().equals(existed.getUuid())) {
                            listOfHINCLocal.remove(existed);
                        }
                    }
                    listOfHINCLocal.add(meta);
                    AbstractDAO<HincLocalMeta> metaDAO = new AbstractDAO<>(HincLocalMeta.class);
                    metaDAO.save(meta);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}