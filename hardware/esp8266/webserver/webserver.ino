
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiClient.h>
#include <SPI.h>
//#include <SD.h>
#include "data_type.h"

#include "SdFat/SdFat.h"
SdFat sd;
SdFile file;
SdFile dirFile;
ESP8266WebServer server(80);
const char* ssid = "AQUA";
const char* password = "patlas1992";
const int SD_CS = 4;

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

void sendFile(SdFile &fd, String type)
{
  char buff[512];
  int s = 0;
  if(fd.fileSize() <=0 ) 
  {
    return;
    fd.close();
  }
  server.setContentLength(CONTENT_LENGTH_UNKNOWN /*fd.fileSize()*/); //??
  server.send(200, type, "");

  do
  {
    s = fd.read(&buff[0],512);
    if(s<0)
    {
      Serial.println("Read error");
      fd.close();
      return;
    }
    //Serial.write(&buff[0],s);
    server.sendContent_P(&buff[0], s);
  } while(s>0);
  
}

void handleNotFound(){
  String fname = server.uri();
  //fname = fname.substring(1);

  Serial.println("Trying to open file: "+fname);
  /*if(!SD.exists(fname.c_str()))
  {
    String message = "File "+ fname +" does not exists!";
    server.send(404, "text/plain", message);
    Serial.println(message);
    return;
  }*/

  String type = getMIME(fname);
  Serial.println("MIME type: "+type);
  
  SdFile fd;
  SdFile dir;

  sd.vwd()->rewind();
  int findex = openFile(sd, fd, fname);
  if(findex < 0)
  {
    Serial.println("Cannot open file or not exists, received -1");
    String message = "File "+ fname +" does not exists!";
    server.send(404, "text/plain", message);
    Serial.println(message);
    return;
  }

  //fd.printSFN(&Serial);
  //Serial.println();
  sendFile(fd, type);
  fd.close();
  Serial.println("File sent");
  

  
  //server.setContentLength(CONTENT_LENGTH_UNKNOWN);
  //server.send(200, type, "");
  //WiFiClient client = server.client();
  //server.sendContent("[");

//  if(!fd)
//  {
//    Serial.println("Cannot open file!");
//    return;
//  }

  /*if (server.streamFile(fd, type) != fd.size()) {
    Serial.println("Sent less data than expected!");
  }*/
  ///////////////fd.close();
  
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
  ESP.wdtDisable();
  startDefaultAP();

server.onNotFound(handleNotFound);
Serial.println("Starting server");
server.begin();
Serial.println("SERVER BEGINED");


if(sd.begin(SD_CS/*, SPI_HALF_SPEED*/))
{
  Serial.println("SD communication OK!");
/*
if(!dirFile.open("/", O_READ))
  {
    Serial.print("ERROR OPENING DIR: ");
    return;
  }
  else
  {
    //Serial.println("LS");
    //dirFile.ls(&Serial);
    //Serial.println();
    dirFile.close();
  }*/
  
}
else
  Serial.println("SD ERROR");

}

uint8_t toggle = 0;



void loop() {
  // put your main code here, to run repeatedly:
//  digitalWrite(14, toggle);
  delay(200);
//  toggle^=1;
  server.handleClient();

}










