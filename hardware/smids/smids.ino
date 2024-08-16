#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClient.h>
#include <Arduino_JSON.h>


// WiFi credentials
const char* ssid = "Sch! Phone";
const char* password = "passc0d10";
const char* manhole_id = "MH_HRE0001";
// API endpoints
const char* postAPI = "http://lui.co.zw/smids/post.php";
const char* ctrlAPI = "http://lui.co.zw/smids/ctrl.php?board=MH_HRE0001";
const char* notifyAPI = "http://lui.co.zw/smids/notify.php";
const char* auto_hwAPI = "http://lui.co.zw/smids/auto_hw.php";

// Sensor variables
int waterLevel;
int reedStatus;
int motionDetected;

// Timing variables
unsigned long lastTime = 0;
unsigned long timerDelay = 10000;
const long interval = 10000;
unsigned long previousMillis = 0;

// Pin configuration
const int relayPin = 13;
const int buzzerPin = 15;
const int waterSensorPin = 32; 
const int reedSensorPin = 16;  
const int motionSensorPin = 27; 
const int ledIndicator = 14;
const int echoPin = 6;
const int triggerPin = 4;

void setup() {
    Serial.begin(115200);
    setupSensors();
    setupRelay(relayPin);
    pinMode(buzzerPin, OUTPUT);
    setupWiFi();
   
}

void loop() {
    unsigned long currentMillis = millis();

    if (currentMillis - previousMillis >= interval) {
        if (httpAutoStatus() == "0") {
          Serial.println("AUTO MODE DISABLED");
            appCtrl();
         
        } else if (httpAutoStatus() == "1") {
          Serial.println("AUTO MODE ENABLED");
            autoCtrl();
        } else {
            // OFFLINE DO SOMETHING
            Serial.println(F("OFFLINE: AUTO MODE ON"));
            autoCtrl();
        }

        // Send sensor data
        postData(waterLevel, reedStatus, motionDetected);

        // Send notification
        postNotification(waterLevel, reedStatus, motionDetected);

        // Update previousMillis to the current time
        previousMillis = currentMillis;
    }
}
