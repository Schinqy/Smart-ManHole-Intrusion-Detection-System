
const int waterSensorPin = A0; 
const int reedSensorPin = 2;  
const int motionSensorPin = 4; 
 int waterLevell
 int reedStatus;
 int motionDetected;
void setupSensors() {
  pinMode(waterSensorPin, INPUT);
  pinMode(reedSensorPin, INPUT_PULLUP);
  pinMode(motionSensorPin, INPUT);
}

void checkSensors() {
  waterLevel = digitalRead(waterSensorPin);
  reedStatus = digitalRead(reedSensorPin); 
  motionDetected = digitalRead(motionSensorPin);


  Serial.print("Water Level: "); Serial.println(waterLevel);
  Serial.print("Reed Status: "); Serial.println(reedStatus ? "Detected" : "Not Detected");
  Serial.print("Motion Detected: "); Serial.println(motionDetected ? "Yes" : "No");

  // Add logic to trigger relay or other actions based on sensor data
}
