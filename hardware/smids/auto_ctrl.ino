
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
    if(!waterLevel)
    {
      digitalWrite(buzzerPin, LOW);
    } 
    digitalWrite(relayPin, HIGH);
   }
}

