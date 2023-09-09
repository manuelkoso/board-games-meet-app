package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Event {

    private String key;
    private String ownerId;
    private String game;
    private int maxNumberOfPlayers;
    private List<String> players;
    private String location;
    private long timestamp;

    public Event(@NonNull String key, @NonNull String ownerId, @NonNull String game, int maxNumberOfPlayers, @NonNull String location, long timestamp) {
        this.ownerId = ownerId;
        this.game = game;
        this.timestamp = timestamp;
        this.key = key;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.location = location;
        players = new ArrayList<>(maxNumberOfPlayers);
        players.add(ownerId);
    }

    // needed for Firebase
    public Event() {
    }

    @NonNull
    public String getOwnerId() {
        return ownerId;
    }

    @NonNull
    public String getGame() {
        return game;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    @NonNull
    public List<String> getPlayers() {
        return players;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void addPlayer(String playerId) {
        if (!players.contains(playerId) && players.size() < maxNumberOfPlayers)
            this.players.add(playerId);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("ownerId", ownerId);
        result.put("game", game);
        result.put("maxNumberOfPlayers", maxNumberOfPlayers);
        result.put("players", players);
        result.put("location", location);
        result.put("timestamp", timestamp);
        return result;
    }

}
