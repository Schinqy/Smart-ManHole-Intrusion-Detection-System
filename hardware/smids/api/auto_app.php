<<<<<<< HEAD
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
=======
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
>>>>>>> ac249ccfe930fda68d40cf0b4bbde51bce9c9ee3
$conn->close();