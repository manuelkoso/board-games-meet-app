package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class UserInfo {
    @NonNull
    private String name;
    @NonNull
    private String surname;
    private int age;
    @NonNull
    private String favouritePlace;
    @NonNull
    private String favouriteGame;

    public UserInfo() {
        this.name = "";
        this.surname = "";
        this.age = 0;
        this.favouriteGame = "";
        this.favouritePlace = "";
    }

    public UserInfo(@NonNull String name, @NonNull String surname, int age, @NonNull String favouritePlace, @NonNull String favouriteGame) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.favouritePlace = favouritePlace;
        this.favouriteGame = favouriteGame;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    @NonNull
    public String getFavouritePlace() {
        return favouritePlace;
    }

    @NonNull
    public String getFavouriteGame() {
        return favouriteGame;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setSurname(@NonNull String surname) {
        this.surname = surname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavouritePlace(@NonNull String favouritePlace) {
        this.favouritePlace = favouritePlace;
    }

    public void setFavouriteGame(@NonNull String favouriteGame) {
        this.favouriteGame = favouriteGame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return age == userInfo.age && Objects.equals(name, userInfo.name) && Objects.equals(surname, userInfo.surname) && Objects.equals(favouritePlace, userInfo.favouritePlace) && Objects.equals(favouriteGame, userInfo.favouriteGame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age, favouritePlace, favouriteGame);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("surname", surname);
        result.put("age", age);
        result.put("favouritePlace", favouritePlace);
        result.put("favouriteGame", favouriteGame);
        return result;
    }

}
