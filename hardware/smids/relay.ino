
void setupRelay(int relayPin) {
  pinMode(relayPin, OUTPUT);
  digitalWrite(relayPin, HIGH); // Initialize relay to off state
}

void triggerRelay(int relayPin) {
  digitalWrite(relayPin, LOW); // Trigger relay
}

void stopRelay(int relayPin) {
  digitalWrite(relayPin, HIGH); // Stop relay
}
