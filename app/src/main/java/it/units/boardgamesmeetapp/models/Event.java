package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Event {

    private String ownerUsername;
    private Game game;
    private List<String> players;
    private Location location;
    private long timestamp;

    public Event(@NonNull String ownerUsername, @NonNull Game game, @NonNull Location location, long timestamp) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.timestamp = timestamp;
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

    public long getTime() {
        return timestamp;
    }

}
