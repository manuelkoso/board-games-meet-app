package it.units.boardgamesmeetapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class UserInfo {

    private String userId;

    private String name;

    private String surname;

    private int age;

    private String favouritePlace;

    private String favouriteGame;

    public UserInfo() {
        this.userId = "";
        this.name = "";
        this.surname = "";
        this.age = 0;
        this.favouriteGame = "";
        this.favouritePlace = "";
    }

    public UserInfo(String name, String surname, int age, String favouritePlace, String favouriteGame) {
        this.userId = null;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.favouritePlace = favouritePlace;
        this.favouriteGame = favouriteGame;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUserId() {
        return userId;
    }

    public int getAge() {
        return age;
    }

    public String getFavouritePlace() {
        return favouritePlace;
    }

    public String getFavouriteGame() {
        return favouriteGame;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavouritePlace(String favouritePlace) {
        this.favouritePlace = favouritePlace;
    }

    public void setFavouriteGame(String favouriteGame) {
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
        result.put("id", userId);
        result.put("name", name);
        result.put("surname", surname);
        result.put("age", age);
        result.put("favouritePlace", favouritePlace);
        result.put("favouriteGame", favouriteGame);
        return result;
    }

}
