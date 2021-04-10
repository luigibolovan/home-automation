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

#define WIFI_SSID     "insert your wifi ssid"
#define WIFI_PASSWORD "insert your password"
#define FIREBASE_HOST "firebase host"
#define FIREBASE_AUTH "insert your firebase secret"

#define SECOND         1000
#define UNLOCKED_DELAY 3 * SECOND
#define READ_DELAY     2 * SECOND
#define INIT_DELAY     5 * SECOND

#define LOCKED 	       1
#define UNLOCKED       0

// what esp reads from firebase realtime database
int doorStatus = 321;

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
  // "auth/isLocked" is the key for lock status value
  doorStatus = Firebase.getInt("auth/isLocked");

  if(doorStatus == UNLOCKED){
    Serial.write(doorStatus);
    delay(UNLOCKED_DELAY);
    doorStatus = LOCKED;
    Firebase.setInt("auth/isLocked", doorStatus);
  }

  delay(READ_DELAY);
}
