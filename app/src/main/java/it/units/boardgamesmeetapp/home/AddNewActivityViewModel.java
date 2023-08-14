package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Activity;

public class AddNewActivityViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final FirebaseAuth firebaseAuth;

    AddNewActivityViewModel(FirebaseAuth firebaseAuth, FirebaseDatabase database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
    }

    public void addNewActivity(String game, String date, String time, int number_of_players, String place) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Activity activity = new Activity(user.getEmail(), number_of_players, game, place, date, time);
        DatabaseReference databaseReference = database.getReference("activities");
        databaseReference.push().setValue(activity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Data successfully written.");
            } else {
                Log.d(FirebaseConfig.TAG, task.getException().getMessage());
            }
        });
    }


}