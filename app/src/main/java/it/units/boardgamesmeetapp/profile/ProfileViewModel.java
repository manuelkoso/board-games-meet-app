package it.units.boardgamesmeetapp.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<UserInfo> initialUserInfo = new MutableLiveData<>();
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();

    public ProfileViewModel(FirebaseAuth firebaseAuth, FirebaseDatabase database) {
        this.database = database;
        this.firebaseAuth = firebaseAuth;
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = database.getReference("users").child(firebaseUser.getUid());
        ValueEventListener initialUserInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo userInfo = snapshot.getValue(UserInfo.class);
                initialUserInfo.setValue(userInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(initialUserInfoListener);
    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    public void modifyUserInformation() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = database.getReference("users");
        databaseReference.child(firebaseUser.getUid()).updateChildren(currentUserInfo.getValue().toMap());
    }
    public void updateCurrentInfoUser(UserInfo userInfo) {
        this.currentUserInfo.setValue(userInfo);
    }

    public MutableLiveData<UserInfo> getInitialUserInfo() {
        return initialUserInfo;
    }
}
