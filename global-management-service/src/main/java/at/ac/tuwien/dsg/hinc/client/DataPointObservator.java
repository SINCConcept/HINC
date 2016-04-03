/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.hinc.client;

import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.DataPoint;

/**
 * This
 *
 * @author hungld
 */
public abstract class DataPointObservator {

    DataPoint observeMe;

    public DataPointObservator(DataPoint observeMe) {
        this.observeMe = observeMe;
    }

    public abstract void onChange(DataPoint newValue);

}
