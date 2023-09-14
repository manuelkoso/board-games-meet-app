package it.units.boardgamesmeetapp.viewmodels.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import it.units.boardgamesmeetapp.R;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> actionBarTitle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enableActionBarBackButton = new MutableLiveData<>();

    public MainViewModel() {
        actionBarTitle.setValue(null);
    }

    public MutableLiveData<String> getActionBarTitle() {
        return actionBarTitle;
    }

    public MutableLiveData<Boolean> getEnableActionBarBackButton() {
        return enableActionBarBackButton;
    }

    public void updateActionBarTitle(@NonNull String title) {
        actionBarTitle.setValue(title);
    }

    public void updateActionBarBackButtonState(boolean isEnabled) {
        enableActionBarBackButton.setValue(isEnabled);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

}
