
void setupRelay(int relayPin) {
  pinMode(relayPin, OUTPUT);
  digitalWrite(relayPin, LOW); // Initialize relay to off state
}

void triggerRelay(int relayPin) {
  digitalWrite(relayPin, HIGH); // Trigger relay
}

void stopRelay(int relayPin) {
  digitalWrite(relayPin, LOW); // Stop relay
}
