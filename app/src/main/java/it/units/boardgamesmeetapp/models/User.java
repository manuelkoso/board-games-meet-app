package it.units.boardgamesmeetapp.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class User {

    @NotNull
    private String userId;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String email;

    private User() {}

    public User(@NotNull String userId, @NotNull String name, @NotNull String surname, @NotNull String email) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    @NotNull
    public String getUserId() {
        return userId;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getSurname() {
        return surname;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) && name.equals(user.name) && surname.equals(user.surname) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, surname, email);
    }
}
