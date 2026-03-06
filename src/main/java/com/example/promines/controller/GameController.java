package com.example.promines.controller;

import com.example.promines.model.Board;
import com.example.promines.model.Cell;
import com.example.promines.model.Score;
import com.example.promines.model.ScoreManager;
import com.example.promines.view.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

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
        
        gameView.drawBoard(board);
        initHandlers();
        initKeyboardHandlers();
        updateDisplay();
        startTimer();
    }

    private void initKeyboardHandlers() {
        // Utilisation d'un EventFilter pour capturer les touches AVANT tout le monde
        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!gameView.getMnuKeyboardMode().isSelected() || board.isGameOver()) return;

            boolean consumed = true;
            switch (event.getCode()) {
                case UP:    gameView.moveCursor(-1, 0); break;
                case DOWN:  gameView.moveCursor(1, 0);  break;
                case LEFT:  gameView.moveCursor(0, -1); break;
                case RIGHT: gameView.moveCursor(0, 1);  break;
                case SPACE:
                    if (event.isControlDown()) {
                        board.toggleFlag(gameView.getCursorX(), gameView.getCursorY());
                    } else {
                        board.revealCell(gameView.getCursorX(), gameView.getCursorY());
                    }
                    updateDisplay();
                    checkGameStatus();
                    break;
                default:
                    consumed = false;
                    break;
            }
            
            if (consumed) {
                event.consume(); // Empêche la barre de menu de réagir aux flèches
            }
        });
    }

    private void initHandlers() {
        gameView.getMnuNew().setOnAction(e -> resetGame());
        gameView.getMnuQuit().setOnAction(e -> System.exit(0));
        gameView.getMnuScores().setOnAction(e -> showHighScores());

        gameView.getMnuHelp().setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Règles du Jeu");
            alert.setHeaderText("Comment jouer ?");
            alert.setContentText("1. Révéler les cases sans mines.\n2. Chiffre = Mines autour.\n3. Drapeaux sur les mines.");
            alert.showAndWait();
        });

        gameView.getMnuAbout().setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("À Propos");
            alert.setHeaderText("BLOODLINK - Mines");
            alert.setContentText("Version 2.0 - Keyboard Enhanced");
            alert.showAndWait();
        });

        gameView.getCanvas().setOnMouseClicked(event -> {
            gameView.setKeyboardActive(false); 
            if (board.isGameOver()) return;

            double cellW = gameView.getCellW();
            double cellH = gameView.getCellH();
            int y = (int) (event.getX() / cellW);
            int x = (int) (event.getY() / cellH);

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
        gameView.drawBoard(board);
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
            handleWin();
        }
    }

    private void handleWin() {
        int totalCentis = centis + secs * 100 + mins * 6000 + hours * 360000;
        TextInputDialog dialog = new TextInputDialog("Joueur");
        dialog.setTitle("Victoire !");
        dialog.setHeaderText("Bravo !");
        dialog.setContentText("Nom pour le score :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            ScoreManager.saveScore(new Score(name, totalCentis));
            showHighScores();
        });
    }

    private void showHighScores() {
        List<Score> scores = ScoreManager.loadScores();
        StringBuilder sb = new StringBuilder("--- TOP 10 ---\n\n");
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            sb.append(String.format("%d. %-12s | %s\n", (i + 1), s.getName(), s.getTimeFormatted()));
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Scores");
        alert.setContentText(sb.toString());
        alert.showAndWait();
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
                if (cell.isBomb()) cell.setRevealed(true);
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
        Board newBoard = new Board(22, 24, (22*24)/6);
        GameView newView = new GameView();
        primaryStage.getScene().setRoot(newView);
        new GameController(newBoard, newView, primaryStage);
    }
}
