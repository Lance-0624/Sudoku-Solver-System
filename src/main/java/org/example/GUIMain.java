package org.example;
import javax.swing.SwingUtilities;

public class GUIMain {
    public static void main(String[] args) {
        SudokuModel model = new SudokuModel();
        SudokuController controller = new SudokuController(model);
        SwingUtilities.invokeLater(() -> {
            new SudokuView(model, controller);
        });
    }
}