package it.units.boardgamesmeetapp.data;

import com.google.firebase.auth.FirebaseAuth;

import it.units.boardgamesmeetapp.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginRemoteDataSource {

    private FirebaseAuth firebaseAuth;

    public Result<LoggedInUser> login(String username, String password) {

        try {
            firebaseAuth = FirebaseAuth.getInstance();
            if(firebaseAuth.signInWithEmailAndPassword(username,password).isSuccessful()) {
                LoggedInUser currentUser = new LoggedInUser(firebaseAuth.getUid(), username);
                return new Result.Success<>(currentUser);
            }
            throw new IOException("Error logging in");
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<LoggedInUser> signup(String username, String password) {
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            if(firebaseAuth.createUserWithEmailAndPassword(username,password).isSuccessful()) {
                LoggedInUser currentUser = new LoggedInUser(firebaseAuth.getUid(), username);
                return new Result.Success<>(currentUser);
            }
            throw new IOException("Error signing up");
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}