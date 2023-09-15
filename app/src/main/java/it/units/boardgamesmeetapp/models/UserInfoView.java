package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

public class UserInfoView {

    private final String name;
    private final String surname;
    private final String age;
    private final String favouritePlace;
    private final String favouriteGame;

    public UserInfoView(@NonNull User user) {
        name = initField(user.getName());
        surname = initField(user.getSurname());
        favouritePlace = initField(user.getFavouritePlace());
        favouriteGame = initField(user.getFavouriteGame());
        if (user.getAge() == 0) {
            age = "";
        } else {
            age = String.valueOf(user.getAge());
        }
    }

    private String initField(@Nullable String input) {
        if(input == null) {
            return "";
        }
        return input;
    }

    public UserInfoView(String name, String surname, String age, String favouritePlace, String favouriteGame) {
        this.name = initField(name);
        this.surname = initField(surname);
        this.age = initField(age);
        this.favouritePlace = initField(favouritePlace);
        this.favouriteGame = initField(favouriteGame);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAge() {
        return age;
    }

    public String getFavouritePlace() {
        return favouritePlace;
    }

    public String getFavouriteGame() {
        return favouriteGame;
    }

    public String getNameAndSurname() {
        return name + " " + surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoView that = (UserInfoView) o;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(age, that.age) && Objects.equals(favouritePlace, that.favouritePlace) && Objects.equals(favouriteGame, that.favouriteGame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age, favouritePlace, favouriteGame);
    }
}
