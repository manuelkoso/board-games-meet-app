package it.units.boardgamesmeetapp.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final FirebaseAuth firebaseAuth;
    private final UserInfo initialUserInfo;
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();

    public ProfileViewModel(FirebaseAuth firebaseAuth, FirebaseDatabase database, UserInfo userInfo) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        this.initialUserInfo = userInfo;
        this.currentUserInfo.setValue(userInfo);
    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = database.getReference("users");
        // databaseReference.child(firebaseUser.getUid()).updateChildren();
    }

}
