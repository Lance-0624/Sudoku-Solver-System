package org.example;

public class SudokuController {
    private ISudokuModel model;

    public SudokuController(ISudokuModel model) {
        this.model = model;
    }

    // deal the view input request
    public void onCellInput(int row, int col, int value) {
        model.setValue(row, col, value);
    }

    public void undo() { model.undo(); }
    public void giveHint() { model.giveHint(); }
    public void reset() { model.reset(); }
    public void setValidation(boolean enabled) {
        model.setValidationEnabled(enabled);
    }
    public void newGame() { model.newGame(); }

    // deal the view input request
    public void setRandomPuzzle(boolean random) {
        model.setRandomPuzzle(random);
    }
}