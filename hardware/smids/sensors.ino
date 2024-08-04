
const int waterSensorPin = 7; 
const int reedSensorPin = 8;  
const int motionSensorPin = 9; 
void setupSensors() {
  pinMode(waterSensorPin, INPUT);
  pinMode(reedSensorPin, INPUT_PULLUP);
  pinMode(motionSensorPin, INPUT);
}

void checkSensors() {
  int waterLevelAnalog = analogRead(waterSensorPin);
  if(waterLevelAnalog >= 400) waterLevel = 1;
  else waterLevel = 0;
  reedStatus = digitalRead(reedSensorPin); 
  motionDetected = digitalRead(motionSensorPin);


  Serial.print("Water Level: "); Serial.println(waterLevel);
  Serial.print("Reed Status: "); Serial.println(reedStatus ? "Detected" : "Not Detected");
  Serial.print("Motion Detected: "); Serial.println(motionDetected ? "Yes" : "No");

  // Add logic to trigger relay or other actions based on sensor data
}
