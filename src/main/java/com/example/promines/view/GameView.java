package com.example.promines.view;

import com.example.promines.model.Board;
import com.example.promines.model.Cell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

public class GameView extends VBox {
    private final Canvas canvas;
    private final Pane boardWrapper;
    private double cellW, cellH; 
    private final Label timerLabel;
    private final Label flagsLabel;
    private final Label minesLabel;
    private final Label statusLabel;
    private final Button resetButton;
    
    // Menu items
    private final MenuItem mnuNew, mnuScores, mnuQuit, mnuHelp, mnuAbout;
    private final CheckMenuItem mnuKeyboardMode;

    private final Map<String, Image> imageCache = new HashMap<>();
    private Board currentBoard;
    private int cursorX = 0, cursorY = 0; 
    private boolean isKeyboardActive = false; 

    private final Color[] numColors = {
        null, Color.web("#3498db"), Color.web("#27ae60"), Color.web("#e74c3c"), 
        Color.web("#9b59b6"), Color.web("#f39c12"), Color.web("#1abc9c"), 
        Color.web("#2c3e50"), Color.web("#7f8c8d")
    };

    public GameView() {
        this.setStyle("-fx-background-color: #121212;");

        // --- BARRE DE MENU ---
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #1e1e1e;");
        
        Menu mnuGame = new Menu("Jeu");
        mnuNew = new MenuItem("Nouvelle Partie");
        mnuScores = new MenuItem("Meilleurs Scores");
        mnuQuit = new MenuItem("Quitter");
        mnuGame.getItems().addAll(mnuNew, new SeparatorMenuItem(), mnuScores, new SeparatorMenuItem(), mnuQuit);

        Menu mnuOptions = new Menu("Options");
        mnuKeyboardMode = new CheckMenuItem("Activer le mode clavier");
        mnuKeyboardMode.setSelected(true); // Activé par défaut
        mnuOptions.getItems().add(mnuKeyboardMode);

        Menu mnuAide = new Menu("Aide");
        mnuHelp = new MenuItem("Règles du jeu");
        mnuAbout = new MenuItem("À propos");
        mnuAide.getItems().addAll(mnuHelp, mnuAbout);

        menuBar.getMenus().addAll(mnuGame, mnuOptions, mnuAide);

        // --- CONTENU PRINCIPAL ---
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.CENTER);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        this.boardWrapper = new Pane();
        HBox.setHgrow(boardWrapper, Priority.ALWAYS);
        boardWrapper.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 12; -fx-border-color: #333; -fx-border-radius: 12; -fx-border-width: 3;");
        
        this.canvas = new Canvas();
        canvas.widthProperty().bind(boardWrapper.widthProperty());
        canvas.heightProperty().bind(boardWrapper.heightProperty());
        boardWrapper.getChildren().add(canvas);

        VBox sidebar = new VBox(20);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);
        sidebar.setPadding(new Insets(15));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 20; -fx-border-color: #333; -fx-border-width: 1;");

        Label title = new Label("BLOODLINK\nMINES");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web("#e74c3c"));
        sidebar.getChildren().add(title);

        this.timerLabel = createStatCard(sidebar, "TEMPS", "/images/time2.png", "00:00:00");
        this.flagsLabel = createStatCard(sidebar, "DRAPEAUX", "/images/flag.png", "00");
        this.minesLabel = createStatCard(sidebar, "MINES", "/images/bomb3.png", "0");

        statusLabel = new Label("PRÊT ?");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        statusLabel.setTextFill(Color.web("#2ecc71"));
        VBox.setMargin(statusLabel, new Insets(20, 0, 10, 0));
        sidebar.getChildren().add(statusLabel);

        resetButton = new Button("NOUVELLE PARTIE");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 10;");
        
        sidebar.getChildren().add(resetButton);
        mainContent.getChildren().addAll(boardWrapper, sidebar);

        this.getChildren().addAll(menuBar, mainContent);

        canvas.widthProperty().addListener(e -> drawBoard(currentBoard));
        canvas.heightProperty().addListener(e -> drawBoard(currentBoard));

        loadImages();
    }

    public void drawBoard(Board board) {
        if (board == null) return;
        this.currentBoard = board;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        this.cellW = canvas.getWidth() / board.getHeight();
        this.cellH = canvas.getHeight() / board.getWidth();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                drawCell(gc, board.getCell(x, y), x, y);
            }
        }

        // --- SÉLECTEUR CLAVIER ---
        if (isKeyboardActive && mnuKeyboardMode.isSelected()) {
            gc.setStroke(Color.web("#f1c40f")); 
            gc.setLineWidth(3);
            gc.strokeRoundRect(cursorY * cellW + 2, cursorX * cellH + 2, cellW - 4, cellH - 4, 8, 8);
            
            gc.setStroke(Color.web("#f1c40f44"));
            gc.setLineWidth(6);
            gc.strokeRoundRect(cursorY * cellW, cursorX * cellH, cellW, cellH, 10, 10);
        }
    }

    public void moveCursor(int dx, int dy) {
        if (!mnuKeyboardMode.isSelected()) return;
        isKeyboardActive = true;
        cursorX = Math.max(0, Math.min(currentBoard.getWidth() - 1, cursorX + dx));
        cursorY = Math.max(0, Math.min(currentBoard.getHeight() - 1, cursorY + dy));
        drawBoard(currentBoard);
    }

    private void drawCell(GraphicsContext gc, Cell cell, int x, int y) {
        double px = y * cellW;
        double py = x * cellH;
        double sw = cellW - 2;
        double sh = cellH - 2;

        if (!cell.isRevealed()) {
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#4e5a5a")),
                    new Stop(1, Color.web("#2c3e50")));
            gc.setFill(gradient);
            gc.fillRoundRect(px, py, sw, sh, 4, 4);
            gc.setStroke(Color.web("#ffffff33"));
            gc.setLineWidth(1.5);
            gc.strokeLine(px + 1, py + 1, px + sw - 1, py + 1);
            gc.strokeLine(px + 1, py + 1, px + 1, py + sh - 1);
            gc.setStroke(Color.web("#00000066"));
            gc.strokeLine(px + 1, py + sh - 1, px + sw - 1, py + sh - 1);
            gc.strokeLine(px + sw - 1, py + 1, px + sw - 1, py + sh - 1);
            if (cell.isFlagged()) {
                double iconSize = Math.min(sw, sh) * 0.6;
                gc.drawImage(imageCache.get("flag"), px + (sw-iconSize)/2, py + (sh-iconSize)/2, iconSize, iconSize);
            }
        } else {
            if (cell.isBomb()) {
                javafx.scene.paint.RadialGradient explosion = new javafx.scene.paint.RadialGradient(0, 0, px + sw/2, py + sh/2, Math.max(sw, sh)/2, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#ff9f43")), new Stop(0.6, Color.web("#ee5253")), new Stop(1, Color.web("#2f3542")));
                gc.setFill(explosion);
                gc.fillRoundRect(px, py, sw, sh, 4, 4);
                gc.setStroke(Color.web("#1e272e"));
                gc.setLineWidth(1.5);
                for (int k = 0; k < 6; k++) {
                    double angle = k * Math.PI / 3 + (x*y % 10) * 0.1;
                    gc.strokeLine(px + sw/2, py + sh/2, px + sw/2 + Math.cos(angle) * sw/2, py + sh/2 + Math.sin(angle) * sh/2);
                }
                double iconSize = Math.min(sw, sh) * 0.75;
                gc.drawImage(imageCache.get("bomb2"), px + (sw-iconSize)/2, py + (sh-iconSize)/2, iconSize, iconSize);
            } else {
                gc.setFill(Color.web("#2c2c2c"));
                gc.fillRoundRect(px, py, sw, sh, 2, 2);
                gc.setStroke(Color.web("#00000099"));
                gc.setLineWidth(1);
                gc.strokeLine(px, py, px + sw, py);
                gc.strokeLine(px, py, px, py + sh);
                gc.setStroke(Color.web("#ffffff11"));
                gc.strokeLine(px, py + sh, px + sw, py + sh);
                gc.strokeLine(px + sw, py, px + sw, py + sh);
                if (cell.getNeighborBombs() > 0) {
                    gc.setFill(numColors[cell.getNeighborBombs()]);
                    gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, Math.min(sw, sh) * 0.7));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText(String.valueOf(cell.getNeighborBombs()), px + sw/2, py + sh*0.75);
                }
            }
        }
    }

    private Label createStatCard(VBox parent, String title, String iconPath, String initialValue) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 15; -fx-border-color: #333;");
        card.setAlignment(Pos.CENTER);
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iv = new ImageView(icon);
            iv.setFitWidth(18); iv.setFitHeight(18);
            header.getChildren().add(iv);
        } catch (Exception e) {}
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#888"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        header.getChildren().add(titleLabel);
        Label valueLabel = new Label(initialValue);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 24));
        card.getChildren().addAll(header, valueLabel);
        parent.getChildren().add(card);
        return valueLabel;
    }

    private void loadImages() {
        String[] images = {"flag", "bomb2"};
        for (String img : images) {
            try {
                imageCache.put(img, new Image(getClass().getResourceAsStream("/images/" + img + ".png")));
            } catch (Exception e) {}
        }
    }

    public Canvas getCanvas() { return canvas; }
    public double getCellW() { return cellW; }
    public double getCellH() { return cellH; }
    public int getCursorX() { return cursorX; }
    public int getCursorY() { return cursorY; }
    public void setKeyboardActive(boolean active) { 
        this.isKeyboardActive = active; 
        drawBoard(currentBoard);
    }
    public Label getTimerLabel() { return timerLabel; }
    public Label getFlagsLabel() { return flagsLabel; }
    public Label getMinesLabel() { return minesLabel; }
    public Label getStatusLabel() { return statusLabel; }
    public Button getResetButton() { return resetButton; }
    public MenuItem getMnuNew() { return mnuNew; }
    public MenuItem getMnuScores() { return mnuScores; }
    public MenuItem getMnuQuit() { return mnuQuit; }
    public MenuItem getMnuHelp() { return mnuHelp; }
    public MenuItem getMnuAbout() { return mnuAbout; }
    public CheckMenuItem getMnuKeyboardMode() { return mnuKeyboardMode; }
}
