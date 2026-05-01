package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing for SudokuModel.
 * This class includes three significantly different test scenarios as required[cite: 40, 87].
 * Each scenario describes a specific logic flow and verifies model invariants[cite: 41, 85].
 */
public class SudokuModelTest {
    private SudokuModel model;

    @BeforeEach
    public void setUp() {
        // Initialize the model before each test to ensure a clean state [cite: 175]
        model = new SudokuModel();
    }

    /**
     * Scenario 1: Functional test for valid move and single-level undo[cite: 81].
     * Logic: Verifies that setting a value updates the board correctly and
     * that the undo function restores the previous state[cite: 5].
     */
    @Test
    public void testValidMoveAndUndo() {
        // Find the first empty (editable) cell
        int row = 0, col = 0;
        while (model.isInitial(row, col)) {
            col++;
            if (col == SudokuModel.SIZE) { col = 0; row++; }
        }

        int testValue = 5;
        // Verify postcondition: value is set correctly [cite: 35, 42]
        model.setValue(row, col, testValue);
        assertEquals(testValue, model.getValue(row, col), "Board should update with the new value.");

        // Verify postcondition: undo restores the cell to empty (0) [cite: 81]
        model.undo();
        assertEquals(0, model.getValue(row, col), "Undo should revert the cell to its original state.");
    }

    /**
     * Scenario 2: Validation of Sudoku rules (Conflict Detection)[cite: 81].
     * Logic: Verifies that the model correctly identifies duplicate numbers
     * within the same row according to Sudoku constraints[cite: 59].
     */
    @Test
    public void testSudokuRuleValidation() {
        // Find a pre-filled cell to get its value as a reference
        int refValue = -1;
        int targetRow = -1;
        for (int r = 0; r < SudokuModel.SIZE; r++) {
            for (int c = 0; c < SudokuModel.SIZE; c++) {
                if (model.isInitial(r, c)) {
                    refValue = model.getValue(r, c);
                    targetRow = r;
                    break;
                }
            }
            if (refValue != -1) break;
        }

        // Try to find an empty cell in the SAME row to test conflict detection [cite: 81]
        for (int c = 0; c < SudokuModel.SIZE; c++) {
            if (!model.isInitial(targetRow, c) && model.getValue(targetRow, c) == 0) {
                // Assert that placing a duplicate value in the same row is invalid [cite: 42]
                assertFalse(model.isValid(targetRow, c, refValue),
                        "Placing a duplicate value in the same row should return false.");
                break;
            }
        }
    }

    /**
     * Scenario 3: Immutability of initial cells.
     * Logic: Verifying that attempting to change pre-filled cells
     * triggers an AssertionError as defined in the preconditions.
     */
    @Test
    public void testInitialCellProtection() {
        // Find the first pre-filled initial cell
        int initRow = -1, initCol = -1;
        for (int r = 0; r < SudokuModel.SIZE; r++) {
            for (int c = 0; c < SudokuModel.SIZE; c++) {
                if (model.isInitial(r, c)) {
                    initRow = r;
                    initCol = c;
                    break;
                }
            }
            if (initRow != -1) break;
        }

        // We expect an AssertionError because the Model's precondition
        // protects initial cells via an 'assert' statement.
        final int finalRow = initRow;
        final int finalCol = initCol;
        final int newValue = (model.getValue(initRow, initCol) % 9) + 1;

        assertThrows(AssertionError.class, () -> {
            model.setValue(finalRow, finalCol, newValue);
        }, "Modifying an initial cell should trigger an AssertionError.");
    }
}