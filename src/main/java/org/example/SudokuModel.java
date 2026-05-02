package org.example;

import java.util.Observable;
import java.io.File;
import java.util.Scanner;

// update Updated SudokuModel implementing the new interface
public class SudokuModel extends Observable implements ISudokuModel {
//public class SudokuModel extends Observable {

    /*@ public invariant board != null && board.length == SIZE;
      @ public invariant (\forall int r, c; 0 <= r && r < SIZE && 0 <= c && c < SIZE;
      @                  board[r][c] >= 0 && board[r][c] <= 9);
      @ public invariant isInitial != null && isInitial.length == SIZE;
      @*/

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

    /*@ assignable board[*], isInitial[*];
      @ ensures (\exists int i; i >= 0; true);
      @*/
    public void loadPuzzle() {
        try {
            //File file = new File("test_win.txt");
            File file = new File("puzzles.txt");
            Scanner scanner = new Scanner(file);
            String puzzleLine = "";
            if (scanner.hasNextLine()) {
                puzzleLine = scanner.nextLine().trim();
            }
            scanner.close();

            if (puzzleLine.length() >= SIZE*SIZE) {
                for (int i = 0; i < SIZE*SIZE; i++) {
                    int row = i / SIZE;
                    int column = i % SIZE;
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



    /*@ requires 0 <= row && row < SIZE && 0 <= col && col < SIZE;
      @ requires value >= 0 && value <= 9;
      @ requires !isInitial[row][col];
      @ assignable board[row][col], lastRow, lastCol, lastValue;
      @ ensures board[row][col] == value;
      @ ensures lastRow == row && lastCol == col;
      @*/
    public void setValue(int row, int col, int value) {
        // Verify preconditions using assert
        assert row >= 0 && row < SIZE : "Precondition: Row index out of bounds";
        assert col >= 0 && col < SIZE : "Precondition: Column index out of bounds";
        assert !isInitial[row][col] : "Precondition: Cannot modify initial cells";

        if (!isInitial[row][col]) {
            // Record state for undo functionality
            lastRow = row;
            lastCol = col;
            lastValue = board[row][col];

            board[row][col] = value;

            // Verify postconditions using assert
            assert board[row][col] == value : "Postcondition: Value not set correctly";

            setChanged();
            notifyObservers();
        }
    }

    /*@ requires lastRow != -1;
      @ assignable board[lastRow][lastCol], lastRow;
      @ ensures board[lastRow][lastCol] == 0;
      @ ensures lastRow == -1;
      @*/
    public void undo() {
        // Precondition for verification: There must be a revocable operation
        assert lastRow != -1 : "Precondition: No move to undo";

        if (lastRow != -1 && !isInitial[lastRow][lastCol]) {
            board[lastRow][lastCol] = lastValue;

            // Verify the postconditions
            assert board[lastRow][lastCol] == lastValue : "Postcondition: Undo failed";

            lastRow = -1;
            setChanged();
            notifyObservers();
        }
    }

    /*@ requires (\exists int r, c; 0 <= r && r < SIZE && 0 <= c && c < SIZE; board[r][c] == 0);
      @ assignable board[*], lastRow, lastCol, lastValue;
      @ ensures (\exists int r, c; 0 <= r && r < SIZE && 0 <= c && c < SIZE; \old(board[r][c]) == 0 && board[r][c] != 0);
      @*/
    public void giveHint() {
        if (!hintEnabled) return;

        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int val = 1; val <= SIZE; val++) {
                        if (isValid(row, column, val)) {
                            setValue(row, column, val);
                            return;
                        }
                    }
                }
            }
        }
    }

    /*@ requires 0 <= row && row < SIZE && 0 <= col && col < SIZE;
      @ requires val >= 1 && val <= 9;
      @ ensures \result == ((\forall int i; 0 <= i && i < SIZE && i != col; board[row][i] != val) &&
      @                    (\forall int i; 0 <= i && i < SIZE && i != row; board[i][col] != val) &&
      @                    (\forall int r, c; (r/3 == row/3) && (c/3 == col/3) && (r != row || c != col); board[r][c] != val));
      @*/
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

    /*@ requires 0 <= row && row < SIZE && 0 <= col && col < SIZE;
      @ requires value >= 1 && value <= 9;
      @ ensures \result == ((\forall int i; 0 <= i && i < SIZE && i != col; board[row][i] != value) &&
      @                    (\forall int i; 0 <= i && i < SIZE && i != row; board[i][col] != value) &&
      @                    (\forall int r, c; (r/3 == row/3) && (c/3 == col/3) && (r != row || c != col); board[r][c] != value));
      @*/
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

    // @ensures (\forall int r, c; 0 <= r, c < SIZE && !isInitial[r][c]; board[r][c] == 0)
    // @ensures lastRow == -1
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

    /*@ assignable board[*], isInitial[*], lastRow;
      @ ensures lastRow == -1;
      @*/
    public void newGame() {
        loadPuzzle();
        lastRow = -1;
    }

    /*@ ensures \result == (\forall int r, c; 0 <= r && r < SIZE && 0 <= c && c < SIZE;
      @                    board[r][c] != 0 && isValidForCheck(r, c, board[r][c]));
      @*/
    public boolean isGameWon() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
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