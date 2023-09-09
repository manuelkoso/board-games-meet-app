package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

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

    public void addNewEvent(@NonNull String game, @NonNull String numberOfPlayers, @NonNull String place, @NonNull String date, @NonNull String time) {
        if (Stream.of(game, numberOfPlayers, place, date, time).anyMatch(String::isEmpty)) {
            submissionResult.setValue(Result.FAILURE);
            return;
        }
        try {
            String key = database.collection(FirebaseConfig.EVENTS).document().getId();
            Event event = new Event(key, Objects.requireNonNull(firebaseAuth.getUid()), game, Integer.parseInt(numberOfPlayers), place, fromDateTimeStringToTimestamp(date, time));

            database.collection(FirebaseConfig.EVENTS).document(key).set(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(FirebaseConfig.TAG, "Data successfully written.");
                    submissionResult.setValue(Result.SUCCESS);
                } else {
                    Log.w(FirebaseConfig.TAG, task.getException());
                    submissionResult.setValue(Result.FAILURE);
                }
            });

        } catch (ParseException e) {
            Log.w(FirebaseConfig.TAG, e);
            submissionResult.setValue(Result.FAILURE);
        }

    }

    private long fromDateTimeStringToTimestamp(@NonNull String date, @NonNull String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return Objects.requireNonNull(dateFormat.parse(date + " " + time)).getTime();
    }

    public MutableLiveData<Result> getSubmissionResult() {
        return submissionResult;
    }
}