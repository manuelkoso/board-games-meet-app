package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;
public class Location {
    private String place;

    // Needed for Firebase
    public Location() {}
    public Location(@NonNull String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }
    @NonNull
    @Override
    public String toString() {
        return place;
    }
}
