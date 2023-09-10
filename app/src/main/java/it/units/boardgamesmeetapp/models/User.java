package it.units.boardgamesmeetapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    @NonNull
    private final String id;
    @NonNull
    private UserInfo info;

    public User() {
        this.id = "";
        info = new UserInfo();
    }

    public User(@NonNull String id) {
        this.id = id;
        info = new UserInfo();
    }

    public User(@NonNull String id, @NonNull UserInfo info) {
        this.id = id;
        this.info = info;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(@NonNull UserInfo info) {
        this.info = info;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("info", info.toMap());
        return result;
    }

}
