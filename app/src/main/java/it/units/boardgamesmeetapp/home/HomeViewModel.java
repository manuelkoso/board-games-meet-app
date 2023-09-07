package it.units.boardgamesmeetapp.home;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.boardgamesmeetapp.models.Event;

public class HomeViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseDatabase;

    public HomeViewModel(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        this.firebaseAuth = firebaseAuth;
    }

    public void submit(Event event) {
        event.addPlayer(firebaseAuth.getUid());
        firebaseDatabase.collection("activities").document(event.getKey()).update(event.toMap());
    }


}