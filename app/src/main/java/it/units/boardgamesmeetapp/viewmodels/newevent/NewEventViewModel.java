package it.units.boardgamesmeetapp.viewmodels.newevent;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.utils.Result;
import it.units.boardgamesmeetapp.viewmodels.SubmissionResult;

public class NewEventViewModel extends ViewModel {

    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<SubmissionResult> submissionResult = new MutableLiveData<>();
    private final MutableLiveData<String> currentEventKey = new MutableLiveData<>();
    private final MutableLiveData<String> currentGame = new MutableLiveData<>();
    private final MutableLiveData<String> currentNumberOfPlayers = new MutableLiveData<>();
    private final MutableLiveData<String> currentPlace = new MutableLiveData<>();
    private final MutableLiveData<String> currentDate = new MutableLiveData<>();
    private final MutableLiveData<String> currentTime = new MutableLiveData<>();

    public NewEventViewModel(@NonNull FirebaseFirestore firebaseFirestore, @NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
        resetSubmissionResult();
    }

    public void submit() {
        try {
            if (checkInputError()) return;
            if (currentEventKey.getValue() == null) {
                currentEventKey.setValue(firebaseFirestore.collection(FirebaseConfig.EVENTS).document().getId());
            }
            submitEvent();
        } catch (ParseException e) {
            submissionResult.setValue(new SubmissionResult(Result.FAILURE, R.string.unknown_error));
        }
    }

    private boolean checkInputError() throws ParseException {
        if (isAnyFieldEmpty()) {
            submissionResult.setValue(new SubmissionResult(Result.EMPTY_FIELD, R.string.empty_field_error));
            return true;
        }
        if(Integer.parseInt(Objects.requireNonNull(currentNumberOfPlayers.getValue())) < 2) {
            submissionResult.setValue(new SubmissionResult(Result.WRONG_NUMBER_PLAYERS, R.string.number_of_players_error));
            return true;
        }
        if (hasPastDate()) {
            submissionResult.setValue(new SubmissionResult(Result.OLD_DATE, R.string.old_date_error));
            return true;
        }
        return false;
    }

    private boolean hasPastDate() throws ParseException {
        return fromDateTimeStringToTimestamp() < new Date().getTime();
    }

    private long fromDateTimeStringToTimestamp() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return Objects.requireNonNull(dateFormat.parse(currentDate.getValue() + " " + currentTime.getValue())).getTime();
    }

    private boolean isAnyFieldEmpty() {
        return Stream.of(currentGame.getValue(), currentDate.getValue(), currentPlace.getValue(), currentTime.getValue(), currentNumberOfPlayers.getValue()).anyMatch(value -> value == null || value.isEmpty());
    }

    public void submitEvent() throws ParseException {
        Event newEvent = new Event(currentEventKey.getValue(), Objects.requireNonNull(firebaseAuth.getUid()), currentGame.getValue(), Integer.parseInt(currentNumberOfPlayers.getValue()), currentPlace.getValue(), fromDateTimeStringToTimestamp());
        firebaseFirestore.collection(FirebaseConfig.EVENTS).document(Objects.requireNonNull(currentEventKey.getValue())).set(newEvent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, FirebaseConfig.DATA_WRITE_SUCCESS);
                submissionResult.setValue(new SubmissionResult(Result.SUCCESS, R.string.new_event_success));
                resetFieldValues();
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
                submissionResult.setValue(new SubmissionResult(Result.FAILURE, R.string.unknown_error));
            }
        });
    }

    private void resetFieldValues() {
        currentGame.setValue(null);
    }

    public MutableLiveData<SubmissionResult> getSubmissionResult() {
        return submissionResult;
    }

    public void resetSubmissionResult() {
        this.submissionResult.setValue(new SubmissionResult(Result.NONE));
    }

    public void updateCurrentEvent(@NonNull String game, @NonNull String numberOfPlayers, @NonNull String place, @NonNull String date, @NonNull String time) {
        currentGame.setValue(game);
        currentNumberOfPlayers.setValue(numberOfPlayers);
        currentPlace.setValue(place);
        currentDate.setValue(date);
        currentTime.setValue(time);
    }

    public void setEventKey(String eventKey) {
        currentEventKey.setValue(eventKey);
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
}