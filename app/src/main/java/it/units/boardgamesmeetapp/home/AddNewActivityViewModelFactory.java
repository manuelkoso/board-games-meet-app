package it.units.boardgamesmeetapp.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.login.LoginViewModel;

public class AddNewActivityViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddNewActivityViewModel.class)) {
            return (T) new AddNewActivityViewModel(FirebaseDatabase.getInstance(FirebaseConfig.DB_URL));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}