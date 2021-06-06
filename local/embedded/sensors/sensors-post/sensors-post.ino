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
#include "DHT.h"
#include <ArduinoJson.h>


#define WIFI_SSID     ""
#define WIFI_PASSWORD ""
#define LOCAL_GW_IP   "192.168.1.10:1310"
#define POST_PERIOD   10000 //10s

#define DHT_TYPE               DHT22
#define DHT_PIN                D1
#define MQ4_PIN                A0

DHT dht(DHT_PIN, DHT_TYPE);
float lastTemperaturePosted;
float lastHumidityPosted;
float lastCH4Posted;

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
    http.begin(client, "http://192.168.1.10:1310/sensors"); //HTTP
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
    dht.begin();
    delay(2000);
}


void loop() {
    /**********************************************
     **************** DECLARATIONS ****************
     *********************************************/
    float                   humidityRate;
    float                   temperatureC;
    int                     methaneConcentration;
    StaticJsonDocument<256> jsonDoc;
    String                  output;

    /*********************************************
     **************** CODE EXECUTION *************
     *********************************************/
    humidityRate            = dht.readHumidity();
    temperatureC            = dht.readTemperature();
    methaneConcentration    = analogRead(MQ4_PIN);
    if (isnan(humidityRate) || isnan(temperatureC))
    {
        Serial.println("Failed to read from DHT sensor");
        delay(2000);
        return;
    }
    if ((temperatureC - lastTemperaturePosted > 2.0) ||
        (humidityRate - lastHumidityPosted > 5.0)    ||
        (methaneConcentration - lastCH4Posted > 100))
        {
            jsonDoc["Temperature"]              = (int)temperatureC;
            jsonDoc["Humidity"]                 = (int)humidityRate;
            jsonDoc["MethaneGasConcentration"]  = methaneConcentration;
            serializeJson(jsonDoc, output);
            Serial.println(output);
            postToGW(output);
            lastTemperaturePosted   = temperatureC;
            lastHumidityPosted      = humidityRate;
            lastCH4Posted           = methaneConcentration;
        }
    
    delay(POST_PERIOD);
}
