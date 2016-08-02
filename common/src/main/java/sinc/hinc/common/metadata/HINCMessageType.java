/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.common.metadata;

/**
 *
 * @author hungld
 */
public enum HINCMessageType {
    // broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
// broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
        SYN_REQUEST,
        SYN_REPLY,
        // unicast:local->global, local manager register it self 
        local_register,
        // unicast: Client->local, query information from local regarding to SD Gateway or NVF
        RPC_QUERY_SDGATEWAY_LOCAL,
        RPC_QUERY_NFV_LOCAL,
        // unicast: Client->global, query information from global (which include relationship)
        RPC_QUERY_INFORMATION_GLOBAL,
        // unicast: Client->local, send a control command to local
//        RPC_CONTROL_LOCAL,
        // unicast: Client->global, send a control command to global
        CONTROL,
        CONTROL_RESULT,
        // unicast/broadcast: Client--> local: subscribe the changes in the gateway
        SUBSCRIBE_SDGATEWAY_LOCAL,
        SUBSCRIBE_SDGATEWAY_LOCAL_SET_PARAM,
        // unicast: local/global --> client: send back the response
        UPDATE_INFORMATION
}