/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.hinc.repository.DTOMapper.impl;


import at.ac.tuwien.dsg.hinc.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.hinc.repository.DTOMapper.DTOMapperInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author hungld
 */
public class ControlPointMapper implements DTOMapperInterface<ControlPoint>{

    @Override
    public ControlPoint fromODocument(ODocument doc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ODocument toODocument(ControlPoint object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}