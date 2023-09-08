package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.utils.Result;

public class NewEventViewModel extends ViewModel {

    private final FirebaseFirestore database;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<Result> submissionResult = new MutableLiveData<>();

    NewEventViewModel(FirebaseAuth firebaseAuth, FirebaseFirestore database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        this.submissionResult.setValue(Result.NONE);
    }

    public void addNewActivity(String game, String numberOfPlayers, String place, String date, String time) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(date.equals("Date") || time.equals("Time") || user == null || !isInputStringValid(game, numberOfPlayers, place, date, time)) {
            submissionResult.setValue(Result.FAILURE);
            return;
        }
        String timestamp = date + " " + time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        Date d = null;
        try {
            d = dateFormat.parse(timestamp);
        } catch (ParseException ignored) {
        }
        String key = database.collection("activities").document().getId();
        Event event = new Event(user.getUid(), game, Integer.parseInt(numberOfPlayers), place, d.getTime());
        event.setKey(key);
        database.collection("activities").document(key).set(event).addOnCompleteListener(task -> {
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