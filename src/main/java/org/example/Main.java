package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize the Model
        SudokuModel model = new SudokuModel();

        // Initialize the CLI (Text-based view for debugging/audit)
        SudokuCLI cli = new SudokuCLI();
        model.addObserver(cli);

        // Initialize the Controller
        SudokuController controller = new SudokuController(model);

        // We use invokeLater to ensure thread safety for Swing components
        SwingUtilities.invokeLater(() -> {
            new SudokuView(model, controller);
        });

        System.out.println("Sudoku System Initialized with both CLI and GUI support.");
    }
}