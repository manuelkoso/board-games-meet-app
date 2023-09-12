package it.units.boardgamesmeetapp.viewmodels.newevent;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.viewmodels.newevent.NewEventViewModel;

public class NewEventViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewEventViewModel.class)) {
            return (T) new NewEventViewModel(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
