const assert = require('assert');

const util = require('../main/util/slice_util');
const check = require('../main/check/intop_check');
const recommendation = require('../main/recommendation/intop_recommendation');

const configModule = require('config');
let config = configModule.get('interoperability_service');

const basic_data = require('./testdata/testslices/basic_testslices');

const bts_testslice_0 = require('./testdata/testslices/bts_testslice0');
const bts_testslice_1 = require('./testdata/testslices/bts_testslice1');
const bts_testslice_2 = require('./testdata/testslices/bts_testslice2');


//TODO set solution resources
const solutionResourcesDB = require('./testdata/testslices/intop_recommendation_db_dump');

const MongoClient = require("mongodb").MongoClient;


describe("valencia slices - intop check", function(){
    it("01_protocol",function(){
        const testslice = require('../../client_testslices/valencia_intop/01_protocol');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("02_dataformat",function(){
        const testslice = require('../../client_testslices/valencia_intop/02_dataformat');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("03_datacontract_jurisdiction",function(){
        const testslice = require('../../client_testslices/valencia_intop/03_datacontract_jurisdiction');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("04_datacontract_datarights",function(){
        const testslice = require('../../client_testslices/valencia_intop/04_datacontract_datarights');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("05_datacontract_pricing",function(){
        const testslice = require('../../client_testslices/valencia_intop/05_datacontract_pricing');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("06_qos_reliability",function(){
        const testslice = require('../../client_testslices/valencia_intop/06_qos_reliability');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("07_qos_messagefrequency",function(){
        const testslice = require('../../client_testslices/valencia_intop/07_qos_messagefrequency');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("08_qod_precision",function(){
        const testslice = require('../../client_testslices/valencia_intop/08_qod_precision');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
    it("09_qod_averagemeasurementage",function(){
        const testslice = require('../../client_testslices/valencia_intop/09_qod_averagemeasurementage');
        let result = check.checkSlice(testslice);
        assert.equal(result.errors.length, 0);
        assert.equal(result.matches.length, 1);
    });
});


describe("valencia slices - intop recommendation", function(){
    before(function() {
        recommendation.queryServices = function(query){
            return new Promise((resolve, reject) => {
                MongoClient.connect(config.MONGODB_URL, {useNewUrlParser: true} , function(err, db) {
                    if (err) return reject(err);
                    let dbo = db.db("recommendation_test");
                    dbo.collection("test").find(query).toArray(function(err, result) {
                        if (err) throw err;
                        db.close();
                        result.forEach(e=>{e._type = "resource"});
                        resolve(result);
                    });
                });
            })
        };

        let promises = [];
        promises.push(new Promise(function(resolve, reject){
            MongoClient.connect(config.MONGODB_URL, {useNewUrlParser: true}, function(err, db) {
                if (err) return reject(err);
                let dbo = db.db("recommendation_test");
                dbo.collection("test").insertMany(solutionResourcesDB, function(err, res) {
                    if (err) throw err;
                    console.log("Number of documents inserted: " + res.insertedCount);
                    db.close();
                    resolve();
                });
            });
        }));
        promises.push(new Promise(function(resolve, reject){
            MongoClient.connect(config.MONGODB_URL, {useNewUrlParser: true}, function(err, db) {
                if (err) return reject(err);
                let dbo = db.db("recommendation_test");
                dbo.createCollection("recommendations", function(err, res) {
                    if (err) throw err;
                    db.close();
                    resolve();
                });
            });
        }));
        return Promise.all(promises);
    });
    after(function() {
        let promises = [];
        promises.push(new Promise(function(resolve, reject){
            MongoClient.connect(config.MONGODB_URL, {useNewUrlParser: true}, function(err, db) {
                if (err) {db.close(); return reject(err);}
                let dbo = db.db("recommendation_test");
                dbo.collection("test").drop(function(err, delOK) {
                    db.close();
                    if (err) return reject(err);
                    if (delOK) console.log("Collection deleted");
                    resolve();
                });
            });
        }));
        promises.push(new Promise(function(resolve, reject){
            MongoClient.connect(config.MONGODB_URL, {useNewUrlParser: true}, function(err, db) {
                if (err) {db.close(); return reject(err);}
                let dbo = db.db("recommendation_test");
                dbo.collection("recommendations").drop(function(err, delOK) {
                    db.close();
                    if (err) return reject(err);
                    if (delOK) console.log("Collection deleted");
                    resolve();
                });
            });
        }));
        return Promise.all(promises);
    });

    it("01_protocol",function(){
        let slice = require('../../client_testslices/valencia_intop/01_protocol');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("02_dataformat",function(){
        let slice = require('../../client_testslices/valencia_intop/02_dataformat');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("03_datacontract_jurisdiction",function(){
        let slice = require('../../client_testslices/valencia_intop/03_datacontract_jurisdiction');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("04_datacontract_datarights",function(){
        let slice = require('../../client_testslices/valencia_intop/04_datacontract_datarights');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("05_datacontract_pricing",function(){
        let slice = require('../../client_testslices/valencia_intop/05_datacontract_pricing');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("06_qos_reliability",function(){
        let slice = require('../../client_testslices/valencia_intop/06_qos_reliability');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("07_qos_messagefrequency",function(){
        let slice = require('../../client_testslices/valencia_intop/07_qos_messagefrequency');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("08_qod_precision",function(){
        let slice = require('../../client_testslices/valencia_intop/08_qod_precision');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
    it("09_qod_averagemeasurementage",function(){
        let slice = require('../../client_testslices/valencia_intop/09_qod_averagemeasurementage');
        let old_slice = util.deepcopy(slice);
        //intopcheck returns no errors (and warnings)
        let checkresults = check.checkSlice(slice);
        let errors = checkresults.errors;
        assert.equal(errors.length, 0);
        //slice after recommendation equals before recommendation

        return recommendation.getRecommendationsWithoutCheck(slice, checkresults).then(function(result) {
            let slice = result.slice;
            let logs = result.logs;

            assert.deepEqual(slice, old_slice);

            //intopcheck returns 0 error
            errors = check.checkSlice(slice).errors;
            assert.equal(errors.length, 0);
        });
    });
});
