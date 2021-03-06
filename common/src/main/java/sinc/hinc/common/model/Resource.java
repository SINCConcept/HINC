package sinc.hinc.common.model;

import com.fasterxml.jackson.databind.JsonNode;
import sinc.hinc.common.model.accessPoint.AccessPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import sinc.hinc.common.model.accessPoint.AccessPoint;
import sinc.hinc.common.model.capabilities.ControlPoint;
import sinc.hinc.common.model.capabilities.DataPoint;

import java.util.ArrayList;
import java.util.Collection;

public class Resource {

    // add more as needed
    public enum ResourceCategory {
        SENSOR,
        BROKER,
        SOFTWARE_ARTIFACT,
        STORAGE,
    }


    public enum ResourceType{
        IOT_RESOURCE,
        NETWORK_FUNCTION_SERVICE,
        CLOUD_SERVICE,
    }

    private String name;
    private String adaptorName;
    private ResourceCategory resourceCategory;
    private ResourceType resourceType;
    // can be gps or other representation
    // not formalized yet
    private String location;
    private JsonNode metadata;
    //configurable resource parameters
    private JsonNode parameters;


    private Collection<ControlPoint> controlPoints = new ArrayList<>();
    private Collection<DataPoint> dataPoints = new ArrayList<>();
    // primary key used in database


    @Id
    private String uuid;

    private String providerUuid;

    public String getProviderUuid() {
        return providerUuid;
    }

    public void setProviderUuid(String providerUuid) {
        this.providerUuid = providerUuid;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public JsonNode getParameters() {
        return parameters;
    }

    public void setParameters(JsonNode parameters) {
        this.parameters = parameters;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAdaptorName() {
        return adaptorName;
    }

    public void setAdaptorName(String adaptorName) {
        this.adaptorName = adaptorName;
    }

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(ResourceCategory resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    public Collection<ControlPoint> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(Collection<ControlPoint> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public Collection<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(Collection<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
