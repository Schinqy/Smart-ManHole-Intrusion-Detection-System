void setupWiFi() {
  Serial.print("Connecting to WiFi...");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("Connected to WiFi.");
  }




void postNotification(int water_level, int reed_status, int motion_detected){
    unsigned long currentTime = millis();  // Get the current time in milliseconds
    String message = "";

    // Build the message based on the status of the sensors
    if (water_level == 1) {
        message += "Alert: Water Level Full in Manhole " + String(manhole_id) + ". Immediate action required. " ;
    }
    if (reed_status == 1) {
        message += "Alert: Manhole " + String(manhole_id) + " Cover Opened. Check for potential issues. " ;
    }
    if (motion_detected == 1) {
        message += "Alert: Motion Detected in Manhole " + String(manhole_id) + ". Verify the area for safety. ";
    }

    // Remove trailing space if there's any message
    if (message.endsWith(" ")) {
        message.trim();
    }

    // Check if the message is different from the previous one
    if (message != previousMessage) {
        // Check if enough time has passed since the last notification
        if ((currentTime - lastNotificationTime) >= NOTIFICATION_INTERVAL) {
            // Send the notification to the API
            if (WiFi.status() == WL_CONNECTED) {
                HTTPClient http;
                http.begin(notifyAPI);

                // Prepare the POST data
                String postData = "announcement=" + urlencode(message) + "&board_id=" + String(manhole_id);

                // Send the POST request
                http.addHeader("Content-Type", "application/x-www-form-urlencoded");
                int httpResponseCode = http.POST(postData);

                if (httpResponseCode > 0) {
                    Serial.print("HTTP Response code: ");
                    Serial.println(httpResponseCode);
                } else {
                    Serial.print("Error on sending POST: ");
                    Serial.println(httpResponseCode);
                }

                http.end();
            } else {
                Serial.println("WiFi not connected");
            }

            // Update the last notification time and previous message
            lastNotificationTime = currentTime;
            previousMessage = message;

            Serial.print("Notification sent: ");
            Serial.println(message);
        } else {
            Serial.println("Not enough time has passed since last notification.");
        }
    } else {
        Serial.println("Message is the same as the previous one. Not sending.");
    }
}

// Helper function to URL encode the message
String urlencode(String str) {
    String encoded = "";
    for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (c == ' ') {
            encoded += '+';
        } else if (c == '!' || c == '*' || c == '\'' || c == '(' || c == ')' || c == '~' ||
                   (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                   (c >= '0' && c <= '9')) {
            encoded += c;
        } else {
            encoded += '%';
            encoded += String(c, HEX);
            encoded += ' ';
        }
    }
    return encoded;
}



void postData(int waterLevel, int reedStatus, int motionDetected) {
  const String api_key = "tPmAT5Ab3j7F9"; // API key for authentication

  // Check WiFi connection status
  if (WiFi.status() == WL_CONNECTED) {
    WiFiClient client;
    HTTPClient http;

    http.begin(client, postAPI);

 
// Construct the JSON payload 
 String payload = "{\"api_key\":\"" + api_key + "\",\"manhole_id\":\"" + String(manhole_id) + "\",\"water_level\":" + String(waterLevel) + ",\"reed_status\":" + String(reedStatus) + ",\"motion_detected\":" + String(motionDetected) + "}";


// Use this payload for HTTP request
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



String httpAutoStatus(){
    // Construct the URL by concatenating parts as Strings
    String url = String(auto_hwAPI) + "?board_id=" + String(manhole_id);

    // Convert the String URL to const char*
    const char* urlChar = url.c_str();

    // Perform the HTTP GET request
    String payload = httpGET(urlChar);

    // Print the result for debugging
    if (payload == "-1") {
        Serial.println("Error retrieving auto status.");
    } else {
        Serial.println("Auto Status: " + payload);
    }

    return payload;
}







