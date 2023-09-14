package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class EventInfoView {

    private final String game;
    private final String maxNumberOfPlayers;
    private final String place;
    private final String date;
    private final String time;
    private final String numberOfPlayers;

    public EventInfoView(@Nullable String game, @Nullable String maxNumberOfPlayers, @Nullable String place, @Nullable String date, @Nullable String time) {
        this.game = game;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.place = place;
        this.date = date;
        this.time = time;
        this.numberOfPlayers = null;
    }

    public EventInfoView(@NonNull Event event) {
        this.game = event.getGame();
        this.maxNumberOfPlayers = String.valueOf(event.getMaxNumberOfPlayers());
        this.place = event.getPlace();
        Date tmpDate = new Date(event.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.date = dateFormat.format(tmpDate);
        this.time = timeFormat.format(tmpDate);
        this.numberOfPlayers = String.valueOf(event.getPlayers().size());
    }

    public String getGame() {
        return game;
    }

    public String getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventInfoView that = (EventInfoView) o;
        return Objects.equals(game, that.game) && Objects.equals(maxNumberOfPlayers, that.maxNumberOfPlayers) && Objects.equals(place, that.place) && Objects.equals(date, that.date) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, maxNumberOfPlayers, place, date, time);
    }

    public boolean isAnyFieldEmpty() {
        return Stream.of(game, maxNumberOfPlayers, place, date, time).anyMatch(String::isEmpty);
    }

}
