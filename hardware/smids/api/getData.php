<?php


// Check if the request method is GET

if ($_SERVER["REQUEST_METHOD"] == "GET") {

// Get the manhole_id from the query string

if (isset($_GET['manhole_id'])) {

$manhole_id = test_input($_GET['manhole_id']);

require 'connection.php'; // Include the connection file


// Prepare and execute SQL query to retrieve data

$stmt = $conn->prepare("SELECT water_level, reed_status, motion_detected FROM sensorData WHERE manhole_id = ?");

$stmt->bind_param("i", $manhole_id); // Bind the manhole_id as an integer

$stmt->execute();

$result = $stmt->get_result();


// Check if any data is returned

if ($result->num_rows > 0) {

// Fetch data as an associative array

$data = $result->fetch_assoc();

echo json_encode(["status" => "success", "data" => $data]);

} else {

echo json_encode(["status" => "error", "message" => "No data found for the given manhole_id."]);

}


$stmt->close();

$conn->close();

} else {

echo json_encode(["status" => "error", "message" => "manhole_id parameter is missing."]);

}

} else {

echo json_encode(["status" => "error", "message" => "Invalid request method."]);

}


// Function to sanitize input data

function test_input($data) {

$data = trim($data); // Remove whitespace from both ends of a string

$data = stripslashes($data); // Remove backslashes from the string

$data = htmlspecialchars($data); // Convert special characters to HTML entities

return $data;

}

?>


