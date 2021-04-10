#include <SoftwareSerial.h>

// door lock relay
#define RELAY_IN            7

// software serial rx and tx
#define ESP_TX__ARDUINO_RX  2
#define ESP_RX__ARDUINO_TX  3

#define LOCKED 		    1
#define UNLOCKED            0

#define UNLOCKED_DELAY	    3000

// create an softwareserial instance in order to be able to communicate
// with the esp8266 module
SoftwareSerial espArduinoSerial(ESP_TX__ARDUINO_RX, ESP_RX__ARDUINO_TX);


// what esp sends to arduino uno
uint8_t doorStatusFromApp   = 1;


void setup() {
  Serial.begin(9600);

  espArduinoSerial.begin(9600);
  
  pinMode(RELAY_IN, OUTPUT);
}

void loop() {
  
  //check if the communication between esp and arduino is possible
  if(espArduinoSerial.available() > 0){
    doorStatusFromApp = espArduinoSerial.read();
    Serial.println(doorStatusFromApp);
  }

  if(doorStatusFromApp == UNLOCKED){
    Serial.println("Unlocked! Welcome");
    digitalWrite(RELAY_IN, LOW);
    delay(UNLOCKED_DELAY);
    digitalWrite(RELAY_IN, HIGH);
    Serial.println("Locked");
    doorStatusFromApp = LOCKED;
  }
}
