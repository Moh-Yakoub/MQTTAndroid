var mqtt = require("./MQTT.js/lib/mqtt");

//https://github.com/adamvr/MQTT.js

mqtt.createServer(function(client){
var self=this;
if(!self.clients)self.clients={};
if(!self.client_topics)self.client_topics={};

//
client.on('connect',function(packet){
client.connack({returnCode:0});
client.id = packet.client;
self.clients[client.id]=client;
console.log(client.id+':'+'connected');
});

//
client.on('subscribe',function(packet){
//getting id
var granted=[];
for (var i = 0; i < packet.subscriptions.length; i++) {
      var sub = packet.subscriptions[i]
      granted.push(sub.qos);
}
client.suback({messageId:packet.messageId,granted:granted});
self.client_topics[client.id]=packet.subscriptions[0]['topic'];
console.log(client.id+':'+'subscribed to :'+packet.subscriptions[0]['topic']);
});


client.on('publish',function(packet){
var topic = packet['topic'];
for(var k in self.client_topics){
console.log(k);
if(self.client_topics[k]==topic)self.clients[k].publish({topic:packet.topic,payload:packet.payload});
}
});

 client.on('pingreq', function(packet) {
    client.pingresp();
  });

  client.on('disconnect', function(packet) {
    client.stream.end();
console.log(client.id+":"+"_disconnected");
  });

  client.on('close', function(err) {
    delete self.clients[client.id];
  });

  client.on('error', function(err) {
    client.stream.end();
    util.log('error!');
  });

}).listen(1833);