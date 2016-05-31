#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

ESP8266WebServer server(80);
const char* ssid = "AQUA";
const char* password = "patlas1992";

void setup() {
  // put your setup code here, to run once:
  pinMode(14,OUTPUT);


  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);
/*
 // connecting to existing ap
 WiFi.begin(ssid, password);

 uint8_t toggle = 0;
 while(WiFi.status() != WL_CONNECTED)
 {
  toggle^=1;
  digitalWrite(14, toggle);
  delay(200);
 }
*/
 server.on("/", [](){
  server.send(200, "text/plain", "This is an index page.");
 });

server.begin();
}

uint8_t toggle = 0;
void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(14, toggle);
  delay(200);
  toggle^=1;
  server.handleClient();

}
