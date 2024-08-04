<<<<<<< HEAD
<?php
include('connection.php');

if ($conn->connect_error) {
  die('Connection failed: ' . $conn->connect_error);
}

// Get the autonomy value from the database for board 1 (change as needed)
$query = "SELECT autonomy FROM Boards WHERE board = 1";
$result = $conn->query($query);
if ($result->num_rows > 0) {
  $data = $result->fetch_assoc();
  // Return the autonomy value as plain text
  echo $data['autonomy'];
} else {
  // Handle the case where no rows were returned (board not found)
  http_response_code(404);
  echo 'Board not found';
}

// Close the database connection
=======
<?php
include('connection.php');

if ($conn->connect_error) {
  die('Connection failed: ' . $conn->connect_error);
}

// Get the autonomy value from the database for board 1 (change as needed)
$query = "SELECT autonomy FROM Boards WHERE board = 1";
$result = $conn->query($query);
if ($result->num_rows > 0) {
  $data = $result->fetch_assoc();
  // Return the autonomy value as plain text
  echo $data['autonomy'];
} else {
  // Handle the case where no rows were returned (board not found)
  http_response_code(404);
  echo 'Board not found';
}

// Close the database connection
>>>>>>> ac249ccfe930fda68d40cf0b4bbde51bce9c9ee3
$conn->close();