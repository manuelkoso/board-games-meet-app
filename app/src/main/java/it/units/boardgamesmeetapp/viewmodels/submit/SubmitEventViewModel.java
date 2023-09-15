package it.units.boardgamesmeetapp.viewmodels.submit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.EventInfoView;
import it.units.boardgamesmeetapp.utils.Result;
import it.units.boardgamesmeetapp.viewmodels.SubmissionResult;

public class SubmitEventViewModel extends ViewModel {

    public static final int MIN_NUMBER_OF_PLAYERS = 2;
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<SubmissionResult> submissionResult = new MutableLiveData<>();
    private final MutableLiveData<String> currentEventKey = new MutableLiveData<>();
    private final MutableLiveData<EventInfoView> currentEventInfoView = new MutableLiveData<>();
    private final MutableLiveData<EventInfoView> initialEventInfoView = new MutableLiveData<>();

    public SubmitEventViewModel(@NonNull FirebaseFirestore firebaseFirestore, @NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
        resetSubmissionResult();
    }

    public void submit() {
        try {
            if (checkCurrentEventInfoViewFieldValidity()) {
                if (currentEventKey.getValue() == null) {
                    currentEventKey.setValue(firebaseFirestore.collection(FirebaseConfig.EVENTS_REFERENCE).document().getId());
                }
                String game = Objects.requireNonNull(currentEventInfoView.getValue()).getGame().toUpperCase();
                String place = currentEventInfoView.getValue().getPlace().toUpperCase();
                int maxNumberOfPlayers = Integer.parseInt(currentEventInfoView.getValue().getMaxNumberOfPlayers());
                long timestamp = getTimestampFromDateTimeString(currentEventInfoView.getValue().getDate(), currentEventInfoView.getValue().getTime());
                Event newEvent = new Event(currentEventKey.getValue(), Objects.requireNonNull(firebaseAuth.getUid()), game, maxNumberOfPlayers, place, timestamp);
                submitEvent(newEvent);
            }
        } catch (ParseException exception) {
            Log.w("APP", exception);
        }
    }

    private boolean checkCurrentEventInfoViewFieldValidity() throws ParseException {
        if (currentEventInfoView.getValue() == null || Objects.requireNonNull(currentEventInfoView.getValue()).isAnyFieldEmpty()) {
            submissionResult.setValue(new SubmissionResult(Result.EMPTY_FIELD, R.string.empty_field_error));
            return false;
        }
        if (Integer.parseInt(currentEventInfoView.getValue().getMaxNumberOfPlayers()) < MIN_NUMBER_OF_PLAYERS) {
            submissionResult.setValue(new SubmissionResult(Result.WRONG_NUMBER_PLAYERS, R.string.number_of_players_error));
            return false;
        }
        if (getTimestampFromDateTimeString(currentEventInfoView.getValue().getDate(), currentEventInfoView.getValue().getTime()) < getCurrentTimestamp()) {
            submissionResult.setValue(new SubmissionResult(Result.OLD_DATE, R.string.old_date_error));
            return false;
        }
        return true;
    }

    private long getTimestampFromDateTimeString(@NonNull String date, @NonNull String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return Objects.requireNonNull(dateFormat.parse(date + " " + time)).getTime();
    }


    private static long getCurrentTimestamp() {
        return new Date().getTime();
    }

    public void submitEvent(@NonNull Event newEvent) {
        firebaseFirestore.collection(FirebaseConfig.EVENTS_REFERENCE).document(Objects.requireNonNull(newEvent.getKey())).set(newEvent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, FirebaseConfig.DATA_WRITE_SUCCESS);
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
            }
        });
        submissionResult.setValue(new SubmissionResult(Result.SUCCESS, R.string.new_event_success));
    }

    public MutableLiveData<SubmissionResult> getSubmissionResult() {
        return submissionResult;
    }

    public void resetSubmissionResult() {
        this.submissionResult.setValue(new SubmissionResult(Result.NONE));
    }

    public void updateCurrentEvent(@Nullable EventInfoView eventInfoView) {
        currentEventInfoView.setValue(eventInfoView);
    }

    public void setEventKey(String eventKey) {
        currentEventKey.setValue(eventKey);
    }

    public void setInitialEventInfoView(@Nullable EventInfoView eventInfoView) {
        initialEventInfoView.setValue(eventInfoView);
    }

    public MutableLiveData<EventInfoView> getInitialEventInfoView() {
        return initialEventInfoView;
    }

    public MutableLiveData<EventInfoView> getCurrentEventInfoView() {
        return currentEventInfoView;
    }

    public boolean isInitialEventInfoViewNull() {
        return initialEventInfoView.getValue() == null;
    }

    public boolean isCurrentEventInfoViewNull() {
        return currentEventInfoView.getValue() == null;
    }

}