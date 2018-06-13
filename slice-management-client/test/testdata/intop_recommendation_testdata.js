
exports.solutionResources_test_0_0 = function (){
    let resources = [];
    return resources;
};

exports.solutionResources_test_0_1 = function (){
    let resources = [];
    //csv to json
    resources.push(csvToJsonArtefact("transformer", mqttProtocol()));
    return resources;
};

exports.solutionResources_test_0_2 = function (){
    //csv to json
    //broker
    let resources = [];
    resources.push(csvToJsonArtefact("transformer", mqttProtocol()));
    resources.push(mqttBroker("broker", mqttProtocol()));
    return resources;
};

exports.solutionResources_test_0_3 = function (){
    //mqtt broker
    let resources = [];
    resources.push(mqttBroker("broker", mqttProtocol()));
    return resources;
};

exports.solutionResources_test_0_4 = function (){
    //reduction test case --> nothing needed
    let resources = [];
    return resources;
};

exports.solutionResources_test_0_5 = function (){
    //http poller
    //http buffer
    let resources = [];
    resources.push(httpPoller("poller", jsonFormat()));
    resources.push(httpBuffer("buffer", jsonFormat()));
    return resources;
};

exports.solutionResources_test_1_0 = function (){
    let resources = [];
    return resources;
};

exports.solutionResources_test_1_1 = function (){
    //broker
    //csv to json
    let resources = [];
    resources.push(csvToJsonArtefact("transformer", mqttProtocol()));
    resources.push(mqttBroker("broker", mqttProtocol()));
    return resources;
};

exports.solutionResources_test_1_2 = function (){
    //broker
    //csv to json
    let resources = [];
    resources.push(csvToJsonArtefact("transformer", mqttProtocol()));
    resources.push(mqttBroker("broker", mqttProtocol()));
    return resources;
};



//******************************************************************************* resource functions
function source(name, push_pull, protocol, format){
    return {
        name:name,
        source: [],
        target: [],
        metadata:{
            resource:{category:"iot"},
            inputs: [],
            outputs:[
                {
                    push_pull:push_pull,
                    protocol:protocol,
                    dataformat:format
                }
            ]
        }
    };
}

function dest(name, push_pull, protocol, format){
    return {
        name:name,
        source: [],
        target: [],
        metadata:{
            resource:{category:"iot"},
            inputs:[
                {
                    push_pull:push_pull,
                    protocol:protocol,
                    dataformat:format
                }
            ],
            outputs:[]
        }
    };
}

function csvToJsonArtefact(name, protocol){
    return {
        name:name,
        source: [],
        target: [],
        metadata:{
            resource:{category:"iot"},
            inputs:[
                {
                    push_pull:"push",
                    protocol:protocol,
                    dataformat:csvFormat()
                }
            ],
            outputs:[
                {
                    push_pull:"push",
                    protocol:protocol,
                    dataformat:jsonFormat()
                }
            ]
        }
    };
}

function httpPoller(name, dataformat){
    return {
        name:name,
        source: [],
        target: [],
        metadata:{
            resource:{category:"iot"},
            inputs:[
                {
                    push_pull:"pull",
                    protocol:httpProtocol(),
                    dataformat:dataformat
                }
            ],
            outputs:[
                {
                    push_pull:"push",
                    protocol:mqttProtocol(),
                    dataformat:dataformat
                }
            ]
        }
    };
}

function httpBuffer(name, dataformat){
    return {
        name:name,
        source: [],
        target: [],
        metadata:{
            resource:{category:"iot"},
            inputs:[
                {
                    push_pull:"push",
                    protocol:mqttProtocol(),
                    dataformat:dataformat
                }
            ],
            outputs:[
                {
                    push_pull:"pull",
                    protocol:httpProtocol(),
                    dataformat:dataformat
                }
            ]
        }
    };
}

function mqttBroker(name, mqttprotocol){
    return {
        name:name,
        source:[],
        target:[],
        metadata: {
            resource: {
                category:"network_funciton",
                prototype: "messagebroker",
                protocols: [mqttprotocol],
                topics: [mqttprotocol.topic]
            }
        }
    }
}



function amqpBroker(name, amqpprotocol){
    return {
        name:name,
        source:[],
        target:[],
        metadata: {
            resource: {
                category:"network_funciton",
                prototype: "messagebroker",
                protocols: [amqpprotocol],
                queues:[amqpprotocol.queue],
                exchanges:[amqpprotocol.exchange]
            }
        }
    }
}

function mqttProtocol(){
    return {uri:"mqtt://test:1883",
        protocol_name:"mqtt",
        topic:"basic_test",
        qos:0}
}


function amqpProtocol() {
    return {
        uri:"amqp://test:5672",
        protocol_name:"amqp",
        queue:"testqueue",
        exchange:"testexchange",
    }
}


function httpProtocol(){
    return {
        uri: "http://test:80",
        protocol_name: "http",
        http_method: "get"
    }
}

function csvFormat() {
    return {encoding:"utf-8",
        dataformat_name:"csv"}
}

function jsonFormat() {
    return {encoding:"utf-8",
        dataformat_name:"json"}
}