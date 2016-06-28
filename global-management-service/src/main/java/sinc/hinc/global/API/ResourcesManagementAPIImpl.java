/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.global.API;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sinc.hinc.common.metadata.HincLocalMeta;
import sinc.hinc.communication.processing.HincMessage;
import sinc.hinc.common.metadata.HincMessageTopic;
import sinc.hinc.common.utils.HincConfiguration;
import sinc.hinc.communication.processing.HINCMessageSender;
import sinc.hinc.model.API.ResourcesManagementAPI;
import sinc.hinc.model.CloudServices.CloudProvider;
import sinc.hinc.model.CloudServices.CloudService;
import sinc.hinc.model.VirtualComputingResource.SoftwareDefinedGateway;
import sinc.hinc.model.VirtualNetworkResource.NetworkFunctionService;
import sinc.hinc.model.VirtualNetworkResource.VNF;
import sinc.hinc.repository.DAO.orientDB.SoftwareDefinedGatewayDAO;

import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import sinc.hinc.common.metadata.HINCMessageType;
import sinc.hinc.model.VirtualComputingResource.Capabilities.CloudConnectivity;
import sinc.hinc.model.VirtualComputingResource.Capabilities.ControlPoint;
import sinc.hinc.model.VirtualComputingResource.Capabilities.DataPoint;
import sinc.hinc.model.VirtualComputingResource.Capability;
import sinc.hinc.repository.DAO.orientDB.DatabaseUtils;
import sinc.hinc.communication.processing.HINCMessageHander;

/**
 * @author hungld
 */
@Service
//@Path("/")
public class ResourcesManagementAPIImpl implements ResourcesManagementAPI {

    static Logger logger = LoggerFactory.getLogger("HINC");
    HINCMessageSender comMng = getCommunicationManager();
    List<HincLocalMeta> listOfHINCLocal = new ArrayList<>();

    {
        // check database, if not exist then create
        DatabaseUtils.initDB();
    }

    public ResourcesManagementAPIImpl() {
    }

    public HINCMessageSender getCommunicationManager() {
        if (comMng == null) {
            this.comMng = new HINCMessageSender(HincConfiguration.getBroker(), HincConfiguration.getBrokerType());
        }
        return this.comMng;
    }

    @Override
    public List<SoftwareDefinedGateway> querySoftwareDefinedGateways(int timeout, String hincUUID) {
        logger.debug("Start broadcasting the query...");
        File dir = new File("log/queries");
        dir.mkdirs();
        logger.debug("Data is stored in: " + dir.getAbsolutePath());

        final List<String> events = new LinkedList<>();
        final List<SoftwareDefinedGateway> result = new ArrayList<>();

        String feedBackTopic = HincMessageTopic.getTemporaryTopic();

        final long timeStamp1 = (new Date()).getTime();
        String eventFileName = "log/queries/" + timeStamp1 + ".event";

        if (hincUUID != null && !hincUUID.isEmpty() && !hincUUID.trim().equals("null")) {
            logger.debug("Trying to query HINC Local with ID: " + hincUUID);
            String gatewayInJson = comMng.synCall(new HincMessage(HINCMessageType.RPC_QUERY_SDGATEWAY_LOCAL.toString(), HincConfiguration.getMyUUID(), HincMessageTopic.getHINCPrivateTopic(hincUUID), feedBackTopic, ""));
            
            result.add(SoftwareDefinedGateway.fromJson(gatewayInJson));
        } else {
            HincMessage queryMessage = new HincMessage(HINCMessageType.RPC_QUERY_SDGATEWAY_LOCAL.toString(), HincConfiguration.getMyUUID(), HincMessageTopic.getBroadCastTopic(HincConfiguration.getGroupName()), feedBackTopic, "");
            comMng.asynCall(timeout, queryMessage, new HINCMessageHander() {
                long latestTime = 0;
                long quantity = 0;
                long currentSum = 0;

                @Override
                public void handleMessage(HincMessage message) {
                    Long timeStamp5 = (new Date()).getTime();
                    logger.debug("Get a response message from " + message.getSenderID());
                    SoftwareDefinedGateway gw = SoftwareDefinedGateway.fromJson(message.getPayload());
                    if (gw == null) {
                        logger.debug("Payload is null, or cannot be converted");
                        return;
                    }
                    SoftwareDefinedGatewayDAO gwDAO = new SoftwareDefinedGatewayDAO();
                    gwDAO.save(gw);
                    result.add(gw);

                    // ==== Record time for various experiments ===
                    Long timeStamp6 = (new Date()).getTime();
                    Long timeStamp2 = Long.parseLong(message.getExtra().get("timeStamp2"));
                    Long timeStamp3 = Long.parseLong(message.getExtra().get("timeStamp3"));
                    Long timeStamp4 = Long.parseLong(message.getExtra().get("timeStamp4"));

                    Long local_global_latency = timeStamp2 - timeStamp1;
                    Long provider_process = timeStamp3 - timeStamp2;
                    Long local_process = timeStamp4 - timeStamp3;
                    Long reply_latency = timeStamp5 - timeStamp4;
                    Long global_latency = timeStamp6 - timeStamp5;
                    Long end2end = timeStamp6 - timeStamp1;

                    String eventStr = gw.getUuid() + "," + timeStamp1 + "," + timeStamp2 + "," + timeStamp3 + "," + timeStamp4 + "," + timeStamp5 + "," + timeStamp6 + ","
                            + local_global_latency + "," + provider_process + "," + local_process + "," + reply_latency + "," + global_latency + "," + end2end;
                    System.out.println("Event is log: " + eventStr);
                    events.add(eventStr);
                }
            });
        }

        // wait for a few second
        try {
            logger.debug("Wait for " + timeout + " miliseconds for subscription threads finish ...........");
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(eventFileName, true)))) {
            logger.debug("Now saving events to file: " + eventFileName);
            for (String s : events) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<DataPoint> queryDataPoint(int timeout, String hincUUID) {
        List<SoftwareDefinedGateway> gateways = querySoftwareDefinedGateways(timeout, hincUUID);
        List<DataPoint> datapoints = new ArrayList<>();
        for (SoftwareDefinedGateway gw : gateways) {
            for (Capability capa : gw.getDataPoints()) {
                datapoints.add((DataPoint) capa);
            }
        }
        return datapoints;
    }

    @Override
    public List<ControlPoint> queryControlPoint(int timeout, String hincUUID) {
        List<SoftwareDefinedGateway> gateways = querySoftwareDefinedGateways(timeout, hincUUID);
        List<ControlPoint> controlPoints = new ArrayList<>();
        for (SoftwareDefinedGateway gw : gateways) {
            for (Capability capa : gw.getControlPoints()) {
                controlPoints.add((ControlPoint) capa);
            }
        }
        return controlPoints;
    }

    @Override
    public List<CloudConnectivity> queryConnectivity(int timeout, String hincUUID) {
        List<SoftwareDefinedGateway> gateways = querySoftwareDefinedGateways(timeout, hincUUID);
        List<CloudConnectivity> connectivity = new ArrayList<>();
        for (SoftwareDefinedGateway gw : gateways) {
            for (Capability capa : gw.getConnectivity()) {
                connectivity.add((CloudConnectivity) capa);

            }
        }
        return connectivity;
    }

    @Override
    public List<NetworkFunctionService> queryNetworkService(int timeout, String hincUUID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<VNF> queryVNF(int timeout, String hincUUID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<CloudService> queryCloudServices(int timeout, String hincUUID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CloudProvider> queryCloudProviders(int timeout, String hincUUID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendControl(UriInfo context, String providerUUID, String controlAction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
