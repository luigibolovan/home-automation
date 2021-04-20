/**
 * @author: Luigi-Ionut Bolovan
 * @description: ESP8266 code used for posting 
 *               dht22 & mq4 sensors data 
 *               to a local gateway
 **/

#include <ArduinoJson.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>


#define WIFI_SSID     
#define WIFI_PASSWORD 
#define LOCAL_GW_IP   "192.168.1.6:5000"
#define POST_PERIOD   5000

void postToGW(String data) {
    /**********************************************
     **************** DECLARATIONS ****************
     *********************************************/
    WiFiClient  client;
    HTTPClient  http;

    /*********************************************
     **************** CODE EXECUTION *************
     *********************************************/
    // post data
    Serial.print("[HTTP] begin...\n");
    http.begin(client, "http://" LOCAL_GW_IP "/sensors"); //HTTP
    http.addHeader("Content-Type", "application/json");

    Serial.print("[HTTP] POST...\n");
    int httpCode = http.POST(data);
    if (httpCode > 0)
    {
        Serial.printf("[HTTP] POST... code: %d\n", httpCode);
    }
    else
    {
        Serial.printf("[HTTP] POST... failed, error: %s\n", http.errorToString(httpCode).c_str());  
    }
    http.end();

    // Serial.println(data);
}

void setup() {
    /**********************************************
     *************** INITIAL SETUP ****************
     *********************************************/
    Serial.begin(9600);
    pinMode(LED_BUILTIN, OUTPUT);
    for (char t = 4; t > 0; t--) 
    {
        Serial.printf("Wait %d...\n", t);
        Serial.flush();
        delay(1000);
    }
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting");
    while (WiFi.status() != WL_CONNECTED) 
    {
        Serial.print(".");
        delay(500);
    }
    Serial.println();
    Serial.println("Connected to wifi");
    Serial.println(WiFi.localIP());
    
    delay(2000);
}


void loop() {
    /**********************************************
     **************** DECLARATIONS ****************
     *********************************************/
    char        readByte;
    String      sensorData = "";

    /*********************************************
     **************** CODE EXECUTION *************
     *********************************************/
    while (Serial.available() > 0) 
    {
        readByte = Serial.read();
        sensorData.concat(readByte);
        if (readByte == '\n')
        {
            digitalWrite(LED_BUILTIN, LOW);   // Turn the LED on
            postToGW(sensorData);
            digitalWrite(LED_BUILTIN, HIGH);  // Turn the LED off
        }
    }
}
