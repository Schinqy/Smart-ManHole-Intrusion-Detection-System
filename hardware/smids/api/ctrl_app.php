<<<<<<< HEAD
<?php
include('connection.php');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get the board ID and state from the POST request
    $board = test_input($_POST["board"]);
    $state = test_input($_POST["state"]);

    // Validate the input
    if (!is_numeric($board) || !is_numeric($state)) {
        echo "Invalid input.";
        exit();
    }

    // Update the GPIO state for all pins associated with the board
    $sql = "UPDATE Outputs SET state='" . $state . "' WHERE board='" . $board . "'";
    if ($conn->query($sql) === TRUE) {
        echo "GPIO state updated successfully for board " . $board;
    } else {
        echo "Error updating GPIO state: " . $conn->error;
    }

    $conn->close();
} else {
    echo "No data posted with HTTP POST.";
}

function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>
=======
<?php
include('connection.php');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get the board ID and state from the POST request
    $board = test_input($_POST["board"]);
    $state = test_input($_POST["state"]);

    // Validate the input
    if (!is_numeric($board) || !is_numeric($state)) {
        echo "Invalid input.";
        exit();
    }

    // Update the GPIO state for all pins associated with the board
    $sql = "UPDATE Outputs SET state='" . $state . "' WHERE board='" . $board . "'";
    if ($conn->query($sql) === TRUE) {
        echo "GPIO state updated successfully for board " . $board;
    } else {
        echo "Error updating GPIO state: " . $conn->error;
    }

    $conn->close();
} else {
    echo "No data posted with HTTP POST.";
}

function test_input($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>
>>>>>>> ac249ccfe930fda68d40cf0b4bbde51bce9c9ee3
