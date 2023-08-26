package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.Game;
import it.units.boardgamesmeetapp.models.Location;

public class NewEventViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final FirebaseAuth firebaseAuth;

    NewEventViewModel(FirebaseAuth firebaseAuth, FirebaseDatabase database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
    }

    public void addNewActivity(String game, String date, long timestamp, int numberOfPlayers, String place) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Event event = new Event(user.getEmail(), new Game(game, numberOfPlayers), new Location(place), timestamp);
        DatabaseReference databaseReference = database.getReference("activities");
        databaseReference.push().setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Data successfully written.");
            } else {
                Log.d(FirebaseConfig.TAG, task.getException().getMessage());
            }
        });
    }


}