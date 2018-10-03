const mongoose = require("mongoose");
const MetadataModel = require("./interoperability_metadata/interoperability_metadata_schema").Metadata;
const SliceInformation = require("./slice_information/slice_information_schema").Slice;


const Schema = mongoose.Schema;
const ObjectId = Schema.ObjectId;

const Bridge = new Schema({
    id:ObjectId,
    slice: SliceInformation,
    metadata: MetadataModel,
    inputResourceId: String,
    outputResourceId: String
});



module.exports = {
    InteroperabilityBridgeSchema:Bridge
};