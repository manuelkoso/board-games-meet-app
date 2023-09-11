package it.units.boardgamesmeetapp.dashboard;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;

public class DashboardViewModel extends ViewModel {

    @NonNull
    private final FirebaseFirestore database;
    private final MutableLiveData<Event> currentEventShown = new MutableLiveData<>();

    public DashboardViewModel(@NonNull FirebaseFirestore database) {
        this.database = database;
    }

    public void deleteEvent(@NonNull Event event) {
        database.collection(FirebaseConfig.EVENTS).document(event.getKey()).delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Event deleted");
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
            }
        });
    }

    public void unsubscribe(@NonNull Event event) {
        database.collection(FirebaseConfig.EVENTS).document(event.getKey()).update("players", FieldValue.arrayRemove(FirebaseAuth.getInstance().getUid())).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Event unsubscribed");
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
            }
        });
    }

    public @NonNull MutableLiveData<Event> getCurrentEventShown() {
        return currentEventShown;
    }

    public void updateCurrentEventShown(@NonNull Event event) {
        this.currentEventShown.setValue(event);
    }

}