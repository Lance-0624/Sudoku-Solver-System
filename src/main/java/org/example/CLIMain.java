package org.example;

public class CLIMain {
    public static void main(String[] args) {
        SudokuModel model = new SudokuModel();
        SudokuCLI cli = new SudokuCLI();
        model.addObserver(cli);
        cli.start(model);
    }
}