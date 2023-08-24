package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Activity {

    private String ownerUsername;
    private Game game;
    private List<String> players;
    private Location location;
    private String time;

    public Activity(@NonNull String ownerUsername, @NonNull Game game, @NonNull Location location, @NonNull String time) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.time = time;
        this.location = location;
        players = new ArrayList<>(game.getNumberOfPlayers());
        players.add(ownerUsername);
    }

    // needed for Firebase
    public Activity() {}

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
    public String getTime() {
        return time;
    }

}
