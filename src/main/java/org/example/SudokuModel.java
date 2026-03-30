package org.example;

import java.util.Observable;
import java.io.File;
import java.util.Scanner;


public class SudokuModel extends Observable {
    public static final int SIZE = 9;
    private int[][] board = new int[SIZE][SIZE];
    private boolean[][] isInitial = new boolean[SIZE][SIZE];

    public SudokuModel() {
        loadPuzzle();
    }

    // Loads the first puzzle line from the data file.
    public void loadPuzzle() {
        try {
            // use puzzles.txt file as the start
            File file = new File("puzzles.txt");
            Scanner scanner = new Scanner(file);

            if (scanner.hasNextLine()) {
                String puzzleLine = scanner.nextLine().trim();
                if (puzzleLine.length() >= 81) {
                    for (int i = 0; i < 81; i++) {
                        int r = i / 9;
                        int c = i % 9;
                        int val = Character.getNumericValue(puzzleLine.charAt(i));
                        board[r][c] = val;
                        isInitial[r][c] = (val != 0);
                    }
                }
            }
            scanner.close();
            setChanged();
            notifyObservers();
        } catch (Exception e) {
            System.err.println("Error loading puzzle: " + e.getMessage());
        }
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    public boolean isInitial(int row, int col) {
        return isInitial[row][col];
    }


    // Updates the board value if the cell is not an initial puzzle cell.
    public void setValue(int row, int col, int value) {
        if (!isInitial[row][col]) {
            board[row][col] = value;
            setChanged();
            notifyObservers();
        }
    }

    // Basic Sudoku Rule Validation: Checks row, column, and 3x3 grid.

    public boolean isValid(int row, int col, int value) {
        // Check row
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == value) return false;
        }

        // Check column
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == value) return false;
        }

        // Check 3x3 sub-grid
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (board[r][c] == value) return false;
            }
        }
        return true;
    }
}