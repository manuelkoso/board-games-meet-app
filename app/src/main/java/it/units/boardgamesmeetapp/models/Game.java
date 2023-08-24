package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

public class Game {

    private final String name;
    private final int numberOfPlayers;

    public Game(String name, int numberOfPlayers) {
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
