package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.utils.Result;

public class NewEventViewModel extends ViewModel {

    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Result> submissionResult = new MutableLiveData<>();
    private final MutableLiveData<String> currentGame = new MutableLiveData<>();
    private final MutableLiveData<String> currentNumberOfPlayers = new MutableLiveData<>();
    private final MutableLiveData<String> currentPlace = new MutableLiveData<>();
    private final MutableLiveData<String> currentDate = new MutableLiveData<>();
    private final MutableLiveData<String> currentTime = new MutableLiveData<>();

    public NewEventViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        resetSubmissionResult();
    }

    public void addNewEvent(@NonNull String game, @NonNull String numberOfPlayers, @NonNull String place, @NonNull String date, @NonNull String time) {
        if (Stream.of(game, numberOfPlayers, place, date, time).anyMatch(String::isEmpty)) {
            submissionResult.setValue(Result.FAILURE);
            return;
        }
        try {
            String key = firebaseFirestore.collection(FirebaseConfig.EVENTS).document().getId();
            long timestamp = fromDateTimeStringToTimestamp(date, time);
            if(timestamp < new Date().getTime()) {
                submissionResult.setValue(Result.OLD_DATE);
                return;
            }
            Event event = new Event(key, Objects.requireNonNull(firebaseAuth.getUid()), game, Integer.parseInt(numberOfPlayers), place, fromDateTimeStringToTimestamp(date, time));
            firebaseFirestore.collection(FirebaseConfig.EVENTS).document(key).set(event).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(FirebaseConfig.TAG, "Data successfully written.");
                    submissionResult.setValue(Result.SUCCESS);
                    resetFieldValues();
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

    private void resetFieldValues() {
        Stream.of(currentDate, currentGame, currentPlace, currentTime, currentNumberOfPlayers).forEach(field -> field.setValue(null));
    }

    private long fromDateTimeStringToTimestamp(@NonNull String date, @NonNull String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return Objects.requireNonNull(dateFormat.parse(date + " " + time)).getTime();
    }

    public MutableLiveData<Result> getSubmissionResult() {
        return submissionResult;
    }

    public void resetSubmissionResult() {
        this.submissionResult.setValue(Result.NONE);
    }

    public void updateCurrentEvent(@NonNull String game, @NonNull String numberOfPlayers, @NonNull String place, @NonNull String date, @NonNull String time) {
        currentGame.setValue(game);
        currentNumberOfPlayers.setValue(numberOfPlayers);
        currentPlace.setValue(place);
        currentDate.setValue(date);
        currentTime.setValue(time);
    }

    public void updateCurrentEvent(@NonNull Event event) {
        currentGame.setValue(event.getGame());
        currentNumberOfPlayers.setValue(String.valueOf(event.getMaxNumberOfPlayers()));
        currentPlace.setValue(event.getLocation());
        Date date = new Date(event.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        currentDate.setValue(dateFormat.format(date));
        currentTime.setValue(timeFormat.format(date));
    }

    public MutableLiveData<String> getCurrentGame() {
        return currentGame;
    }

    public MutableLiveData<String> getCurrentNumberOfPlayers() {
        return currentNumberOfPlayers;
    }

    public MutableLiveData<String> getCurrentPlace() {
        return currentPlace;
    }

    public MutableLiveData<String> getCurrentDate() {
        return currentDate;
    }

    public MutableLiveData<String> getCurrentTime() {
        return currentTime;
    }

    public void modifyEvent(String eventKey, String game, String numberOfPlayers, String place, String date, String time) {
        if (Stream.of(game, numberOfPlayers, place, date, time).anyMatch(String::isEmpty)) {
            submissionResult.setValue(Result.FAILURE);
            return;
        }
        try {
            DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.EVENTS).document(eventKey);
            long timestamp = fromDateTimeStringToTimestamp(date, time);
            if(timestamp < new Date().getTime()) {
                submissionResult.setValue(Result.OLD_DATE);
                return;
            }
            Event event = new Event(eventKey, Objects.requireNonNull(firebaseAuth.getUid()), game, Integer.parseInt(numberOfPlayers), place, fromDateTimeStringToTimestamp(date, time));
            reference.update(event.toMap()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(FirebaseConfig.TAG, "Data successfully written.");
                    submissionResult.setValue(Result.SUCCESS);
                    resetFieldValues();
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

}