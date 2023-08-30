package it.units.boardgamesmeetapp.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private UserInfo userInfo;

    public ProfileViewModelFactory(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance(FirebaseConfig.DB_URL), userInfo);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
