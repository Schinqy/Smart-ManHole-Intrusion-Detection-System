#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClient.h>
#include <Arduino_JSON.h>

// WiFi credentials
const char* ssid = "Sch! Phone";
const char* password = "passc0d10";

const char* postAPI = "http://lui.co.zw/smids/post.php";
const char* ctrlAPI = "http://lui.co.zw/smids/ctrl.php?board=1";
const char* notifyAPI = "http://lui.co.zw/smids/notify.php";
  const char* auto_hwAPI = "http://lui.co.zw/smids/auto_hw.php";
int waterLevel;
 int reedStatus;
 int motionDetected;
unsigned long lastTime = 0;
unsigned long timerDelay = 5000;
const long interval = 5000;
unsigned long previousMillis = 0;
const int relayPin = 48; 
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
    postData( waterLevel, reedStatus, motionDetected );
    postNotification( waterLevel, reedStatus, motionDetected );

      // save the last HTTP GET Request
      previousMillis = currentMillis;
    
  
  }
}
