FROM node:8

COPY . /master
WORKDIR /master

RUN npm install

ENV MONGODB_URL "mongodb://iotcloudexamples:ac.at.tuwien.dsg@iotcloudexamples-shard-00-00-pz2vu.mongodb.net:27017,iotcloudexamples-shard-00-01-pz2vu.mongodb.net:27017,iotcloudexamples-shard-00-02-pz2vu.mongodb.net:27017/sinc?ssl=true&replicaSet=IoTCloudExamples-shard-0&authSource=admin"
ENV COLLECTION "test"

CMD ["npm", "start"]