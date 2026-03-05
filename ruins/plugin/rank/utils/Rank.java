package fr.ruins.plugin.rank.utils;

public class Rank {

    private final String name;
    private final String color;
    private final int minScore;
    private final int basePrime;

    public Rank(String name, String color, int minScore, int basePrime) {
        this.name = name;
        this.color = color;
        this.minScore = minScore;
        this.basePrime = basePrime;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public int getMinScore() { return minScore; }
    public int getBasePrime() { return basePrime; }
}
