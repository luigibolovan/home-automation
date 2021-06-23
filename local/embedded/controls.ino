/**
 * @author: Luigi-Ionut Bolovan
 * @description: ESP8266 code used for getting door lock status and lights status
 **/


#include <ArduinoJson.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

#define WIFI_SSID     ""
#define WIFI_PASSWORD ""
#define LOCAL_GW_IP   "192.168.1.10:2305"
#define GET_PERIOD    5000

#define DOOR_LOCK_RELAY D1
#define LIGHTS_RELAY    D2

void setup() 
{
    /**********************************************
     *************** INITIAL SETUP ****************
     *********************************************/
    Serial.begin(9600);
    pinMode(DOOR_LOCK_RELAY, OUTPUT);
    pinMode(LIGHTS_RELAY, OUTPUT);
    for (char t = 4; t > 0; t--) 
    {
        Serial.printf("Wait %d...\n", t);
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

void loop() 
{
    /**********************************************
     **************** DECLARATIONS ****************
     *********************************************/
    WiFiClient client;
    HTTPClient http;
    DynamicJsonDocument doc(1024);
    JsonObject jsonObj;

    /*********************************************
     **************** CODE EXECUTION *************
     *********************************************/
    Serial.print("[HTTP] begin...\n");
    if (http.begin(client, "http://192.168.1.10:2305/controls")) 
    {
        Serial.print("[HTTP] GET...\n");
        int httpCode = http.GET();
        if (httpCode > 0)
        {
            Serial.printf("[HTTP] GET... code: %d\n", httpCode);
            if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) 
            {
                String payload = http.getString();
                Serial.println(payload);
                deserializeJson(doc, payload);
                jsonObj = doc.as<JsonObject>();
                if (jsonObj["DoorLock"] == 1)
                {
                    digitalWrite(DOOR_LOCK_RELAY, HIGH);
                }
                else
                {
                    digitalWrite(DOOR_LOCK_RELAY, LOW);
                }
                if (jsonObj["Lights"] == 1)
                {
                    digitalWrite(LIGHTS_RELAY, HIGH);
                }
                else
                {
                    digitalWrite(LIGHTS_RELAY, LOW);
                }
            }
        }
        else
        {
            Serial.printf("[HTTP] GET... failed, error: %d\n", httpCode);
        }
        http.end();
    }
    else
    {
        Serial.printf("Unable to connect");
    }
    delay(GET_PERIOD);
}
