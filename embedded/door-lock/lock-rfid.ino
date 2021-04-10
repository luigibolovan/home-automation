#include <deprecated.h>
#include <MFRC522.h>
#include <MFRC522Extended.h>
#include <require_cpp11.h>
#include <SoftwareSerial.h>

#include <SPI.h>

//RFID RC522 setup:
//         SDA - PIN10
//         SCK - PIN13
//         MOSI - PIN11
//         MISO - PIN12
//         GND - Arduino GND
//         RST - PIN9
//         3.3v - 3.3v Arduino
#define SDA                 10
#define SCK                 13
#define MOSI                11
#define MISO                12
#define RST                 9

// door lock relay
#define RELAY_IN            7


#define UNLOCKED_DELAY      3000

MFRC522 rfid(SDA, RST);
int myCardID[4] = { //add here the ids of the allowed cards };

// flags used for unlocking the door
bool    grantAccess         = true;
bool    grantedAccess       = false;

void setup() {
  Serial.begin(9600);

  pinMode(RELAY_IN, OUTPUT);
  
  SPI.begin();
  rfid.PCD_Init();
  delay(4);
  rfid.PCD_DumpVersionToSerial();
}

void loop() {
    if (!rfid.PICC_IsNewCardPresent()) {
      return;
    }

    if (!rfid.PICC_ReadCardSerial()) {
      return;
    }

    //print the uid of the card that rfid sensor reads
    for(int i = 0; i < 4; i++){
      Serial.println(rfid.uid.uidByte[i]);
    }

    //byte comparison    
    for(int i = 0; i < 4; i++){
      if(rfid.uid.uidByte[i] != myCardID[i]) grantAccess = false;
    }
  
    if(grantAccess == true){
      grantedAccess = true;
      grantAccess = false;
    }

    if(grantedAccess == false){
      //do nothing with the lock
    }

    //unlock door if the uid is valid
    if(grantedAccess == true){
      Serial.println("Unlocked! Welcome");
      digitalWrite(RELAY_IN, LOW);
      delay(UNLOCKED_DELAY);
      digitalWrite(RELAY_IN, HIGH);
      Serial.println("Locked");
      grantedAccess = false;
    }

    grantAccess = true;
}
