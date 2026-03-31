package org.example;

public class Main {
    public static void main(String[] args) {

        // use cil to view the logic support by model
        SudokuModel model = new SudokuModel();
        SudokuCLI cli = new SudokuCLI();
        model.addObserver(cli);

        System.out.println("Game Started!");


        System.out.println("\nAction: Setting value 5 at (0,0)");
        model.setValue(0, 0, 5);

        // undo
        System.out.println("\nAction: Performing Undo");
        model.undo();

       // hint
        System.out.println("\nAction: Getting Hint");
        model.giveHint();
    }
}