package it.units.boardgamesmeetapp.dashboard;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.models.Event;

public class DashboardViewModel extends ViewModel {

    private final FirebaseFirestore database;

    public DashboardViewModel(FirebaseFirestore database) {
        this.database = database;
    }

    public void deleteEvent(Event event) {
        database.collection("activities").document(event.getKey()).delete();
    }

}