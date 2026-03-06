package com.example.promines.model;

import java.util.Random;

public class Board {
    private final int width;
    private final int height;
    private final int totalMines;
    private final Cell[][] cells;
    private int flaggedCount = 0;
    private boolean isGameOver = false;

    public Board(int width, int height, int totalMines) {
        this.width = width;
        this.height = height;
        this.totalMines = totalMines;
        this.cells = new Cell[width][height];
        initialize();
    }

    private void initialize() {
        // Init cells
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        // Place mines
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < totalMines) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            if (!cells[x][y].isBomb()) {
                cells[x][y].setBomb(true);
                minesPlaced++;
            }
        }

        // Calculate neighbors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!cells[x][y].isBomb()) {
                    cells[x][y].setNeighborBombs(countMinesAround(x, y));
                }
            }
        }
    }

    private int countMinesAround(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (cells[nx][ny].isBomb()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void revealCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height || cells[x][y].isRevealed() || cells[x][y].isFlagged() || isGameOver) {
            return;
        }

        Cell cell = cells[x][y];
        cell.setRevealed(true);

        if (cell.isBomb()) {
            isGameOver = true;
            return;
        }

        if (cell.getNeighborBombs() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealCell(x + i, y + j);
                }
            }
        }
    }

    public void toggleFlag(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height || cells[x][y].isRevealed() || isGameOver) {
            return;
        }
        Cell cell = cells[x][y];
        cell.setFlagged(!cell.isFlagged());
        if (cell.isFlagged()) flaggedCount++;
        else flaggedCount--;
    }

    public void chordCell(int x, int y) {
        Cell cell = cells[x][y];
        if (!cell.isRevealed() || cell.isBomb() || isGameOver) return;

        int flagsAround = countFlagsAround(x, y);
        if (flagsAround == cell.getNeighborBombs()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nx = x + i;
                    int ny = y + j;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        Cell neighbor = cells[nx][ny];
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            revealCell(nx, ny);
                        }
                    }
                }
            }
        }
    }

    private int countFlagsAround(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (cells[nx][ny].isFlagged()) count++;
                }
            }
        }
        return count;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Cell getCell(int x, int y) { return cells[x][y]; }
    public int getFlaggedCount() { return flaggedCount; }
    public int getTotalMines() { return totalMines; }
    public boolean isGameOver() { return isGameOver; }
}
