package it.units.boardgamesmeetapp.viewmodels.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.models.UserInfoView;

public class ProfileViewModel extends ViewModel {
    public static final int INITIAL_COUNTER_VALUE = 0;
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<UserInfoView> initialUserInfo = new MutableLiveData<>();
    private final MutableLiveData<UserInfoView> currentUserInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> userCreatedEvents = new MutableLiveData<>();
    private final MutableLiveData<Integer> userParticipatedEvents = new MutableLiveData<>();

    public ProfileViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        this.userParticipatedEvents.setValue(0);
        this.userCreatedEvents.setValue(0);
        DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS_REFERENCE).document(Objects.requireNonNull(firebaseAuth.getUid()));
        reference.addSnapshotListener(
                (value, exception) -> {
                    if (exception != null) {
                        Log.w(FirebaseConfig.TAG, exception);
                        return;
                    }
                    if (value != null && value.exists()) {
                        Log.d(FirebaseConfig.TAG, "Data: " + value.getData());
                        User user = value.toObject(User.class);
                        initialUserInfo.setValue(new UserInfoView(user));
                    } else {
                        Log.w(FirebaseConfig.TAG, "Data: null");
                    }
                }
        );
        firebaseFirestore.collection(FirebaseConfig.EVENTS_REFERENCE).where(Filter.notEqualTo(FirebaseConfig.OWNER_ID_REFERENCE, firebaseAuth.getUid())).whereArrayContains(FirebaseConfig.PLAYERS_REFERENCE, Objects.requireNonNull(firebaseAuth.getUid())).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userParticipatedEvents.setValue(task.getResult().size());
            } else {
                userParticipatedEvents.setValue(INITIAL_COUNTER_VALUE);
            }
        });
        firebaseFirestore.collection(FirebaseConfig.EVENTS_REFERENCE).whereEqualTo(FirebaseConfig.OWNER_ID_REFERENCE, firebaseAuth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userCreatedEvents.setValue(task.getResult().size());
            } else {
                userCreatedEvents.setValue(INITIAL_COUNTER_VALUE);
            }
        });
    }

    public MutableLiveData<Integer> getUserCreatedEvents() {
        return userCreatedEvents;
    }

    public MutableLiveData<Integer> getUserParticipatedEvents() {
        return userParticipatedEvents;
    }

    public MutableLiveData<UserInfoView> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        String name = Objects.requireNonNull(currentUserInfo.getValue()).getName();
        String surname = currentUserInfo.getValue().getSurname();
        int age = getAgeFromString(currentUserInfo.getValue().getAge());
        String place = currentUserInfo.getValue().getFavouritePlace();
        String game = currentUserInfo.getValue().getFavouriteGame();
        User user = new User(firebaseAuth.getUid(), name, surname, age, place, game);
        firebaseFirestore.collection(FirebaseConfig.USERS_REFERENCE).document(user.getId()).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, FirebaseConfig.DATA_MODIFIED_SUCCESS);
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
            }
        });
    }

    private static int getAgeFromString(@Nullable String inputString) {
        if (inputString == null || inputString.isEmpty())
            return 0;
        return Integer.parseInt(inputString);
    }

    public void updateCurrentInfoUser(UserInfoView userInfo) {
        this.currentUserInfo.setValue(userInfo);
    }

    public MutableLiveData<UserInfoView> getInitialUserInfo() {
        return initialUserInfo;
    }

}
