package com.luitech.smids;
public class ControlRequest {
    private String board;
    private int state;

    // Constructor
    public ControlRequest(String board, int state) {
        this.board = board;
        this.state = state;
    }

    // Getter for board
    public String getBoard() {
        return board;
    }

    // Setter for board
    public void setBoard(String board) {
        this.board = board;
    }

    // Getter for state
    public int getState() {
        return state;
    }

    // Setter for state
    public void setState(int state) {
        this.state = state;
    }
}
