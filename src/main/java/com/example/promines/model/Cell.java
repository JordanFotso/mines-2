package com.example.promines.model;

public class Cell {
    private final int x;
    private final int y;
    private boolean isBomb;
    private int neighborBombs;
    private boolean isRevealed;
    private boolean isFlagged;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.isBomb = false;
        this.neighborBombs = 0;
        this.isRevealed = false;
        this.isFlagged = false;
    }

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isBomb() { return isBomb; }
    public void setBomb(boolean bomb) { isBomb = bomb; }
    public int getNeighborBombs() { return neighborBombs; }
    public void setNeighborBombs(int neighborBombs) { this.neighborBombs = neighborBombs; }
    public boolean isRevealed() { return isRevealed; }
    public void setRevealed(boolean revealed) { isRevealed = revealed; }
    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { isFlagged = flagged; }
}
