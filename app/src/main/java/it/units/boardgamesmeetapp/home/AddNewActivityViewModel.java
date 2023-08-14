package it.units.boardgamesmeetapp.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.units.boardgamesmeetapp.models.Activity;

public class AddNewActivityViewModel extends ViewModel {

    private final FirebaseDatabase database;

    AddNewActivityViewModel(FirebaseDatabase database) {
        this.database = database;
    }

    public void addNewActivity() {
        DatabaseReference databaseReference = database.getReference();
        databaseReference.setValue("hello").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("MIAO", "Data successfully written.");
            } else {
                Log.d("MIAO", task.getException().getMessage());
            }
        });
    }


}