package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private String id;
    private String name;
    private String surname;
    private int age;
    private String favouritePlace;
    private String favouriteGame;

    public User() {
    }

    public User(@NonNull String id) {
        this.id = id;
        this.name = null;
        this.surname = null;
        this.age = 0;
        this.favouritePlace = null;
        this.favouriteGame = null;
    }

    public User(String id, String name, String surname, int age, String favouritePlace, String favouriteGame) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.favouritePlace = favouritePlace;
        this.favouriteGame = favouriteGame;
    }

    @NonNull
    public String getId() {
        return id;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("surname", surname);
        result.put("age", age);
        result.put("favouritePlace", favouritePlace);
        result.put("favouriteGame", favouriteGame);
        return result;
    }

}
