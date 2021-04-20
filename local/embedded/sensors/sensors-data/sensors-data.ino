/**
 * @author: Luigi-Ionut Bolovan
 * @description: Arduino code used to get dht22 & mq4 sensor data
 *               and send it via software serial to esp8266
 **/

#include "DHT.h"
#include <ArduinoJson.h>
#include <SoftwareSerial.h>

#define SENSORS_DBG            1

#define RX_PIN                 10
#define TX_PIN                 11
#define MQ4_PIN                A0
#define DHT_PIN                8
#define DHT_TYPE               DHT22
#define MEASUREMENT_PERIOD     5000

DHT dht(DHT_PIN, DHT_TYPE);
SoftwareSerial sensorSerial(RX_PIN, TX_PIN);

void setup() {
    /**********************************************
     *************** INITIAL SETUP ****************
     *********************************************/
    Serial.begin(9600);
    dht.begin();
    sensorSerial.begin(9600);
}

void loop() {
    /**********************************************
     **************** DECLARATIONS ****************
     *********************************************/
    float                   humidityRate;
    float                   temperatureC;
    int                     mq4MetaneGasPPM;
    StaticJsonDocument<48>  jsonDoc;

    /*********************************************
     **************** CODE EXECUTION *************
     *********************************************/
    mq4MetaneGasPPM  = analogRead(MQ4_PIN);
    humidityRate     = dht.readHumidity();
    temperatureC     = dht.readTemperature();
    if (isnan(humidityRate) || isnan(temperatureC))
    {
        Serial.println("Failed to read from DHT sensor");
        return;
    }
    jsonDoc["h"]     = (int)humidityRate;
    jsonDoc["t"]     = (int)temperatureC;
    jsonDoc["gas"]   = mq4MetaneGasPPM;
#if ((defined SENSORS_DBG) && (SENSORS_DBG == 1))
    if (serializeJson(jsonDoc, Serial) == 0)
    {
        Serial.println("Failed to serialize json and send through serial");
    }
    Serial.print("\n");
#endif
    if (serializeJson(jsonDoc, sensorSerial) == 0)
    {
        Serial.println("Failed to serialize json and send through serial");
    }
    sensorSerial.write('\n');
    
    delay(MEASUREMENT_PERIOD);
}
