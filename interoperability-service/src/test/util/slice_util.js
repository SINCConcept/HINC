exports.resourceCount = function(slice){
    return Object.keys(slice.resources).length;
};

exports.isConnected = function(slice, source, destination){
    let connectivityName = null;


    //one element in source.target == one element dest.target
    for(let i = 0; i < source.target.length; i++){
        if(destination.source.indexOf(source.target[i])>-1){
            connectivityName = source.target[i];
            break;
        }
    }

    if(connectivityName === null){
        return false;
    }

    //slice[connectivity.in].name == source.name
    if(slice.resources[slice.connectivities[connectivityName].in].name !== source.name){
        return false
    }

    if(slice.resources[slice.connectivities[connectivityName].out].name !== destination.name){
        return false
    }

    return true;

};

exports.contains = function(slice, resourceName){
    return Object.keys(slice.resources).map(function(r){return slice.resources[r].name}).indexOf(resourceName)>-1;
};

exports.deepcopy = function(obj){
    return JSON.parse(JSON.stringify(obj));
};



exports.sliceConnect = function(slice, source, dest, connectivityname){
    slice.connectivities[connectivityname]={in:source.name, out:dest.name};
    source.target.push(connectivityname);
    dest.source.push(connectivityname);
};