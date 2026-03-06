package com.example.promines.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameView extends GridPane {
    private final CellView[][] cellViews;
    private final Label timerLabel;
    private final Label flagsLabel;
    private final Label minesLabel;
    private final Label statusLabel;
    private final Button resetButton;

    public GameView(int width, int height) {
        this.cellViews = new CellView[width][height];
        this.setStyle("-fx-background-color: #302E2B;");

        // Define columns and rows
        for (int i = 0; i < 25; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 25);
            getColumnConstraints().add(col);
        }
        for (int i = 0; i < 25; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 25);
            getRowConstraints().add(row);
        }

        // Create board
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                CellView cellView = new CellView();
                cellViews[x][y] = cellView;
                this.add(cellView, y, x);
            }
        }

        // Side info labels
        this.timerLabel = createLabel("00:00:00:00", 20, 3, 5, 2);
        createIconLabel("/images/time2.png", 18, 3);

        this.flagsLabel = createLabel("00", 20, 7, 2, 2);
        createIconLabel("/images/flag.png", 18, 7);

        this.minesLabel = createLabel("0", 20, 11, 2, 2);
        createIconLabel("/images/bomb3.png", 18, 11);

        this.statusLabel = createLabel("Go !", 20, 15, 4, 2);
        this.statusLabel.setFont(Font.font("Ubuntu", FontWeight.BOLD, 30));

        this.resetButton = new Button("ré-init");
        this.resetButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setColumnSpan(this.resetButton, 2);
        this.add(this.resetButton, 20, 0);
    }

    private Label createLabel(String text, int col, int row, int colSpan, int rowSpan) {
        Label label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setStyle("-fx-background-color:transparent; -fx-border-color:#302E2B; -fx-text-fill:white");
        label.setFont(Font.font("Ubuntu", FontWeight.BOLD, 25));
        setColumnSpan(label, colSpan);
        setRowSpan(label, rowSpan);
        this.add(label, col, row);
        return label;
    }

    private void createIconLabel(String iconPath, int col, int row) {
        Label l = new Label();
        l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setColumnSpan(l, 2);
        setRowSpan(l, 2);
        try {
            Image im = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iv = new ImageView(im);
            iv.setFitWidth(50);
            iv.setFitHeight(50);
            l.setGraphic(iv);
        } catch (Exception e) {}
        this.add(l, col, row);
    }

    // Getters for controller access
    public CellView getCellView(int x, int y) { return cellViews[x][y]; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getFlagsLabel() { return flagsLabel; }
    public Label getMinesLabel() { return minesLabel; }
    public Label getStatusLabel() { return statusLabel; }
    public Button getResetButton() { return resetButton; }
}
