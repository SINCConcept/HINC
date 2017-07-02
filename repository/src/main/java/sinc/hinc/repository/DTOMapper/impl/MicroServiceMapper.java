/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.repository.DTOMapper.impl;

import com.orientechnologies.orient.core.record.impl.ODocument;
import sinc.hinc.model.VirtualComputingResource.MicroService;
import sinc.hinc.repository.DTOMapper.DTOMapperInterface;
import sinc.hinc.repository.Utils;

/**
 *
 * @author hungld
 */
public class MicroServiceMapper implements DTOMapperInterface<MicroService> {

    @Override
    public MicroService fromODocument(ODocument doc) {
        MicroService service = new MicroService();
        service.setEndpoint(String.valueOf(doc.field("endpoint")));
        service.setHostID(String.valueOf(doc.field("hostid")));
        service.setName(String.valueOf(doc.field("name")));
        service.setResourceID(String.valueOf(doc.field("resourceid")));
        service.setMeta(Utils.jsonToMap(String.valueOf(doc.field("meta"))));
        return service;
    }

    @Override
    public ODocument toODocument(MicroService object) {
        ODocument doc = new ODocument();
        doc.setClassName(MicroService.class.getSimpleName());

        doc.field("endpoint", object.getEndpoint());
        doc.field("hostid", object.getHostID());
        doc.field("name", object.getName());
        doc.field("resourceid", object.getResourceID());
        doc.field("meta", Utils.mapToJson(object.getMeta()));
        return doc;
    }

}