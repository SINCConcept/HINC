const axios = require('axios');

var mqttplugin_config = require('config');
var config = mqttplugin_config.get('mqttadaptor');

/**
 * gets the available resources from the provider
 */

function getItems(settings){
    return axios.get(`${config.ENDPOINT}/mosquittobroker/list`).then((res) => {
        let brokers = res.data;
        let resources = [];
        brokers.forEach((broker) => {
            resources.push(_brokerToResource(broker));
        });

        return resources;
    });
}

function _brokerToResource(broker){

    let ingressAccessPoint = {
        applicationProtocol: "MQTT",
        host: broker.location,
        port: 1883,
        accessPattern: "PUBSUB",
        networkProtocol: "IP",
        qos: 0,
        topics: []
    };

    let resource = {
        uuid: broker.brokerId,
        plugin: 'mosquittobroker',
        providerUuid: config.ADAPTOR_NAME,
        resourceType: 'NETWORK_FUNCTION_SERVICE',
        name: `mosquitto broker`,
        controlPoints: [],
        dataPoints: [],
        type: 'BROKER',
        location: null,
        parameters:{
            ingressAccessPoints:[ingressAccessPoint],
            egressAccessPoints: [],
        },
        metadata: {
            createdAt: broker.createdAt,
        },
    }
    return resource
}

module.exports.getItems = getItems;
