<<<<<<< HEAD
void autoCtrl()
{
  checkSensors();
  digitalWrite(buzzerPin, waterLevel? 1 : 0);
   if( reedStatus || motionDetected )
   {
    digitalWrite(buzzerPin, HIGH);
    digitalWrite(relayPin, LOW);
   }
   else {
    digitalWrite(buzzerPin, LOW);
    digitalWrite(relayPin, HIGH);
   }
}
=======
void autoCtrl()
{
  checkSensors();
  digitalWrite(buzzerPin, waterLevel? 1 : 0);
   if( reedStatus || motionDetected )
   {
    digitalWrite(buzzerPin, HIGH);
    digitalWrite(relayPin, LOW);
   }
   else {
    digitalWrite(buzzerPin, LOW);
    digitalWrite(relayPin, HIGH);
   }
}
>>>>>>> ac249ccfe930fda68d40cf0b4bbde51bce9c9ee3
