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
        updateAllViews();
        startTimer();
    }

    private void initHandlers() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                final int fx = x;
                final int fy = y;
                gameView.getCellView(x, y).setOnMouseClicked(event -> {
                    if (board.isGameOver()) return;

                    if (event.getButton() == MouseButton.PRIMARY) {
                        board.revealCell(fx, fy);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        board.toggleFlag(fx, fy);
                    }
                    updateAllViews();
                    checkGameStatus();
                });
            }
        }

        gameView.getResetButton().setOnAction(e -> resetGame());
    }

    private void updateAllViews() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                gameView.getCellView(x, y).update(board.getCell(x, y));
            }
        }
        gameView.getFlagsLabel().setText(String.format("%02d", board.getFlaggedCount()));
        gameView.getMinesLabel().setText(String.valueOf(board.getTotalMines()));
    }

    private void checkGameStatus() {
        if (board.isGameOver()) {
            gameView.getStatusLabel().setText("BOOM !");
            timeline.stop();
            revealAllMines();
        } else if (checkWin()) {
            gameView.getStatusLabel().setText("GAGNÉ !");
            timeline.stop();
        }
    }

    private boolean checkWin() {
        // Simple win condition: all non-bomb cells are revealed
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
                    gameView.getCellView(x, y).update(cell);
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
        // Pour réinitialiser, on peut simplement relancer l'application ou recréer les objets
        Board newBoard = new Board(25, 17, 50);
        GameView newView = new GameView(25, 17);
        primaryStage.getScene().setRoot(newView);
        new GameController(newBoard, newView, primaryStage);
    }
}
