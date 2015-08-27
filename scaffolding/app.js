var express = require('express');
var app = express();
var expressWs = require('express-ws')(app)
var fs = require('fs');
var sha256 = require("crypto-js/sha256");
var lastBody = null;

// "static" HTML files
app.use(express.static(__dirname + '/public'))

var concat = require('concat-stream');
app.use(function(req, res, next) {
  req.pipe(concat(function(data) {
    req.body = data;
    next();
  }));
});

// clients make WS connections to /data to submit their data
app.ws('/data', function(ws, req) {
  var json = req.body.toString('utf-8');
  liveData.clients.forEach(function (client) { client.send(json); });
});

app.ws('/live/data', function(ws, req) { });
// the live data from all clients
var liveData = expressWs.getWss('/live/data');

// debugging endpoint
app.get('/foo', function(req, res) {
  fs.readFile( __dirname + '/example.json', function (err, data) {
    liveData.clients.forEach(function (client) { client.send(data.toString('utf-8')); });
    res.send("{}");
  });
});

app.listen(process.env.PORT || 8080);
