var template = {
    "admin": {
      "ip": "0.0.0.0",
      "port": 9991
    },
    "namers": [
      {
        "kind": "io.l5d.fs",
        "rootDir": "/disco"
      }
    ],
    "storage": {
      "kind": "io.l5d.inMemory",
      "namespaces": {
        "default": "/cluster   => /#/io.l5d.fs;\n/svc       => /cluster;\n"
      }
    },
    "interfaces": [
      {
        "kind": "io.l5d.thriftNameInterpreter",
        "ip": "0.0.0.0",
        "port": 4100
      },
      {
        "kind": "io.l5d.httpController",
        "ip": "0.0.0.0",
        "port": 4180
      }
    ]
  }

  module.exports = template;