<?php
include('connection.php');

$board_id = $_GET['board']; // board ID is passed as a query parameter
$data = array();

// SQL query to fetch GPIO and state for the given board
$sql = "SELECT gpio, state FROM Outputs WHERE board='" . $board_id . "'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $data[$row['gpio']] = $row['state'];
    }
}

echo json_encode($data);
$conn->close();
?>
