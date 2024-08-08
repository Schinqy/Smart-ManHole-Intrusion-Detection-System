<?php


$api_key_value = "tPmAT5Ab3j7F9"; // Hardcoded API key value for validation

$api_key = ""; // Variable to store received API key

$water_level = $reed_status = $motion_detected = $manhole_id = ""; // Variables to store received data


// Check if the request method is POST

if ($_SERVER["REQUEST_METHOD"] == "POST") {

// Get the raw POST data (JSON)

$json_input = file_get_contents('php://input');

$data = json_decode($json_input, true); // Decode JSON data to PHP associative array


// Check if JSON decoding was successful

if ($data === null && json_last_error() !== JSON_ERROR_NONE) {

echo json_encode(["status" => "error", "message" => "Invalid JSON data."]);

exit();

}


// Extract API key and sensor data from the JSON data

if (isset($data['api_key'])) {

$api_key = test_input($data['api_key']);

}


if (isset($data['water_level'])) {

$water_level = test_input($data['water_level']);

}


if (isset($data['reed_status'])) {

$reed_status = test_input($data['reed_status']);

}


if (isset($data['motion_detected'])) {

$motion_detected = test_input($data['motion_detected']);

}


if (isset($data['manhole_id'])) {

$manhole_id = test_input($data['manhole_id']);

} else {

echo json_encode(["status" => "error", "message" => "manhole_id is required."]);

exit();

}


// Validate API key

if ($api_key === $api_key_value) {

require 'connection.php';


// Prepare the SQL query with parameterized inputs

$stmt = $conn->prepare("INSERT INTO sensorData (manhole_id, water_level, reed_status, motion_detected) VALUES (?, ?, ?, ?)");

$stmt->bind_param("isss", $manhole_id, $water_level, $reed_status, $motion_detected);


// Execute the query and check if successful

if ($stmt->execute()) {

echo json_encode(["status" => "success", "message" => "New record created successfully"]);

} else {

echo json_encode(["status" => "error", "message" => "Error: " . $stmt->error]);

}


// Close the statement and connection

$stmt->close();

$conn->close();

} else {

echo json_encode(["status" => "error", "message" => "Wrong API Key provided."]);

}

} else {

echo json_encode(["status" => "error", "message" => "No data posted with HTTP POST."]);

}


// Function to sanitize input data

function test_input($data) {

$data = trim($data); // Remove whitespace from both ends of a string

$data = stripslashes($data); // Remove backslashes from the string

$data = htmlspecialchars($data); // Convert special characters to HTML entities

return $data;

}

?>


