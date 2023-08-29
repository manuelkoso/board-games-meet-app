package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.Game;
import it.units.boardgamesmeetapp.models.Location;
import it.units.boardgamesmeetapp.utils.Result;

public class NewEventViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<Result> submissionResult = new MutableLiveData<>();

    NewEventViewModel(FirebaseAuth firebaseAuth, FirebaseDatabase database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        this.submissionResult.setValue(Result.NONE);
    }

    public void addNewActivity(String game, String numberOfPlayers, String place, String date, String time) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null || !isInputStringValid(game, numberOfPlayers, place, date, time)) {
            submissionResult.setValue(Result.FAILURE);
            return;
        }
        Event event = new Event(user.getEmail(), new Game(game, Integer.parseInt(numberOfPlayers)), new Location(place), date, time);
        DatabaseReference databaseReference = database.getReference("activities");
        databaseReference.push().setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Data successfully written.");
                submissionResult.setValue(Result.SUCCESS);
            } else {
                Log.d(FirebaseConfig.TAG, task.getException().getMessage());
                submissionResult.setValue(Result.FAILURE);
            }
        });
    }

    private boolean isInputStringValid(String... inputs) {
        for(String current : inputs) {
            if(current == null || current.isEmpty()) return false;
        }
        return true;
    }

    public MutableLiveData<Result> getSubmissionResult() {
        return submissionResult;
    }
}