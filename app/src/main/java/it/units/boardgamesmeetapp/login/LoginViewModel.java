package it.units.boardgamesmeetapp.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.R;

public class LoginViewModel extends ViewModel {

    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<LoginState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    LiveData<LoginState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        if (isAlreadyLoggedIn()) {
            updateLoginResult();
        }
        return loginResult;
    }

    private void updateLoginResult() {
        if (loginResult.getValue() == null)
            loginResult.setValue(new LoginResult(new LoggedInUserView(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName())));
    }

    private boolean isAlreadyLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void login(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
            Log.d(FirebaseConfig.TAG, "Login error");
            loginResult.setValue(new LoginResult(R.string.login_failed));
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Successful login");
                loginResult.setValue(new LoginResult(new LoggedInUserView(username)));
            } else {
                Log.d(FirebaseConfig.TAG, "Login error");
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginState(true));
        }
    }

    public void signup(String username, String password) {
        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, "Successful login");
                loginResult.setValue(new LoginResult(new LoggedInUserView(username)));
            } else {
                Log.d(FirebaseConfig.TAG, "Login error");
                loginResult.setValue(new LoginResult(R.string.signup_failed));
            }
        });
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}