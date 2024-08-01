#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClient.h>
#include <Arduino_JSON.h>

// WiFi credentials
const char* ssid = "MH_HRE01";
const char* password = "passc0d3";

const char* postAPI = "http://lui.co.zw/smids/post.php";
const char* ctrlAPI = "http://lui.co.zw/smids/ctrl.php";
const char* notifyAPI = "http://lui.co.zw/smids/notify.php";
unsigned long lastTime = 0;
unsigned long timerDelay = 5000;
bool autoCtrl = true;
void setupWiFi() {
  Serial.print("Connecting to WiFi...");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("Connected to WiFi.");
  }

 void postNotification(int water_level, int reed_status, int motion_detected) 
  {
    Serial.print("Notification: ");
    Serial.println(message);
  }

void postData(float waterLevel, bool reedStatus, bool motionDetected) {
  // Send an HTTP POST request every 10 minutes
  if ((millis() - lastTime) > timerDelay) {
    // Check WiFi connection status
    if (WiFi.status() == WL_CONNECTED) {
      WiFiClient client;
      HTTPClient http;

      
      http.begin(client, postAPI);
      
      // Define JSON payload
      String payload = "{\"water_level\":" + String(waterLevel) + ",\"reed_status\":" + String(reedStatus) + ",\"motion_detected\":" + String(motionDetected) + "}";
      Serial.println("Payload: " + payload);
      
      // Add headers
      http.addHeader("Content-Type", "application/json");

      // Send HTTP POST request
      int httpResponseCode = http.POST(payload);

      // Print HTTP response code
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);

      if (httpResponseCode == 200) {
        Serial.println("Data successfully sent");
      } else {
        Serial.println("Failed to send data");
      }

      String response = http.getString();
      Serial.println("Response: " + response);

      // Free resources
      http.end();
    } else {
      Serial.println("WiFi Disconnected");
    }
    lastTime = millis();
  }
}

String httpAutoStatus() 
{
      char response[200]; 
    char response2[200]; 
    String payload = "{}"; 
 uint16_t responseCode2=   sim808.httpGet("http://protathings.000webhostapp.com/200/auto_hw.php", response2, 512);
delay(500);

  if (responseCode2>0) {
   Serial.println(F("Status: ")); Serial.print(String(responseCode2));
   Serial.println(F("Auto Status: ")); Serial.print(String(response2));
    payload =response2;
  }
  else {
    Serial.println(F("Error code: ")); Serial.print(String(responseCode2));
    payload = "-1";
  }


  return payload;
}