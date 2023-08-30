package it.units.boardgamesmeetapp.models;

import java.util.Objects;

public class UserInfo {

    private String email;

    private String name;

    private String surname;

    private int age;

    private String favouritePlace;

    private String favouriteGame;

    public UserInfo() {
    }

    public UserInfo(String email, String name, String surname, int age, String favouritePlace, String favouriteGame) {
        this.email = email;
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

    public int getAge() {
        return age;
    }

    public String getFavouritePlace() {
        return favouritePlace;
    }

    public String getFavouriteGame() {
        return favouriteGame;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
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

}
