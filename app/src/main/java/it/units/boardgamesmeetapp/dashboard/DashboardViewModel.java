package it.units.boardgamesmeetapp.dashboard;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.FirebaseDatabase;

public class DashboardViewModel extends ViewModel {

    private final FirebaseDatabase database;

    public DashboardViewModel(FirebaseDatabase database) {
        this.database = database;
    }

}