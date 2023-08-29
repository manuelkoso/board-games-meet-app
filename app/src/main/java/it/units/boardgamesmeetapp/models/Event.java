package it.units.boardgamesmeetapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Event {

    private String ownerUsername;
    private Game game;
    private List<String> players;
    private Location location;
    private String date;
    private String time;

    public Event(String ownerUsername, Game game, Location location, String date, String time) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.date = date;
        this.time = time;
        this.location = location;
        players = new ArrayList<>(game.getNumberOfPlayers());
        players.add(ownerUsername);
    }

    // needed for Firebase
    public Event() {
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Game getGame() {
        return game;
    }

    public List<String> getPlayers() {
        return players;
    }

    public Location getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
