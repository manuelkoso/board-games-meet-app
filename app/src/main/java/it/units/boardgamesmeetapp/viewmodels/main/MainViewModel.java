package it.units.boardgamesmeetapp.viewmodels.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> actionBarTitle = new MutableLiveData<>();

    public MainViewModel() {
        actionBarTitle.setValue(null);
    }

    public MutableLiveData<String> getActionBarTitle() {
        return actionBarTitle;
    }

    public void updateActionBarTitle(@NonNull String title) {
        actionBarTitle.setValue(title);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

}
