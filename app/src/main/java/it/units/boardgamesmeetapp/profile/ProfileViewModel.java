package it.units.boardgamesmeetapp.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModel extends ViewModel {
    @NonNull
    private final FirebaseFirestore database;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<UserInfo> initialUserInfo = new MutableLiveData<>();
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();

    public ProfileViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        DocumentReference reference = database.collection(FirebaseConfig.USERS).document(Objects.requireNonNull(firebaseAuth.getUid()));
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
    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        DocumentReference reference = database.collection(FirebaseConfig.USERS).document(Objects.requireNonNull(firebaseAuth.getUid()));
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
