package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class SudokuView implements Observer {
    private ISudokuModel model;
    private SudokuController controller;
    private JFrame frame;


    private JButton[][] cells = new JButton[9][9];
    // select current place
    private int selectedRow = -1;
    private int selectedCol = -1;

    public SudokuView(ISudokuModel model, SudokuController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
        createGui();
        update((Observable)model, null);
    }

    private void createGui() {
        frame = new JFrame("Sudoku Game - Lance");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));


        JPanel mainBoard = new JPanel(new GridLayout(3, 3));
        mainBoard.setBorder(new LineBorder(Color.BLACK, 2));

        for (int block = 0; block < 9; block++) {
            JPanel subGrid = new JPanel(new GridLayout(3, 3));
            subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            int startRow = (block / 3) * 3;
            int startCol = (block % 3) * 3;

            for (int r = startRow; r < startRow + 3; r++) {
                for (int c = startCol; c < startCol + 3; c++) {
                    cells[r][c] = new JButton();
                    cells[r][c].setPreferredSize(new Dimension(50, 50));
                    cells[r][c].setFont(new Font("Arial", Font.BOLD, 20));
                    cells[r][c].setBackground(Color.WHITE);

                    // record current cell
                    final int row = r;
                    final int col = c;
                    cells[r][c].addActionListener(e -> {
                        selectedRow = row;
                        selectedCol = col;
                        clearSelectionColor();
                        cells[row][col].setBackground(new Color(200, 230, 255));
                    });

                    subGrid.add(cells[r][c]);
                }
            }
            mainBoard.add(subGrid);
        }


        // creat the keyboard for number 1 to 9 and buttons
        JPanel southPanel = new JPanel(new BorderLayout());

        // number board
        JPanel keypad = new JPanel(new GridLayout(1, 9));
        for (int i = 1; i <= 9; i++) {
            JButton numBtn = new JButton(String.valueOf(i));
            final int val = i;
            numBtn.addActionListener(e -> {
                if (selectedRow != -1 && selectedCol != -1) {
                    controller.onCellInput(selectedRow, selectedCol, val);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a cell first!");
                }
            });
            keypad.add(numBtn);
        }

        // function button
        JPanel actionPanel = new JPanel();
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> controller.undo());

        JButton hintBtn = new JButton("Hint");
        hintBtn.addActionListener(e -> controller.giveHint());

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> controller.reset());

        JCheckBox valBox = new JCheckBox("Validation Feedback", model.isValidationEnabled());
        valBox.addActionListener(e -> {
            controller.setValidation(valBox.isSelected());
        });

        actionPanel.add(undoBtn);
        actionPanel.add(hintBtn);
        actionPanel.add(resetBtn);
        actionPanel.add(valBox);

        southPanel.add(keypad, BorderLayout.NORTH);
        southPanel.add(actionPanel, BorderLayout.SOUTH);

        frame.add(mainBoard, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void clearSelectionColor() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (model.getValue(r, c) == 0) {
                    cells[r][c].setBackground(Color.WHITE);
                } else {
                    cells[r][c].setBackground(new Color(240, 240, 240));
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = model.getValue(r, c);
                cells[r][c].setText(val == 0 ? "" : String.valueOf(val));

                if (model.isInitial(r, c)) {
                    cells[r][c].setBackground(new Color(225, 225, 225));
                    cells[r][c].setForeground(Color.BLACK);
                } else {
                    cells[r][c].setBackground(Color.WHITE);
                    if (val != 0) {
                        // only validationEnabled is true show the red color
                        if (model.isValidationEnabled() && !model.isValidForCheck(r, c, val)) {
                            cells[r][c].setForeground(Color.RED);
                        } else {
                            cells[r][c].setForeground(Color.BLUE);
                        }
                    }
                }
            }
        }

        if (model.isGameWon()) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You completed the Sudoku!");
        }
    }


    private boolean isEntryValid(int row, int col, int val) {
        return model.isValidForCheck(row, col, val);
    }


    private boolean isInitialCell(int r, int c) {
        return model.isInitial(r, c);
    }
}