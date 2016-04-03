/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.hinc.transformer.android;

import android.hardware.AndroidSensor;
import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import at.ac.tuwien.dsg.hinc.abstraction.transformer.GatewayResourceTransformationInterface;

/**
 * The transformer (should be renamed to DataPoint constructor later) that get data from the domain model and build the DataPoint
 *
 * @author hungld
 */
public class AndroidSensorTransformer implements GatewayResourceTransformationInterface<AndroidSensor> {

    @Override
    public AndroidSensor validateAndConvertToDomainModel(String data, String sourceData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, AndroidSensor.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public DataPoint updateDataPoint(AndroidSensor data) {
        DataPoint datapoint = new DataPoint(data.getmName(), data.getmName(), "type:" + data.getmType() + ",version:" + data.getmVersion());
        datapoint.setMeasurementUnit("default_unit_for_android_type:" + data.getmType());
        datapoint.setRate(data.getmMinDelay());
        datapoint.setDatatype("type:" + data.getmType());
        return datapoint;
    }

    // return null that means the resource have no such capability
    @Override
    public List<ControlPoint> updateControlPoint(AndroidSensor data) {
        return null;
    }

    @Override
    public ExecutionEnvironment updateExecutionEnvironment(AndroidSensor data) {
        return null;
    }

    @Override
    public CloudConnectivity updateCloudConnectivity(AndroidSensor data) {
        return null;
    }

}