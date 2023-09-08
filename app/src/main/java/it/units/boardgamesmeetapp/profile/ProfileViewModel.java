package it.units.boardgamesmeetapp.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModel extends ViewModel {

    private final FirebaseFirestore database;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<UserInfo> initialUserInfo = new MutableLiveData<>();
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();

    public ProfileViewModel(FirebaseAuth firebaseAuth, FirebaseFirestore database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DocumentReference reference = database.collection("users").document(firebaseUser.getUid());
        reference.addSnapshotListener(
                (value, exception) -> {
                    User user = value.toObject(User.class);
                    initialUserInfo.setValue(user.getInfo());
                }
        );
    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DocumentReference reference = database.collection("users").document(firebaseUser.getUid());
        reference.update("info", currentUserInfo.getValue().toMap());
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
