const amqp = require('amqplib');
const messageHandler = require('./messageHandlers/handler');
//const config = require('../config');
var dockerprovider_config = require('config');
var config = dockerprovider_config.get('dockerizedservice');
const uuidv1 = require('uuid/v1');
let connection = null;
let channel = null;
let queue = config.ADAPTOR_NAME;
let exchange = config.EXCHANGE;
let routingKey = config.ADAPTOR_NAME;
let uri = config.URI;

function init(){
    console.log("Connect to rsiHub Local Management Service");
    console.log(`connecting to amqp broker at ${uri}`);
    return amqp.connect(uri).then((conn) => {
        console.log(`successfully conencted to ${uri}`);
        connection = conn;
        console.log('creating new channel');
        return connection.createChannel();
    }).then((ch) => {
        channel = ch;
        let setup = [
            channel.assertQueue(queue),
            channel.assertExchange(exchange, 'fanout'),
            channel.bindQueue(queue, exchange, routingKey),
            channel.consume(queue, _handleMessage),
        ];
        return Promise.all(setup);
    }).then(() => {
        return register(routingKey);
    });
}

function register(adaptorName){
    console.log("Register ", adaptorName, " with id ", config.ADAPTOR_UUID, " to rsiHubLocal");
    let payload = JSON.stringify({
        adaptorName,
    });

    let msg = {
        msgType: 'REGISTER_ADAPTOR',
        senderID: config.ADAPTOR_UUID,
        receiverID: null,
        payload: payload,
        timeStamp: Math.floor((new Date()).getMilliseconds()/1000),
        uuid: '',
    }

    publish(msg, exchange, "");
}

function publish(msg, exchange, routingKey, replyTo){
    console.log(msg)
    channel.publish(exchange, routingKey, new Buffer(JSON.stringify(msg)));
}

function sendToQueue(msg, queue, correlation){
    console.log(`sending msg to queue ${queue} with correlation ${correlation}`);
    console.log(msg);
    channel.sendToQueue(queue, new Buffer(JSON.stringify(msg)), {correlationId: correlation});
}

function _handleMessage(msg){
    if(msg === null) return;
    console.log("Adaptor receives message from rsiHubLocal");
    console.log(JSON.stringify(msg.properties, null, 2));
    let message = JSON.parse(msg.content.toString());
    console.log(JSON.stringify(message, null, 2));
    messageHandler.handle(message).then((reply) => {
        if(msg.properties.replyTo){
            sendToQueue(reply, msg.properties.replyTo, msg.properties.correlationId);
        }
        channel.ack(msg);
    });
}

module.exports = {
    init,
    publish,
};
