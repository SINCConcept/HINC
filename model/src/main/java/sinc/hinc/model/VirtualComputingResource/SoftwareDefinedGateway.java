package sinc.hinc.model.VirtualComputingResource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sinc.hinc.model.VirtualNetworkResource.AccessPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import sinc.hinc.model.Extensible.ExtensibleModel;
import sinc.hinc.model.VirtualComputingResource.Capabilities.CloudConnectivity;
import sinc.hinc.model.VirtualComputingResource.Capabilities.ControlPoint;
import sinc.hinc.model.VirtualComputingResource.Capabilities.DataPoint;
import sinc.hinc.model.VirtualComputingResource.Capabilities.ExecutionEnvironment;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DataPoint.class, name = "DataPoint"),
    @JsonSubTypes.Type(value = ControlPoint.class, name = "ControlPoint"),
    @JsonSubTypes.Type(value = CloudConnectivity.class, name = "CloudConectivity"),
    @JsonSubTypes.Type(value = ExecutionEnvironment.class, name = "ExecutionEnvironment"),
    @JsonSubTypes.Type(value = SoftwareDefinedGateway.class, name = "SoftwareDefinedGateway")
})
public class SoftwareDefinedGateway {

    /**
     * The uuid is unique within the whole system
     */
    private String uuid;

    /**
     * The name for human reading purpose
     */
    private String name;

    private String type = "SoftwareDefinedGateway";

    /**
     * The List of datapoint, controlpoint, connectivity and execution
     */
//    private List<Capability> capabilities;
    private List<DataPoint> dataPoints;
    private List<ControlPoint> controlPoints;
    private List<CloudConnectivity> connectivity;
    private List<ExecutionEnvironment> executionEnvironment;

    private List<ExtensibleModel> extra;

    /**
     * For custom data, e.g. created date, position, comments
     */
    private Map<String, String> meta;

//    private List<AccessPoint> accessPoints;
    /**
     * Construction and get/set
     */
    public SoftwareDefinedGateway() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataPoint> getDataPoints() {
        if (dataPoints == null) {
            dataPoints = new ArrayList<>();
        }
        return dataPoints;
    }

    public List<ControlPoint> getControlPoints() {
        if (controlPoints == null) {
            controlPoints = new ArrayList<>();
        }
        return controlPoints;
    }

    public List<CloudConnectivity> getConnectivity() {
        if (connectivity == null) {
            connectivity = new ArrayList<>();
        }
        return connectivity;
    }

    public List<ExecutionEnvironment> getExecutionEnvironment() {
        if (executionEnvironment == null) {
            executionEnvironment = new ArrayList<>();
        }
        return executionEnvironment;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public void setControlPoints(List<ControlPoint> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public void setConnectivity(List<CloudConnectivity> connectivity) {
        this.connectivity = connectivity;
    }

    public void setExecutionEnvironment(List<ExecutionEnvironment> executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

    public SoftwareDefinedGateway hasCapability(Capability capa) {
        capa.setGatewayID(this.getUuid());
        if (capa.getClass().equals(DataPoint.class)) {
            capa.setUuid(this.getUuid() + "/" + capa.getName());
            this.getDataPoints().add((DataPoint) capa);
        } else if (capa.getClass().equals(ControlPoint.class)) {
            capa.setUuid(this.getUuid() + "/" + capa.getResourceID() + "/" + capa.getName());
            this.getControlPoints().add((ControlPoint) capa);
        } else if (capa.getClass().equals(CloudConnectivity.class)) {
            this.getConnectivity().add((CloudConnectivity) capa);
        } else if (capa.getClass().equals(ExecutionEnvironment.class)) {
            this.getExecutionEnvironment().add((ExecutionEnvironment) capa);
        } else {
            System.out.println("Error when adding capablity: " + capa.getUuid());
        }
        return this;
    }

    public SoftwareDefinedGateway hasCapabilities(List<? extends Capability> capas) {
        if (capas != null) {
            for (Capability capa : capas) {
                hasCapability(capa);
            }
        }
        return this;
    }

    public Map<String, String> getMeta() {
        if (meta == null) {
            meta = new HashMap<>();
        }
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SoftwareDefinedGateway fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, SoftwareDefinedGateway.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<ExtensibleModel> getExtra() {
        if (this.extra == null) {
            this.extra = new ArrayList<>();
        }
        return extra;
    }

    public void setExtra(List<ExtensibleModel> extra) {
        this.extra = extra;
    }

    public ExtensibleModel getExtraByType(Class clazz) {
        for (ExtensibleModel ext : this.getExtra()) {
            if (ext.getClazz().equals(clazz)) {
                return ext;
            }
        }
        return null;
    }

}
