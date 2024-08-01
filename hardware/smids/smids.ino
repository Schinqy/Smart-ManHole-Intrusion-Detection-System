#include <Arduino.h>



const int relayPin = 5; 
const int buzzerPin = 6;

void setup() {
  Serial.begin(115200);
  setupSensors();
  setupRelay(relayPin);
  pinMode(buzzerPin, OUTPUT);
  setupWiFi(); 
}

void loop() {
 unsigned long currentMillis = millis();
  
  if(currentMillis - previousMillis >= interval) {
    if(httpAutoStatus() == "0")
    {
          appCtrl();
         
         // sendSMSs();
    }
    else if(httpAutoStatus() == "1")
    {
      autoCtrl(); 
    }
    else
    {
      //OFFLINE DO SOMETHING
      Serial.println(F("OFFLINE"));
     //autoCtrl();
    }
    postData();
    postNotification();
    
    
      // save the last HTTP GET Request
      previousMillis = currentMillis;
    
  
  }
}
