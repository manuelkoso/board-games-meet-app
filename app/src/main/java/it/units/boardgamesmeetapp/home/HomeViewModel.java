package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.Event;

public class HomeViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final MutableLiveData<Integer> radioButtonIdMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> currentEventShown = new MutableLiveData<>();

    public HomeViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.radioButtonIdMutableLiveData.setValue(R.id.radio_button_game);
    }

    public void submit(@NonNull Event event) {
        event.addPlayer(firebaseAuth.getUid());
        firebaseFirestore.collection(FirebaseConfig.EVENTS).document(event.getKey()).update(event.toMap()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(FirebaseConfig.TAG, "Event submission done");
                    } else {
                        Log.w(FirebaseConfig.TAG, task.getException());
                    }
                }
        );
    }

    public MutableLiveData<Integer> getRadioButtonIdMutableLiveData() {
        return radioButtonIdMutableLiveData;
    }

    public void updateFilterField(@NonNull Integer radioButtonId) {
        this.radioButtonIdMutableLiveData.setValue(radioButtonId);
    }

    public @NonNull MutableLiveData<Event> getCurrentEventShown() {
        return currentEventShown;
    }

    public void updateCurrentEventShown(@NonNull Event event) {
        this.currentEventShown.setValue(event);
    }

}