package com.example.promines.view;

import com.example.promines.model.Cell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CellView extends Label {
    private static final String STYLE_NORMAL = "-fx-background-color: #586868; -fx-border-width: 1px; -fx-border-radius: 10%; -fx-border-style: solid; -fx-background-clip: content-box;";
    private static final String STYLE_REVEALED = "-fx-background-color: #dddddd; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-radius: 10%; -fx-border-color: #dddddd; -fx-background-clip: content-box;";

    public CellView() {
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setAlignment(Pos.CENTER);
        this.setStyle(STYLE_NORMAL);
    }

    public void update(Cell cell) {
        if (!cell.isRevealed()) {
            if (cell.isFlagged()) {
                setIcon("/images/flag.png");
            } else {
                setGraphic(null);
            }
            setStyle(STYLE_NORMAL);
        } else {
            setStyle(STYLE_REVEALED);
            if (cell.isBomb()) {
                setIcon("/images/bomb2.png");
            } else {
                if (cell.getNeighborBombs() > 0) {
                    setIcon("/images/img" + cell.getNeighborBombs() + ".png");
                } else {
                    setGraphic(null);
                }
            }
        }
    }

    private void setIcon(String path) {
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(20);
            iv.setFitHeight(20);
            setGraphic(iv);
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
        }
    }
}
