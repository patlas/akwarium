#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiClient.h>
#include <SPI.h>
#include <SD.h>


#include "data_type.h"


ESP8266WebServer server(80);
const char* ssid = "AQUA";
const char* password = "patlas1992";
const int SD_CS = 4;

File root;

void handleNotFound2(){
  String message = "";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET)?"GET":"POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i=0; i<server.args(); i++){
    message += " NAME:"+server.argName(i) + "\n VALUE:" + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  Serial.println(message);
}

void printDirectory(File dir, int numTabs)
{
  while (true)
  {
    File entry = dir.openNextFile();
    if (! entry)
    {
      if (numTabs == 0)
        Serial.println("** Done **");
      return;
    }
    for (uint8_t i = 0; i < numTabs; i++)
      Serial.print('\t');
    Serial.print(entry.name());
    if (entry.isDirectory())
    {
      Serial.println("/");
      printDirectory(entry, numTabs + 1);
    }
    else
    {
      Serial.print("\t\t");
      Serial.println(entry.size(), DEC);
    }
    entry.close();
  }
}

int sendFile(File fd, String type)
{
  char buff[512];
  int s = 0;
  server.setContentLength(CONTENT_LENGTH_UNKNOWN); //??
  server.send(200, type, "");
  //WiFiClient client = server.client();
  //
  do
  {
    s = fd.read(buff,512);
    server.sendContent_P(buff, s);
  } while(s>0);
}

void handleNotFound(){
  String fname = server.uri();
  fname = fname.substring(1);

  Serial.println("Opening file: "+fname);
  /*if(!SD.exists(fname.c_str()))
  {
    String message = "File "+ fname +" does not exists!";
    server.send(404, "text/plain", message);
    Serial.println(message);
    return;
  }*/

  String type = getMIME(fname);
  Serial.println("MIME type: "+type);
  //File fd = SD.open("INDEX.HTML");//fname.c_str());//, FILE_READ);
  File fd;
  root = SD.open("/");
  if(!root)
  {
    Serial.println("Cannot open root!");
    return;
  }

  printDirectory(root, 0);
  root.close();
  
  //server.setContentLength(CONTENT_LENGTH_UNKNOWN);
  //server.send(200, type, "");
  //WiFiClient client = server.client();
  //server.sendContent("[");
  String ofname = nameLongToShort(fname);
  Serial.println(ofname);
  fd =SD.open(ofname);
  if(!fd)
  {
    Serial.println("Cannot open file!");
    return;
  }
  Serial.println(fd.name());

  sendFile(fd, type);
  Serial.println("File send");

  fd.close();





  
}


void startDefaultAP()
{
//  WiFi.stop();
  delay(500);
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);
}
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


/*replay_server(...)
{
	//open file from uri
	//ex = check extension
	//size  = getFileSize
	
	
}*/



// Update these with values suitable for your network.
//IPAddress ip(192,168,0,128);  //Node static IP
//IPAddress gateway(192,168,0,1);
//IPAddress subnet(255,255,255,0);

IPAddress stringToAddr(String _addr)
{
  int bIndex = 0;
  int address[4];
  int subIndex = 0;
  for(int i=0; i<_addr.length(); i++)
  {
    if(_addr.charAt(i) == '.')
    {
      address[bIndex] = (_addr.substring(subIndex,i-1)).toInt();
      subIndex = i+1;
      bIndex++;
    }
  }
  IPAddress addr(address[0], address[1], address[2], address[3]);
  return addr;
}

//not tested!!!
void connectCustomAP(char *ssid, char *pass, String _ip, String _gateway, String _subnet)
{
//  WiFi.stop();
  delay(500);

  WiFi.begin(ssid, pass);
  WiFi.config(stringToAddr(_ip), stringToAddr(_gateway), stringToAddr(_subnet));

  //jezeli po 10 probach sie nie uda odpalic default (zapalic diode od efault anie network)
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("WiFi connected, using IP address: ");
  Serial.println(WiFi.localIP());
  //zapisac na karcie ze podlaczamy sie do sieci - zapisac jej parametry adres itd
}

void setup() {
  Serial.println("Setup begined");
  // put your setup code here, to run once:
 //pinMode(14,OUTPUT);
  Serial.begin(115200);
  startDefaultAP();
/*
 server.on("/", [](){
  server.send(200, "text/html", "<html><head><title>TEST</title><link rel='stylesheet' href='a.css'></head><body>This is an index page.</body></html>");
  Serial.println("Dziala");
  String test = "TEST";
  Serial.println(test);
  Serial.println(server.uri());
 });

 //server.on("/",HTTP_GET,[](){
 server.on("/a.css",[](){
  Serial.println("Ask for: \n");
  Serial.println("args count:"+server.args());
  Serial.println(server.arg(0));
  //if(server.arg(0).equals("a.css"))
 // {
    server.send(200,"text/css","body{background:red;}");
  Serial.println(server.header(0));
  //}
 });
*/
server.onNotFound(handleNotFound);
Serial.println("Starting server");
server.begin();
Serial.println("SERVER BEGINED");
if(SD.begin(SD_CS))
{
  Serial.println("SD communication OK!");
  //SD.mkdir("ad");
  //Serial.println("Created");
}
else
  Serial.println("SD ERROR");

}

uint8_t toggle = 0;



void loop() {
  // put your main code here, to run repeatedly:
//  digitalWrite(14, toggle);
//  delay(200);
//  toggle^=1;
  server.handleClient();

}
