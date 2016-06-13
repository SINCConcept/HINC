/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.repository.DTOMapper.impl;


import sinc.hinc.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import sinc.hinc.repository.DTOMapper.DTOMapperInterface;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author hungld
 */
public class ControlPointMapper implements DTOMapperInterface<ControlPoint>{

    @Override
    public ControlPoint fromODocument(ODocument doc) {
        CapabilityMapper capa = new CapabilityMapper(ControlPoint.class);
        ControlPoint con = (ControlPoint) capa.fromODocument(doc);
        con.setInvokeProtocol(ControlPoint.InvokeProtocol.valueOf(doc.field("invokeprotocol").toString()));
        con.setPostData(String.valueOf(doc.field("postdata")));
        con.setReference(String.valueOf(doc.field("reference")));
        return con;
    }

    @Override
    public ODocument toODocument(ControlPoint object) {
        CapabilityMapper capa = new CapabilityMapper(ControlPoint.class);
        ODocument doc = capa.toODocument(object);
        doc.setClassName(ControlPoint.class.getSimpleName());
        doc.field("invokeprotocol", object.getInvokeProtocol().toString());
        doc.field("postdata", object.getPostData());
        doc.field("reference", object.getReference());
        return doc;
    }
    
}