package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;
public class Location {
    private final String place;
    public Location(@NonNull String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

}
