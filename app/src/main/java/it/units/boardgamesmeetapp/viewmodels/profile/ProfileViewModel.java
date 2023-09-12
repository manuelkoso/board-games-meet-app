package it.units.boardgamesmeetapp.viewmodels.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModel extends ViewModel {
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<UserInfo> initialUserInfo = new MutableLiveData<>();
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> userCreatedEvents = new MutableLiveData<>();
    private final MutableLiveData<Integer> userParticipatedEvents = new MutableLiveData<>();

    public ProfileViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.userParticipatedEvents.setValue(0);
        this.userCreatedEvents.setValue(0);
        DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS).document(Objects.requireNonNull(firebaseAuth.getUid()));
        reference.addSnapshotListener(
                (value, exception) -> {
                    if (exception != null) {
                        Log.w(FirebaseConfig.TAG, exception);
                        return;
                    }
                    if (value != null && value.exists()) {
                        Log.d(FirebaseConfig.TAG, "Data: " + value.getData());
                        User user = value.toObject(User.class);
                        initialUserInfo.setValue(Objects.requireNonNull(user).getInfo());
                    } else {
                        Log.w(FirebaseConfig.TAG, "Data: null");
                    }
                }
        );
        firebaseFirestore.collection(FirebaseConfig.EVENTS).whereArrayContains("players", Objects.requireNonNull(firebaseAuth.getUid())).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                int lol = task.getResult().size();
                userParticipatedEvents.setValue(task.getResult().size());
            } else {
                userParticipatedEvents.setValue(0);
            }
        });
        firebaseFirestore.collection(FirebaseConfig.EVENTS).whereEqualTo("ownerId", firebaseAuth.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                userCreatedEvents.setValue(task.getResult().size());
            } else {
                userCreatedEvents.setValue(0);
            }
        });
    }

    public MutableLiveData<Integer> getUserCreatedEvents() {
        return userCreatedEvents;
    }

    public MutableLiveData<Integer> getUserParticipatedEvents() {
        return userParticipatedEvents;
    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS).document(Objects.requireNonNull(firebaseAuth.getUid()));
        reference.update("info", Objects.requireNonNull(currentUserInfo.getValue()).toMap()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "User information modified correctly");
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
            }
        });
    }

    public void updateCurrentInfoUser(UserInfo userInfo) {
        this.currentUserInfo.setValue(userInfo);
    }

    public MutableLiveData<UserInfo> getInitialUserInfo() {
        return initialUserInfo;
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}
