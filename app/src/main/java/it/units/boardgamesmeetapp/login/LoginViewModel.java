package it.units.boardgamesmeetapp.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.utils.Result;

public class LoginViewModel extends ViewModel {
    private static final String LOGIN_FAILED_MESSAGE = "Login failed!";
    private static final String LOGIN_SUCCESS_MESSAGE = "Login success!";
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    private final MutableLiveData<LoginState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
    }

    public @NonNull LiveData<LoginState> getLoginFormState() {
        return loginFormState;
    }

    public @NonNull LiveData<LoginResult> getLoginResult() {
        if (isAlreadyLoggedIn()) {
            updateLoginResult();
        }
        return loginResult;
    }

    private void updateLoginResult() {
        if (loginResult.getValue() == null)
            loginResult.setValue(new LoginResult(Result.SUCCESS));
    }

    private boolean isAlreadyLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void login(@NonNull String username, @NonNull String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Log.w(FirebaseConfig.TAG, LOGIN_FAILED_MESSAGE);
            loginResult.setValue(new LoginResult(Result.FAILURE, R.string.login_failed));
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, LOGIN_SUCCESS_MESSAGE);
                loginResult.setValue(new LoginResult(Result.SUCCESS));
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
                loginResult.setValue(new LoginResult(Result.FAILURE, R.string.login_failed));
            }
        });
    }

    public void loginDataChanged(@NonNull String username, @NonNull String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginState(true));
        }
    }

    public void signup(@NonNull String username, @NonNull String password) {
        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, LOGIN_SUCCESS_MESSAGE);
                loginResult.setValue(new LoginResult(Result.SUCCESS));
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                User user = new User(Objects.requireNonNull(firebaseUser).getUid());
                DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS).document(user.getId());
                reference.set(user);
            } else {
                Log.d(FirebaseConfig.TAG, "Signup failed");
                loginResult.setValue(new LoginResult(Result.FAILURE, R.string.signup_failed));
            }
        });
    }

    private boolean isUserNameValid(@Nullable String username) {
        if (username == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    private boolean isPasswordValid(@Nullable String password) {
        return password != null && password.trim().length() > 5;
    }
}