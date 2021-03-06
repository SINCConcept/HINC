package sinc.hinc.local.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sinc.hinc.common.communication.HincMessage;

import java.io.IOException;
import java.util.Map;
import sinc.hinc.common.communication.HINCMessageType;
import sinc.hinc.common.model.Resource;
import sinc.hinc.common.model.payloads.Control;
import sinc.hinc.common.model.payloads.ControlResult;
import sinc.hinc.common.utils.HincConfiguration;

public class Adaptor {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;

    private String adaptorInputQueue;
    private String adaptorOutputUnicastExchange;
    private String group;
    private String id;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Adaptor(RabbitTemplate rabbitTemplate, String adaptorOutputUnicastExchange, String adaptorInputQueue, String group, String id){
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.adaptorInputQueue = adaptorInputQueue;
        this.adaptorOutputUnicastExchange = adaptorOutputUnicastExchange;
        this.group = group;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void scanResources(){
        HincMessage message = new HincMessage(
                HINCMessageType.QUERY_RESOURCES,
                HincConfiguration.getMyUUID(),
                "");

        MessageProperties properties = new MessageProperties();
        System.out.println(adaptorInputQueue);
        properties.setReplyTo(adaptorInputQueue);
        Message msg = new Message(message.toJson().getBytes(), properties);
        rabbitTemplate.send(adaptorOutputUnicastExchange, name, msg);

    }

    public void scanResourceProvider(){
        HincMessage message = new HincMessage(
                HINCMessageType.QUERY_PROVIDER,
                HincConfiguration.getMyUUID(),
                "");

        MessageProperties properties = new MessageProperties();
        properties.setReplyTo(adaptorInputQueue);
        Message msg = new Message(message.toJson().getBytes(), properties);
        rabbitTemplate.send(adaptorOutputUnicastExchange, name, msg);

    }

    public void sendControl(Control control){

    }

    public Resource provisionResource(Resource resource) throws IOException {
        HincMessage provisionMessage = new HincMessage();

        provisionMessage.setMsgType(HINCMessageType.PROVISION);
        provisionMessage.setUuid(group + "." + id);

        provisionMessage.setSenderID(group + "." + id);
        provisionMessage.setPayload(objectMapper.writeValueAsString(resource));

        logger.info("sending provision message");
        logger.info(provisionMessage.toJson());

        ControlResult result = sendControl(resource.getProviderUuid(), provisionMessage);

        Resource provisionedResource = objectMapper.readValue(result.getRawOutput(), Resource.class);

        return provisionedResource;
    }

    public Resource deleteResource(Resource resource) throws IOException {
        HincMessage deleteMessage = new HincMessage();
        deleteMessage.setMsgType(HINCMessageType.DELETE);
        deleteMessage.setSenderID(group+"."+id);
        deleteMessage.setPayload(objectMapper.writeValueAsString(resource));

        ControlResult result = sendControl(resource.getProviderUuid(), deleteMessage);
        Resource deletedResource = objectMapper.readValue(result.getRawOutput(), Resource.class);

        return deletedResource;
    }

    public Resource configureResource(Resource resource) throws IOException{
        HincMessage configureMessage = new HincMessage();
        configureMessage.setMsgType(HINCMessageType.CONFIGURE);
        configureMessage.setSenderID(group+"."+id);
        configureMessage.setPayload(objectMapper.writeValueAsString(resource));

        ControlResult result = sendControl(resource.getProviderUuid(), configureMessage);
        Resource configuredResource = objectMapper.readValue(result.getRawOutput(), Resource.class);

        return configuredResource;
    }

    public ControlResult sendControl(String providerUuid, HincMessage message) throws IOException {
        logger.info("sending "+message.getMsgType()+" message");
        logger.info(message.toJson());

        Object rawReply = rabbitTemplate.convertSendAndReceive(
                adaptorOutputUnicastExchange,
                providerUuid,
                message.toJson().getBytes()
        );

        HincMessage reply = null;
        String stringReply = new String(((byte[]) rawReply));
        reply = objectMapper.readValue(stringReply, HincMessage.class);

        ControlResult result = objectMapper.readValue(reply.getPayload(), ControlResult.class);
        return result;
    }

    public String getResourceLogs(Resource resource) throws IOException {
        HincMessage getLogsMessage = new HincMessage();
        getLogsMessage.setMsgType(HINCMessageType.GET_LOGS);
        getLogsMessage.setSenderID(group+"."+id);
        getLogsMessage.setPayload(objectMapper.writeValueAsString(resource));

        ControlResult result = sendControl(resource.getProviderUuid(), getLogsMessage);
        return result.getRawOutput();
    }
}
