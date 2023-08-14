package it.units.boardgamesmeetapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Activity {

    public String message;

    public Activity() {

    }
    public Activity(String message) {
        this.message = message;
    }
}
