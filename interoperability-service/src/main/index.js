const express = require("express");
const app = express();
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const swaggerDocument = YAML.load('./main/openapi/openapi3.yaml');
const router = require("./router");
const bodyParser = require('body-parser');
const config = require('../config');

const PORT = config.SERVER_PORT;
const HOST = '0.0.0.0';



app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
app.use('/interoperability', router);
app.use(bodyParser.json());

app.listen(PORT);
console.log(`Running on http://localhost:${PORT}/api-docs`);