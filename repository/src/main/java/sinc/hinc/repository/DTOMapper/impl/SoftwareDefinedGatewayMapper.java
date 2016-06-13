/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.repository.DTOMapper.impl;

import sinc.hinc.repository.DTOMapper.DTOMapperInterface;
import sinc.hinc.model.VirtualComputingResource.SoftwareDefinedGateway;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author hungld
 */
public class SoftwareDefinedGatewayMapper implements DTOMapperInterface<SoftwareDefinedGateway> {

    @Override
    public SoftwareDefinedGateway fromODocument(ODocument doc) {
        System.out.println("Converting gateway: " + doc.toJSON());
        SoftwareDefinedGateway gw = new SoftwareDefinedGateway();
        gw.setUuid(String.valueOf(doc.field("uuid")));
        gw.setName(String.valueOf(doc.field("name")));
        return gw;
    }

    @Override
    public ODocument toODocument(SoftwareDefinedGateway object) {
        ODocument doc = new ODocument(SoftwareDefinedGateway.class.getSimpleName());
        doc.field("uuid", object.getUuid());
        doc.field("name", object.getName());
        return doc;
    }

}