package com.example.promines.controller;

import com.example.promines.model.Board;
import com.example.promines.model.Cell;
import com.example.promines.view.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
    private final Board board;
    private final GameView gameView;
    private final Stage primaryStage;
    
    private Timeline timeline;
    private int hours = 0, mins = 0, secs = 0, centis = 0;

    public GameController(Board board, GameView gameView, Stage primaryStage) {
        this.board = board;
        this.gameView = gameView;
        this.primaryStage = primaryStage;
        
        initHandlers();
        updateDisplay();
        startTimer();
    }

    private void initHandlers() {
        // Un seul écouteur pour tout le plateau (Canvas)
        gameView.getCanvas().setOnMouseClicked(event -> {
            if (board.isGameOver()) return;

            // Calcul de la case cliquée
            int cellSize = gameView.getCellSize();
            int y = (int) (event.getX() / cellSize);
            int x = (int) (event.getY() / cellSize);

            // Vérification des limites
            if (x >= 0 && x < board.getWidth() && y >= 0 && y < board.getHeight()) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    board.revealCell(x, y);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    board.toggleFlag(x, y);
                }
                updateDisplay();
                checkGameStatus();
            }
        });

        gameView.getResetButton().setOnAction(e -> resetGame());
    }

    private void updateDisplay() {
        // On demande au Canvas de tout redessiner
        gameView.drawBoard(board);
        
        // Mise à jour des labels
        gameView.getFlagsLabel().setText(String.format("%02d", board.getFlaggedCount()));
        gameView.getMinesLabel().setText(String.valueOf(board.getTotalMines()));
    }

    private void checkGameStatus() {
        if (board.isGameOver()) {
            gameView.getStatusLabel().setText("BOOM !");
            timeline.stop();
            revealAllMines();
            updateDisplay();
        } else if (checkWin()) {
            gameView.getStatusLabel().setText("GAGNÉ !");
            timeline.stop();
        }
    }

    private boolean checkWin() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Cell cell = board.getCell(x, y);
                if (!cell.isBomb() && !cell.isRevealed()) return false;
            }
        }
        return true;
    }

    private void revealAllMines() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Cell cell = board.getCell(x, y);
                if (cell.isBomb()) {
                    cell.setRevealed(true);
                }
            }
        }
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            centis++;
            if (centis == 100) { secs++; centis = 0; }
            if (secs == 60) { mins++; secs = 0; }
            if (mins == 60) { hours++; mins = 0; }
            gameView.getTimerLabel().setText(String.format("%02d:%02d:%02d:%02d", hours, mins, secs, centis));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void resetGame() {
        if (timeline != null) timeline.stop();
        Board newBoard = new Board(25, 17, 50);
        GameView newView = new GameView(25, 17);
        primaryStage.getScene().setRoot(newView);
        new GameController(newBoard, newView, primaryStage);
    }
}
