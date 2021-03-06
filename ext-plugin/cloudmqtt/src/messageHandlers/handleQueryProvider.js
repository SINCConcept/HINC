const providerAdaptor = require('../adaptors/providers');

function handleQueryResources(msg){
    let reply = { 
        msgType: 'UPDATE_PROVIDER',
        senderID: 'b2807e97-2361-4518-86f8-3e7eba1da328',
        receiverID: null,
        payload: '',
        timeStamp: Math.floor((new Date()).getMilliseconds()/1000),
        uuid: '732330'
    }

    return providerAdaptor.getProvider().then((provider) => {
        reply.payload = JSON.stringify(provider);
        reply.destination = msg.reply;
        return reply;
    });
}

module.exports = handleQueryResources