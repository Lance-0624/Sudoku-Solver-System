package org.example;

import java.util.Observable;
import java.io.File;
import java.util.Scanner;

// update Updated SudokuModel implementing the new interface
public class SudokuModel extends Observable implements ISudokuModel {
//public class SudokuModel extends Observable {
    public static final int SIZE = 9;
    private int[][] board = new int[SIZE][SIZE];
    private boolean[][] isInitial = new boolean[SIZE][SIZE];

    // Status flags for functional requirements
    private boolean validationEnabled = true;
    private boolean hintEnabled = true;
    private boolean randomPuzzle = false;

    // Fields to track the last action for undo
    private int lastRow = -1;
    private int lastCol = -1;
    private int lastValue = 0;

    // Initialize by loading the first puzzle
    public SudokuModel() {
        loadPuzzle();
    }

    // Loads puzzle data from the text file
    public void loadPuzzle() {
        try {
            File file = new File("puzzles.txt");
            Scanner scanner = new Scanner(file);
            String puzzleLine = "";
            if (scanner.hasNextLine()) {
                puzzleLine = scanner.nextLine().trim();
            }
            scanner.close();

            if (puzzleLine.length() >= 81) {
                for (int i = 0; i < 81; i++) {
                    int row = i / 9;
                    int column = i % 9;
                    int val = Character.getNumericValue(puzzleLine.charAt(i));
                    board[row][column] = val;
                    isInitial[row][column] = (val != 0);
                }
            }

            setChanged();
            notifyObservers();
        } catch (Exception e) {
            System.err.println("Error loading puzzle: " + e.getMessage());
        }
    }

    public boolean isInitial(int row, int col) {
        return isInitial[row][col];
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    // Updates the cell value and records it for undo
    public void setValue(int row, int col, int value) {
        assert row >= 0 && row < SIZE : "Row index out of bounds";
        assert col >= 0 && col < SIZE : "Column index out of bounds";

        if (!isInitial[row][col]) {
            // Record state for undo functionality
            lastRow = row;
            lastCol = col;
            lastValue = board[row][col];

            board[row][col] = value;
            setChanged();
            notifyObservers();
        }
    }

    // Reverts the last move made by the user
    public void undo() {
        if (lastRow != -1 && !isInitial[lastRow][lastCol]) {
            board[lastRow][lastCol] = lastValue;
            // Prevent multiple undos
            lastRow = -1;
            setChanged();
            notifyObservers();
        }
    }

    // Provides a hint by filling a valid number in the first empty cell
    public void giveHint() {
        if (!hintEnabled) return;

        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int val = 1; val <= 9; val++) {
                        if (isValid(row, column, val)) {
                            setValue(row, column, val);
                            return;
                        }
                    }
                }
            }
        }
    }

    // Standard Sudoku rule validation: checks row, column, and 3x3 grid
    public boolean isValid(int row, int col, int value) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == value) return false;
        }

        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == value) return false;
        }

        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (board[r][c] == value) return false;
            }
        }
        return true;
    }

    // Checks if value conflicts with other cells
    public boolean isValidForCheck(int row, int col, int value) {
        for (int i = 0; i < SIZE; i++) {
            if (i != col && board[row][i] == value) return false;
        }
        for (int i = 0; i < SIZE; i++) {
            if (i != row && board[i][col] == value) return false;
        }
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (r == row && c == col) continue;
                if (board[r][c] == value) return false;
            }
        }
        return true;
    }


    public void setValidationEnabled(boolean enabled) {
        this.validationEnabled = enabled;
        setChanged();
        notifyObservers();
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setHintEnabled(boolean enabled) {
        this.hintEnabled = enabled;
    }

    public void setRandomPuzzle(boolean random) {
        this.randomPuzzle = random;
    }

    // Internal method for win-check without notifying observers
    public void setValueForValidation(int row, int col, int value) {
        board[row][col] = value;
    }

    // Resets all non-initial cells to zero
    public void reset() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!isInitial[r][c]) {
                    board[r][c] = 0;
                }
            }
        }
        lastRow = -1;
        setChanged();
        notifyObservers();
    }

    // Reloads the puzzle for a new game session
    public void newGame() {
        loadPuzzle();
        lastRow = -1;
    }

    // Checks if the board is completely filled and valid
    public boolean isGameWon() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < 9; c++) {
                int val = board[r][c];
                if (val == 0) return false;

                setValueForValidation(r, c, 0);
                boolean valid = isValid(r, c, val);
                setValueForValidation(r, c, val);

                if (!valid) return false;
            }
        }
        return true;
    }
}