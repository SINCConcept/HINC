package sinc.hinc.local.communication.messagehandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sinc.hinc.common.communication.HINCMessageHandler;
import sinc.hinc.common.communication.HINCMessageType;
import sinc.hinc.common.communication.HincMessage;
import sinc.hinc.model.Resource;
import sinc.hinc.repository.DAO.orientDB.AbstractDAO;

import java.io.IOException;
import java.util.Arrays;

public class HandleResourcesUpdate extends HINCMessageHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapper();

    public HandleResourcesUpdate(HINCMessageType messageType) {
        super(messageType);
    }

    @Override
    protected void doHandle(HincMessage hincMessage) {
        try {
            Resource[] resources = objectMapper.readValue(hincMessage.getPayload(), Resource[].class);
            AbstractDAO<Resource> resourceDAO = new AbstractDAO<>(Resource.class);
            resourceDAO.saveAll(Arrays.asList(resources));
        } catch (IOException e) {
            logger.error("failed to serialize message payload for " + this.messageType);
            e.printStackTrace();
        }
    }
}