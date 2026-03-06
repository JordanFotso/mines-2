package com.example.promines;

import com.example.promines.controller.GameController;
import com.example.promines.model.Board;
import com.example.promines.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final String TITLE = "ALIAS - Mines (Refactored)";
    private static final int WIDTH = 750;
    private static final int HEIGHT = 750;

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialiser le modèle (25x17 cases, 50 mines)
        Board board = new Board(25, 17, 50);

        // 2. Initialiser la vue
        GameView gameView = new GameView(25, 17);

        // 3. Créer la scène
        Scene scene = new Scene(gameView);

        // 4. Initialiser le contrôleur pour lier le modèle et la vue
        new GameController(board, gameView, primaryStage);

        // 5. Configurer et afficher la fenêtre
        primaryStage.setScene(scene);
        primaryStage.setTitle(TITLE);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
