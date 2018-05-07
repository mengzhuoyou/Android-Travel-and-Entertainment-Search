/* Listen to Android */
/*var http = require('http');  
var server = http.createServer();  
var querystring = require('querystring');  
  
  
var postResponse = function(req, res) {  
    var info =''; 
    console.log(req); 
    req.addListener('data', function(chunk){  
        info += chunk;  
     }) 
    .addListener('end', function(){  
        info = querystring.parse(info);  
        res.setHeader('content-type','text/html; charset=UTF-8');//响应编码  
        res.end('Hello World POST ' + info.name,'utf8');  
     })  
    console.log(req);
}  
  
var getResponse = function (req, res){  
  res.writeHead(200, {'Content-Type': 'text/plain'});  
  var name = require('url').parse(req.url,true).query.name  
  res.end('Hello World GET ' + name,'utf8');  
}  
  
var requestFunction = function (req, res){  
    req.setEncoding('utf8');//请求编码  
    console.log("aaaa="+req);
  
    if (req.method == 'POST'){  
        return postResponse(req, res);  
    }  
  
    return getResponse(req, res);  
}  
  
server.on('request',requestFunction);  
server.listen(8080, "127.0.0.1"); 

console.log('Server running at http://127.0.0.1:8080/');  
*/
var express = require("express");
var cors = require('cors');
var app     = express();
var path    = require("path");
var request = require('request');
app.use(cors());
app.use(express.static(path.join(__dirname)));


//var googleKey = "AIzaSyDP0xBlUGViCvfHPmEJimqqO00KJYQYDRc";
//var yelpKey = "cEq5vLzjdXhziwMEWbJYMU79-RWjO3uaorXi94MhhQBV2RjPzeMFhVUCDjywnIPXoXKTzxTdM1bUZ4YuznDmlP8-go2Z20PfndoTiHTTNswovl6tVysIoaH_WsLCWnYx";


var bodyParser = require('body-parser');
// create application/json parser
var jsonParser = bodyParser.json();
// create application/x-www-form-urlencoded parser
//var urlencodedParser = bodyParser.urlencoded({ extended: false });

app.set('view engine', 'pug');
app.set('views', './views');

// for parsing application/json
app.use(bodyParser.json()); 

// for parsing application/xwww-
app.use(bodyParser.urlencoded({ extended: true })); 
//form-urlencoded

app.get('/test',function(req,res){
	console.log(req.query);
	res.send("over");
  //res.sendFile(path.join(__dirname+'/HW8_tmhIqOON.html'));
  //__dirname : It will resolve to your project folder.
});


var key = "AIzaSyAAzSJGd1bAyAnqEHBjuPPk2spcMStbXWk";
// Search
app.get('/search', function(req, res){
	//res.header("Access-Control-Allow-Origin", "*");
  	//res.header("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
  	//res.header("Access-Control-Allow-Methods", "GET, PUT, POST");
    //console.log(req.body);

    function getDetail(){
      url ='https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='+location+'&radius='+radius+'&type='+form.category+'&keyword='+form.keyword+'&key='+key;
      console.log("/search");
      console.log(url);
      request(url , function (error, response, body) {
      if (!error && response.statusCode == 200) {
      		res.setHeader('content-type','text/html; charset=UTF-8');//响应编码  
        	//res.end(JSON.stringify([JSON.parse(body),lat,lon]),'utf8');  
        	res.end(JSON.stringify({
        		"res":JSON.parse(body),
        		"lat":lat,
        		"lon":lon
        	}),'utf8');
          	//res.send(JSON.stringify([JSON.parse(body),lat,lon]));
          //res.send(body);
        }else
          console.log("Google Nearbysearch API Website Wrong");
       });
    }

    //console.log(req.body);
    var form = req.query;
    //console.log(form);
    var radius = form.distance;
    radius = radius * 1609.34;
    var location = form.location;
    var lat,lon;
    var url = "";
    if (location == 'Here'){
    	url = 'http://ip-api.com/json';
    	request(url, function (error, response, body) {
		    if (!error && response.statusCode == 200) {
		    	var obj = JSON.parse(body);
		    	location = obj.lat+","+obj.lon;
		        lat = obj.lat;
		        lon = obj.lon;
		        getDetail();
		    }
		    else{
	          	console.log("Ip-api Website Error, Use Stored value");
	          	location = "34.0223519,-118.285117";
	          	lat = 34.0223519;
	          	lon = -118.285117;
	          	getDetail();
        	}
		});
    }
    else {
    	url = 'https://maps.googleapis.com/maps/api/geocode/json?address='+location+'&key='+key;
    	request(url , function (error, response, body) {
		    if (!error && response.statusCode == 200) {
	          var obj = JSON.parse(body);
	          lat = obj.results[0].geometry.location.lat;
	          lon = obj.results[0].geometry.location.lng;
	          location = lat+","+lon;
	          getDetail();
        	}else
		      console.log("Google Geocode API Website Wrong");
		});
    }
});

app.get('/search/next', function(req, res){
  url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken='+req.query.next_page_token+'&key='+key;
  request(url , function (error, response, body) {
    if (!error && response.statusCode == 200) {
    	console.log("/search/next");
    	res.setHeader('content-type','text/html; charset=UTF-8');//响应编码  
        	//res.end(JSON.stringify([JSON.parse(body),lat,lon]),'utf8');  
        res.end(body);
    }else
      console.log("Google Nearbysearch API Website Wrong");
    });
});

app.get('/search/detail', function(req, res){
  url = 'https://maps.googleapis.com/maps/api/place/details/json?placeid='+req.query.place_id+'&key='+key;
  console.log(url);
  request(url , function (error, response, body) {
    if (!error && response.statusCode == 200) {
    	console.log('/search/detail');
    	res.setHeader('content-type','text/html; charset=UTF-8');//响应编码  
        	//res.end(JSON.stringify([JSON.parse(body),lat,lon]),'utf8');  
        res.end(body);
    }else
      console.log("Google Nearbysearch API Website Wrong");
    });
});

// yelp
app.get('/yelp', function(req, res){
  'use strict';
  const yelp = require('yelp-fusion');
  var apiKey = "GXbhLuPSJjlpWiTl9rweDD4H9fnZqc6imBHQBfG2cOHzrBtiIu-f8DoXyBA6NTh_l-OREr9RFLg_QifKXH9BaFe3HysfCkTYEg5b_8goCsOgvzxbK5IJ8z4OQQvDWnYx";
  var client = yelp.client(apiKey);
  //var data = req.query;
  var data = {
        name: req.query.name,
        address1: req.query.address1,
        address2: req.query.address2,
        city: req.query.city,
        state: req.query.state,
        country: req.query.country
    }
  console.log('/yelp');
  console.log(data);
  // matchType can be 'lookup' or 'best'
  client.businessMatch('best', data
    ).then(response => {
    //console.log(response.jsonBody);
    res.setHeader('content-type','text/html; charset=UTF-8');//响应编码  
      console.log(JSON.stringify(response.jsonBody));
    //res.end(body);
    client.reviews(response.jsonBody.businesses[0].id).then(response => {
      res.end(JSON.stringify(response.jsonBody.reviews));
    }).catch(e => {
      res.end("NoReview");
      console.log("NoReview:",e);
    });
  }).catch(e => {
    res.end("UnMatch");
    console.log("UnMatch:",e);
  });
  


});


app.set('host', '127.0.0.1');
app.listen(8080); 

console.log("Running at Port 8080");