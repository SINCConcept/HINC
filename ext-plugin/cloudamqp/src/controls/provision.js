const axios = require('axios');
const config = require('../../config');
const qs = require("qs");


function provision(resource){
    let controlResult = null;
    let broker = null;

    let provisionParameters = {
        name: resource.parameters.name,
        plan: "lemur",
        region: "google-compute-engine::europe-west1",
    }

    let options = {
        auth: { username: process.env.AMQP_KEY},
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }
    return axios.post(`${config.ENDPOINT}`, qs.stringify(provisionParameters), options).then((res) => {
        let item = res.data;
        return axios.get(`${config.ENDPOINT}/${item.id}`, {auth: { username: process.env.AMQP_KEY}}).then((res) => res.data);
    }).then((broker) => {
        console.log(broker);
        let ingressAccessPoint = {
            applicationProtocol: "AMQP",
            host: broker.url,
            port: null,
            accessPattern: "PUBSUB",
            networkProtocol: "IP",
            qos: 0,
            topics: []
        };

        resource.parameters.ingressAccessPoints[0] = ingressAccessPoint;
        resource.metadata = {
            plan: broker.plan,
            region: broker.region,
            apikey: broker.apikey,
            name: broker.name
        }
        resource.uuid = broker.id;

        let controlResult = {
            status: 'SUCCESS',
            rawOutput: JSON.stringify(resource),
            resourceUuid: resource.uuid,
        };
        console.log(controlResult);
        return controlResult

    }).catch((err) => {
        console.log('control execution failed');
        console.error(err.response)
        controlResult = {
            status: 'FAILED',
            rawOutput: err.response,
        };
        return controlResult;
    });
}

module.exports.provision = provision;