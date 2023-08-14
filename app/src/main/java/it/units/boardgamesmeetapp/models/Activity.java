package it.units.boardgamesmeetapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Activity {

    public String ownerUsername;
    public String game;
    public int numberOfPlayers;
    public List<String> players;
    public String place;
    public String date;
    public String time;

    public Activity() {

    }

    public Activity(String ownerUsername, int numberOfPlayers, String game, String place, String date, String time) {
        this.ownerUsername = ownerUsername;
        this.game = game;
        this.numberOfPlayers = numberOfPlayers;
        this.place = place;
        this.date = date;
        this.time = time;
        players = new ArrayList<>(numberOfPlayers);
        players.add(ownerUsername);
    }


}
