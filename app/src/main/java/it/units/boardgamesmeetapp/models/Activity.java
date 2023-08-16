package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Activity {

    public final String ownerUsername;
    public final Game game;
    public final List<String> players;
    public final Location location;
    public final LocalDateTime time;

    public Activity(@NonNull String ownerUsername, @NonNull Game game, @NonNull Location location, @NonNull LocalDateTime time) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.time = time;
        this.location = location;
        players = new ArrayList<>(game.getNumberOfPlayers());
        players.add(ownerUsername);
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
}
