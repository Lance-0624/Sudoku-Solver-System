package org.example;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

// Simple CLI view to demonstrate Observer pattern and model-view decoupling
public class SudokuCLI implements Observer {

    private ISudokuModel model;

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ISudokuModel) {
            this.model = (ISudokuModel) o;
            displayBoard(model);
            if (model.isGameWon()) {
                System.out.println("\n Congratulations! You completed the Sudoku!");
            }
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

    public void start(ISudokuModel model) {
        this.model = model;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWelcome to Sudoku CLI!");
        System.out.println("Commands: set <row> <col> <val> (0-8 index), erase <row> <col>, undo, hint, reset, newgame, exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            String[] parts = input.split("\\s+");

            if (parts[0].equals("exit")) {
                System.out.println("Exiting CLI...");
                break;
            }

            try {
                switch (parts[0]) {
                    case "set":
                        if (parts.length == 4) {
                            model.setValue(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                        } else {
                            System.out.println("Usage: set <row> <col> <val>");
                        }
                        break;
                    case "erase":
                        if (parts.length == 3) {
                            model.setValue(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 0);
                        } else {
                            System.out.println("Usage: erase <row> <col>");
                        }
                        break;
                    case "undo":
                        model.undo();
                        break;
                    case "hint":
                        model.giveHint();
                        break;
                    case "reset":
                        model.reset();
                        break;
                    case "newgame":
                        model.newGame();
                        break;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input format or action.");
            }
        }
    }
}