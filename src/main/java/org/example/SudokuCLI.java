package org.example;

import java.util.Observable;
import java.util.Observer;

// Simple CLI view to demonstrate Observer pattern and model-view decoupling
public class SudokuCLI implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ISudokuModel) {
            ISudokuModel model = (ISudokuModel) o;
            displayBoard(model);
        }
    }

    private void displayBoard(ISudokuModel model) {
        System.out.println("\n--- Current Sudoku Board ---");
        for (int r = 0; r < 9; r++) {
            if (r % 3 == 0 && r != 0) System.out.println("---------------------");
            for (int c = 0; c < 9; c++) {
                if (c % 3 == 0 && c != 0) System.out.print("| ");
                int val = model.getValue(r, c);
                System.out.print((val == 0 ? "." : val) + " ");
            }
            System.out.println();
        }
    }
}