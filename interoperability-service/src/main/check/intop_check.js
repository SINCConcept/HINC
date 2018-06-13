const sliceToConnectionArray = require('../transform/slice_to_connection_array');
const graph_util = require('../transform/slice_to_graph');
const check_protocol = require('../check/check_protocol');
const check_dataformat = require('../check/check_dataformat');

const errorGenerator = require('../transform/error_generator_graph');


exports.check = function (slice) {

    let connections = sliceToConnectionArray.sliceToConnectionArray(slice);
    let errors = [];
    let warnings = [];
    let matches = [];

    connections.forEach(checkConnection(errors, warnings, matches));

    return {errors:errors, warnings:warnings, matches: matches};
};

exports.checkSingleConnection = function(connection){
    let errors = [];
    let warnings = [];
    let matches = [];

    let connections = [connection];
    connections.forEach(checkConnection(errors, warnings, matches));

    return {errors:errors, warnings:warnings, matches: matches};
};

exports.checkSlice = function(slice){
    let graph = graph_util.sliceToGraph(slice);
    let errors = [];
    let warnings = [];
    let matches = [];

    checkGraph(graph, errors, warnings, matches);

    return {errors:errors, warnings:warnings, matches: matches};
};

function checkGraph(graph, errors, warnings, matches){
    graph.startNodes.forEach(function (startNode){traverseGraph(startNode, graph, errors, warnings, matches)});
    let unmarkedNodes = graph_util.getUnmarkedNodes(graph);
    unmarkedNodes.forEach(function (node){traverseGraph(node, graph, errors, warnings, matches)});
}


function traverseGraph(node, graph, errors, warnings, matches) {
    node.marked = true;
    checkConnectedNodes(node, node, true, null, graph, errors, warnings, matches);

    let nextNodes = graph_util.nextNodes(graph, node);
    for(let i = 0; i<nextNodes.length;i++){
        if(nextNodes[i].marked===false){
            traverseGraph(nextNodes[i], graph, errors, warnings, matches);
        }
    }
}

function checkConnectedNodes(startNode, currentNode, directConnection, startOutput, graph, errors, warnings, matches) {
    let nextNodes = graph_util.nextNodes(graph,currentNode);


    for(let i = 0; i<nextNodes.length; i++){
        if(nextNodes[i] === startNode){
            return;
        }


        if(directConnection){
            startOutput = checkDirectConnection(startNode, nextNodes[i], graph, errors, warnings, matches);
        }else{
            indirectConnectionCheck(startNode, nextNodes[i], currentNode, startOutput, graph, errors, warnings, matches);
        }

        if(nextNodes[i].resource.metadata.resource.category === "network_function"){
            checkConnectedNodes(startNode, nextNodes[i], false, startOutput, graph, errors, warnings, matches);
        }
    }
}

function checkDirectConnection(sourceNode, targetNode, graph, errors, warnings, matches){
    console.log("check direct " + sourceNode.nodename + " "+  targetNode.nodename);

    let metadataConnectionChecks = [check_protocol.checkProtocols];
    let connection = getBestMetadataConnection(sourceNode, targetNode, metadataConnectionChecks);
    return connection.output;
}

function indirectConnectionCheck(sourceNode, targetNode, currentNode, sourceOutput, graph, errors, warnings, matches){
    console.log("indirect check " + sourceNode.nodename + " "+  targetNode.nodename);
    let metadataConnectionChecks = [check_protocol.checkProtocols];
    //TODO ignore errors and warnings
    let networkToTarget = getBestMetadataConnection(currentNode, targetNode, metadataConnectionChecks);

    //TODO check sourceOutput, networkToTarget.input
}

function checkMetadataConnection(sourceNode, sourceOutput, targetNode, targetInput, checkFunctionArray){
    let errors = [];
    let warnings =[];


    for(let f = 0; f < checkFunctionArray.length; f++){
        let checkfunction = checkFunctionArray[f];
        checkfunction(sourceNode.resource.metadata, sourceOutput,
                        targetNode.resource.metadata, targetInput,
                        errors, warnings);
    }
}

function getBestMetadataConnection(sourceNode, targetNode, checkFunctionArray){
    let matchingInOutputs = protocolMatchingOutInputs(sourceNode.resource, targetNode.resource);

    if(matchingInOutputs.length === 0){
        return {input:null, output:null};
    }

    let bestConnection = matchingInOutputs[0];

    //TODO for now it is assumed that max. one connection is optimal

    for(let i = 0; i < matchingInOutputs.length; i++){
        matchingInOutputs[i].errors = [];
        matchingInOutputs[i].warnings = [];


        for(let f = 0; f < checkFunctionArray.length; f++){
            let checkfunction = checkFunctionArray[f];
            checkfunction(sourceNode.resource, matchingInOutputs[i].output, targetNode.resource, matchingInOutputs[i].input,
                matchingInOutputs[i].errors , matchingInOutputs[i].warnings);
        }
        /*check_protocol.checkProtocols(matchingInOutputs[i].output, matchingInOutputs[i].input, matchingInOutputs[i].errors ,
            matchingInOutputs[i].warnings);

        if(sourceNode.resource.metadata.resource.category !== "network_function" &&
            targetNode.resource.metadata.resource.category === "network_function") {
            check_dataformat.checkDataFormat(matchingInOutputs[i].output, matchingInOutputs[i].input, matchingInOutputs[i].errors,
                matchingInOutputs[i].warnings);
        }*/


        if(matchingInOutputs[i].errors.length <= 0 &&
            matchingInOutputs[i].warnings.length <= 0 ){
            bestConnection = matchingInOutputs[i];
            break;
        }

        // save connection with lowest number of errors and warnings
        if(matchingInOutputs[i].errors.length<bestConnection.errors.length
            || (matchingInOutputs[i].errors.length===bestConnection.errors.length &&
                matchingInOutputs[i].warnings.length<bestConnection.warnings.length)){
            bestConnection = matchingInOutputs[i];
        }
    }
    return bestConnection;
}


function checkConnection(errors, warnings, matches) {
    return function (connection) {
        let matchingInOutputs = protocolMatchingOutInputs(connection.source, connection.target);
        let matchAvailable = false;

        if(matchingInOutputs.length === 0){
            //TODO add more useful error object
            errors.push("error");
            return;
        }

        let bestConnection = matchingInOutputs[0];

        for(let i = 0; i < matchingInOutputs.length; i++){
            matchingInOutputs[i].errors = [];
            matchingInOutputs[i].warnings = [];

            check_protocol.checkProtocols(matchingInOutputs[i].output, matchingInOutputs[i].input, matchingInOutputs[i].errors ,
                matchingInOutputs[i].warnings);
            check_dataformat.checkDataFormat(matchingInOutputs[i].output, matchingInOutputs[i].input, matchingInOutputs[i].errors ,
                matchingInOutputs[i].warnings);


            if(matchingInOutputs[i].errors.length <= 0 &&
                matchingInOutputs[i].warnings.length <= 0 ){
                //TODO add more useful match object
                matches.push(matchingInOutputs[i].output.protocol.protocol_name);
                matchAvailable = true;
                break;
            }

            // save connection with lowest number of errors and warnings
            if(matchingInOutputs[i].errors.length<bestConnection.errors.length
                || (matchingInOutputs[i].errors.length===bestConnection.errors.length &&
                    matchingInOutputs[i].warnings.length<bestConnection.warnings.length)){
                bestConnection = matchingInOutputs[i];
            }
        }

        if(!matchAvailable){
            if(bestConnection.errors.length>0){
                //TODO add more useful error object
                errors.push("error");
            }else if (bestConnection.warnings.length>0){
                //TODO add more useful warning object
                warnings.push("warning");
            }
        }
    }
}


function protocolMatchingOutInputs(source, target){
    let protocolmatches = [];
    let outputs = [];
    let inputs = [];

    if(source.metadata.outputs){
        outputs = source.metadata.outputs;
    }
    if(target.metadata.inputs){
        inputs = target.metadata.inputs;
    }

    addBrokerOutinputs(inputs, target, "input");
    addBrokerOutinputs(outputs, source, "output");


    for(let i = 0; i<outputs.length; i++){
        for(let j=0; j<inputs.length; j++){
            if(outputs[i].protocol.protocol_name === inputs[j].protocol.protocol_name){
                protocolmatches.push({output: outputs[i], input:inputs[j]});
            }
        }
    }

    return protocolmatches;
}


function addBrokerOutinputs(outinputs, resource, type){
    if(resource.metadata.resource.category === "network_function"){
        if(resource.metadata.resource.type.prototype === "messagebroker"){
            for(let i = 0; i < resource.metadata.resource.type.protocols.length; i++){

                //mqtt || amqp
                if(resource.metadata.resource.type.protocols[i].protocol_name === "mqtt" ||
                    resource.metadata.resource.type.protocols[i].protocol_name === "mqtts"){
                    for(let m = 0; m< resource.metadata.resource.type.topics.length; m++){
                        let outinput = {};
                        outinput.push_pull = "push";
                        outinput.protocol = resource.metadata.resource.type.protocols[i];
                        outinput.topic = resource.metadata.resource.type.topics[m];
                        outinputs.push(outinput);
                    }
                }
                if(resource.metadata.resource.type.protocols[i].protocol_name === "amqp" ||
                    resource.metadata.resource.type.protocols[i].protocol_name === "amqps"){

                    if(type === "input" && resource.metadata.resource.type.exchanges) {
                        for (let m = 0; m < resource.metadata.resource.type.exchanges.length; m++) {
                            let outinput = {};
                            outinput.push_pull = "push";
                            outinput.protocol = resource.metadata.resource.type.protocols[i];
                            outinput.exchange = resource.metadata.resource.type.exchanges[m];
                            outinputs.push(outinput);
                        }
                    }else if(type === "output" && resource.metadata.resource.type.queues) {
                        for (let m = 0; m < resource.metadata.resource.type.queues.length; m++) {
                            let outinput = {};
                            outinput.push_pull = "push";
                            outinput.protocol = resource.metadata.resource.type.protocols[i];
                            outinput.queue = resource.metadata.resource.type.queues[m].name;
                            outinputs.push(outinput);
                        }
                    }
                }
            }
        }
    }
}
