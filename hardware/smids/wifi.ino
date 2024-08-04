
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
    String message = "";
    if(water_level == 1) message = "Water Level Full. Manhole MH_HRE01 Closed. Motion Not Detected.";
    if(reed_status == 1) message = "Motion Detected. Water Level Full. Manhole MH_HRE01 Closed.";
    if(motion_detected == 1) message = " Motion Detected. Water Level Fine. Manhole MH_HRE01 Open.";

    Serial.print("Notification: ");
    Serial.println(message);
  }

void postData(int waterLevel, int reedStatus, int motionDetected) {
  const String api_key = "tPmAT5Ab3j7F9"; // API key for authentication

  // Check WiFi connection status
  if (WiFi.status() == WL_CONNECTED) {
    WiFiClient client;
    HTTPClient http;

    http.begin(client, postAPI);

    // Define JSON payload including API key
    String payload = "{\"api_key\":\"" + api_key + "\",\"water_level\":" + String(waterLevel) + ",\"reed_status\":" + String(reedStatus) + ",\"motion_detected\":" + String(motionDetected) + "}";
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
}




String httpAutoStatus() {
 
  String payload = httpGET(auto_hwAPI); 
  
  // Print the result for debugging
  if (payload == "-1") {
    Serial.println("Error retrieving auto status.");
  } else {
    Serial.println("Auto Status: " + payload);
  }

  return payload;
}





