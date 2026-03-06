package com.example.promines.view;

import com.example.promines.model.Board;
import com.example.promines.model.Cell;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;

public class GameView extends GridPane {
    private final Canvas canvas;
    private final int cellSize = 22; // Taille d'une case en pixels
    private final Label timerLabel;
    private final Label flagsLabel;
    private final Label minesLabel;
    private final Label statusLabel;
    private final Button resetButton;

    // Cache pour les images pour éviter de les recharger à chaque rendu
    private final Map<String, Image> imageCache = new HashMap<>();

    public GameView(int width, int height) {
        this.setStyle("-fx-background-color: #302E2B;");

        // Configuration de la grille principale pour l'interface
        for (int i = 0; i < 25; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 25);
            getColumnConstraints().add(col);
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 25);
            getRowConstraints().add(row);
        }

        // Création du Canvas pour le plateau de jeu
        // On calcule la taille nécessaire : 25 colonnes * cellSize, 17 lignes * cellSize
        this.canvas = new Canvas(height * cellSize, width * cellSize); 
        // Note: l'inversion width/height dépend de comment tu as structuré ton Board. 
        // Dans ton code initial: width=25 (lignes), height=17 (colonnes).
        
        this.add(canvas, 0, 0, 18, 25); // Occupe la partie gauche

        // Labels d'infos (inchangés)
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
        
        loadImages();
    }

    private void loadImages() {
        String[] images = {"flag", "bomb2", "img1", "img2", "img3", "img4", "img5", "img6", "img7", "img8"};
        for (String img : images) {
            try {
                imageCache.put(img, new Image(getClass().getResourceAsStream("/images/" + img + ".png")));
            } catch (Exception e) {}
        }
    }

    public void drawBoard(Board board) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                drawCell(gc, board.getCell(x, y), x, y);
            }
        }
    }

    private void drawCell(GraphicsContext gc, Cell cell, int x, int y) {
        double px = y * cellSize;
        double py = x * cellSize;

        if (!cell.isRevealed()) {
            // Case non révélée
            gc.setFill(Color.web("#586868"));
            gc.fillRoundRect(px + 1, py + 1, cellSize - 2, cellSize - 2, 5, 5);
            
            if (cell.isFlagged()) {
                gc.drawImage(imageCache.get("flag"), px + 2, py + 2, cellSize - 4, cellSize - 4);
            }
        } else {
            // Case révélée
            gc.setFill(Color.web("#dddddd"));
            gc.fillRoundRect(px + 1, py + 1, cellSize - 2, cellSize - 2, 5, 5);

            if (cell.isBomb()) {
                gc.drawImage(imageCache.get("bomb2"), px + 2, py + 2, cellSize - 4, cellSize - 4);
            } else if (cell.getNeighborBombs() > 0) {
                Image numImg = imageCache.get("img" + cell.getNeighborBombs());
                if (numImg != null) {
                    gc.drawImage(numImg, px + 2, py + 2, cellSize - 4, cellSize - 4);
                }
            }
        }
    }

    private Label createLabel(String text, int col, int row, int colSpan, int rowSpan) {
        Label label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setStyle("-fx-background-color:transparent; -fx-text-fill:white");
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

    // Getters
    public Canvas getCanvas() { return canvas; }
    public int getCellSize() { return cellSize; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getFlagsLabel() { return flagsLabel; }
    public Label getMinesLabel() { return minesLabel; }
    public Label getStatusLabel() { return statusLabel; }
    public Button getResetButton() { return resetButton; }
}
