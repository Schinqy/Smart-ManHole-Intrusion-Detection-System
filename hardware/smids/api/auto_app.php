<?php
// Get the autonomy value from the request body
$data = json_decode(file_get_contents('php://input'), true);
$autonomy = $data['autonomy'];

      include('connection.php');

if ($conn->connect_error) {
  die('Connection failed: ' . $conn->connect_error);
}

// Update the autonomy value in the Boards table
$query = "UPDATE Boards SET autonomy = ? WHERE board = 1";
$stmt = $conn->prepare($query);
$stmt->bind_param('i', $autonomy);
if ($stmt->execute()) {
  echo 'Autonomy updated successfully!';
} else {
  http_response_code(500);
  echo 'Failed to update autonomy: ' . $conn->error;
}

// Close the database connection
$conn->close();