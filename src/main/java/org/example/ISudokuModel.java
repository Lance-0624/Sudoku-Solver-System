package org.example;

import java.util.Observer;

// Interface for Sudoku Model to ensure loose coupling
public interface ISudokuModel {
    // Core game logic
    void loadPuzzle();
    void setValue(int row, int col, int value);
    int getValue(int row, int col);
    boolean isInitial(int row, int col);

    // Validation and rules
    boolean isValid(int row, int col, int value);
    boolean isValidForCheck(int row, int col, int value);
    boolean isGameWon();

    // Game features
    void undo();
    void giveHint();
    void reset();
    void newGame();

    // Flag management
    void setValidationEnabled(boolean enabled);
    boolean isValidationEnabled();
    void setHintEnabled(boolean enabled);
    void setRandomPuzzle(boolean random);

    // Observer pattern methods (Needed because View/CLI depend on these)
    void addObserver(Observer o);
    void deleteObserver(Observer o);
}