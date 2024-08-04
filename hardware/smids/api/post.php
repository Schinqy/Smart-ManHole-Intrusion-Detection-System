<?php

$api_key_value = "tPmAT5Ab3j7F9";
$api_key = "";
$water_level = $reed_status = $motion_detected = "";

// Check if the request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get the raw POST data
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

    // Validate API key
    if ($api_key === $api_key_value) {
        require 'connection.php';

        // Insert data into the database
        $sql = "INSERT INTO sensorData (water_level, reed_status, motion_detected) VALUES ('$water_level', '$reed_status', '$motion_detected')";

        if ($conn->query($sql) === TRUE) {
            echo json_encode(["status" => "success", "message" => "New record created successfully"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Error: " . $conn->error]);
        }

        $conn->close();
    } else {
        echo json_encode(["status" => "error", "message" => "Wrong API Key provided."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "No data posted with HTTP POST."]);
}

// Function to sanitize input data
function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>
