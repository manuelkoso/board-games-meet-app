package it.units.boardgamesmeetapp.models;

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
    private String date;
    private String time;

    public Event(String ownerId, String game, int maxNumberOfPlayers, String location, String date, String time) {
        this.ownerId = ownerId;
        this.game = game;
        this.date = date;
        this.time = time;
        this.key = null;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.location = location;
        players = new ArrayList<>(maxNumberOfPlayers);
        players.add(ownerId);
    }

    // needed for Firebase
    public Event() {
    }

    public String getOwnerId() {
        return ownerId;
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

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void addPlayer(String playerId) {
        if(!players.contains(playerId) && players.size() < maxNumberOfPlayers)
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
        result.put("date", date);
        result.put("time", time);
        return result;
    }

}
