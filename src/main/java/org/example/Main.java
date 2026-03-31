package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize the Model
        SudokuModel model = new SudokuModel();

        // Initialize the CLI (Text-based view for debugging/audit)
        SudokuCLI cli = new SudokuCLI();
        model.addObserver(cli);

    }
}