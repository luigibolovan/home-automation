#include <SoftwareSerial.h>

// lights relay
#define RELAY_IN            7

// software serial rx and tx
#define ESP_TX__ARDUINO_RX  2
#define ESP_RX__ARDUINO_TX  3

#define LIGHTSOFF        1
#define LIGHTSON          0

// create an softwareserial instance in order to be able to communicate
// with the esp8266 module
SoftwareSerial espArduinoSerial(ESP_TX__ARDUINO_RX, ESP_RX__ARDUINO_TX);


// what esp sends to arduino uno
uint8_t lightsStatusFromApp   = 1;


void setup() {
  Serial.begin(9600);

  espArduinoSerial.begin(9600);
  
  pinMode(RELAY_IN, OUTPUT);
}

void loop() {
  
  //check if the communication between esp and arduino is possible
  if(espArduinoSerial.available() > 0){
    lightsStatusFromApp = espArduinoSerial.read();
    Serial.println(lightsStatusFromApp);
  }

  if(lightsStatusFromApp == LIGHTSON){
    Serial.println("Lights ON");
    digitalWrite(RELAY_IN, LOW);
  }
  if(lightsStatusFromApp == LIGHTSOFF){
    Serial.println("Lights OFF");
    digitalWrite(RELAY_IN, HIGH);  
  }

}
