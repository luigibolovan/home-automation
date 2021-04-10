#include "DHT.h"
#include <MQ2.h>


#define DHTPIN 2
#define MQpin A0
#define DHTTYPE DHT22


MQ2 mq2(MQpin);
DHT dht(DHTPIN,DHTTYPE);

float hum;
float temp;
float co, smoke, lpg;
void setup() {
  Serial.begin(9600);
  dht.begin();
  mq2.begin();

  
}

void loop() {
 
  hum = dht.readHumidity();
  temp = dht.readTemperature();
  lpg = mq2.readLPG();
  co = mq2.readCO();
  smoke = mq2.readSmoke();
  
  Serial.print("Humidity: ");
  Serial.print(hum);
  Serial.print("%, Temp:");
  Serial.print(temp);

  delay(1000);
  
  Serial.println(" Celsius");
  Serial.print("Gas ");
  Serial.print(lpg);
  Serial.print(" CO: ");
  Serial.print(co);
  Serial.print(" Smoke: ");
  Serial.println(smoke);
 
  delay(8000);
  
}
