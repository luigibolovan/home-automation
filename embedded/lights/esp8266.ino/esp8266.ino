#include <WiFiClientSecureBearSSL.h>
#include <ESP8266WiFiType.h>
#include <WiFiServerSecureAxTLS.h>
#include <WiFiUdp.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266WiFiScan.h>
#include <WiFiServer.h>
#include <WiFiClientSecure.h>
#include <WiFiServerSecureBearSSL.h>
#include <WiFiClientSecureAxTLS.h>
#include <WiFiClient.h>
#include <BearSSLHelpers.h>
#include <ESP8266WiFi.h>
#include <WiFiServerSecure.h>
#include <ESP8266WiFiGeneric.h>
#include <ESP8266WiFiSTA.h>
#include <CertStoreBearSSL.h>
#include <ESP8266WiFiAP.h>

#include <ArduinoJson.h>

#include <FirebaseError.h>
#include <FirebaseArduino.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseHttpClient.h>
#include <Firebase.h>
#include <FirebaseObject.h>

#define WIFI_SSID     "Clicknet-592D"
#define WIFI_PASSWORD "ZZT3WN9TV6SLZ"
#define FIREBASE_HOST "si-smart-room.firebaseio.com"
#define FIREBASE_AUTH "7huRzUWvO2KK3wI1eWFL3whkSY0d2zGOZ7KkPDY5"

#define SECOND         1000
#define READ_DELAY     2 * SECOND
#define INIT_DELAY     5 * SECOND

// what esp reads from firebase realtime database
int lightsStatus = 321;

void setup() {
  Serial.begin(9600);
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting");
  while(WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("Connected to wifi");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  delay(INIT_DELAY);
}

void loop() {
  // read data from firebase database
  // "auth/lights" is the key for lock status value
  
  lightsStatus = Firebase.getInt("lights");
  Serial.println(lightsStatus);
  Serial.write(lightsStatus);
}
