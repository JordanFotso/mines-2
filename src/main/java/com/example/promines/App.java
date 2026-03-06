package com.example.promines;

import com.example.promines.controller.GameController;
import com.example.promines.model.Board;
import com.example.promines.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final String TITLE = "BLOODLINK - Mines";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        // 1. Calcul du nombre de cases pour remplir parfaitement l'espace (basé sur ~30px par case)
        // Espace horizontal pour le plateau : 1000 - 220 (sidebar) - 60 (marges) = 720
        // Espace vertical : 700 - 40 (marges) = 660
        int cols = 24; 
        int rows = 22;
        int mines = (rows * cols) / 6; // Environ 15% de mines

        // 2. Initialiser le modèle
        Board board = new Board(rows, cols, mines);

        // 3. Initialiser la vue (sans paramètres car elle s'adapte)
        GameView gameView = new GameView();

        // 4. Créer la scène
        Scene scene = new Scene(gameView);
        primaryStage.setScene(scene);

        // 5. Initialiser le contrôleur
        new GameController(board, gameView, primaryStage);

        // 6. Configurer et afficher
        primaryStage.setTitle(TITLE);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
