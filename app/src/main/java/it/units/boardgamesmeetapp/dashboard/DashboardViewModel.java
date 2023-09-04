package it.units.boardgamesmeetapp.dashboard;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.models.Event;

public class DashboardViewModel extends ViewModel {

    private final FirebaseDatabase database;

    public DashboardViewModel(FirebaseDatabase database) {
        this.database = database;
    }

    public void deleteEvent(Event event) {
        DatabaseReference databaseReference = database.getReference("activities").child(event.getKey());
        databaseReference.removeValue();
    }

}