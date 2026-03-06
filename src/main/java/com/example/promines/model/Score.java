package com.example.promines.model;

import java.io.Serializable;

public class Score implements Serializable, Comparable<Score> {
    private final String name;
    private final int totalCentis; // Temps total en centièmes pour le tri

    public Score(String name, int totalCentis) {
        this.name = name;
        this.totalCentis = totalCentis;
    }

    public String getName() { return name; }
    public int getTotalCentis() { return totalCentis; }

    public String getTimeFormatted() {
        int c = totalCentis % 100;
        int s = (totalCentis / 100) % 60;
        int m = (totalCentis / 6000) % 60;
        int h = (totalCentis / 360000);
        return String.format("%02d:%02d:%02d:%02d", h, m, s, c);
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.totalCentis, other.totalCentis);
    }
}
