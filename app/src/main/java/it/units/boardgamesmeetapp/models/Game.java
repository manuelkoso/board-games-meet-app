package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

public class Game {

    public String name;
    public int numberOfPlayers;

    // Needed for Firebase
    public Game() {}

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
