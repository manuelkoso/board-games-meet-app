package it.units.boardgamesmeetapp.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.units.boardgamesmeetapp.models.Activity;

public class DashboardViewModel extends ViewModel {

    private final FirebaseDatabase database;
    private final MutableLiveData<Activity> firstActivity;

    private final ValueEventListener firstActivityListener;

    public DashboardViewModel(FirebaseDatabase database) {
        this.database = database;
        firstActivity = new MutableLiveData<>();
        firstActivityListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public MutableLiveData<Activity> getFirstActivity() {
        return firstActivity;
    }

}