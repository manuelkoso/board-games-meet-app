package it.units.boardgamesmeetapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Event {

    private String ownerUsername;
    private String game;
    private int maxNumberOfPlayers;
    private List<String> players;
    private String location;
    private String date;
    private String time;

    public Event(String ownerUsername, String game, int maxNumberOfPlayers, String location, String date, String time) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.date = date;
        this.time = time;
        this.location = location;
        players = new ArrayList<>(maxNumberOfPlayers);
        players.add(ownerUsername);
    }

    // needed for Firebase
    public Event() {
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getGame() {
        return game;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
