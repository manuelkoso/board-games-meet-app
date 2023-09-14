package it.units.boardgamesmeetapp.viewmodels.login;

import static it.units.boardgamesmeetapp.utils.Result.FAILURE;
import static it.units.boardgamesmeetapp.utils.Result.NETWORK_FAILURE;
import static it.units.boardgamesmeetapp.utils.Result.SUCCESS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.models.User;
import it.units.boardgamesmeetapp.viewmodels.SubmissionResult;

public class LoginViewModel extends ViewModel {
    private static final String LOGIN_FAILED_MESSAGE = "Login failed!";
    private static final String LOGIN_SUCCESS_MESSAGE = "Login success!";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    private final MutableLiveData<LoginState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<SubmissionResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
    }

    public @NonNull LiveData<LoginState> getLoginFormState() {
        return loginFormState;
    }

    public @NonNull LiveData<SubmissionResult> getLoginResult() {
        if (isAlreadyLoggedIn()) {
            updateLoginResult();
        }
        return loginResult;
    }

    private void updateLoginResult() {
        if (loginResult.getValue() == null)
            loginResult.setValue(new SubmissionResult(SUCCESS));
    }

    private boolean isAlreadyLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void resetLoginResult() {
        loginResult.setValue(null);
    }

    public void login(@NonNull String email, @NonNull String password) {
        if (isInputEmpty(email, password)) return;
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, LOGIN_SUCCESS_MESSAGE);
                loginResult.setValue(new SubmissionResult(SUCCESS));
            } else {
                Log.w(FirebaseConfig.TAG, task.getException());
                loginResult.setValue(new SubmissionResult(FAILURE, R.string.login_failed));
            }
        });
    }

    private boolean isInputEmpty(@NonNull String email, @NonNull String password) {
        if (email.isEmpty()) {
            Log.w(FirebaseConfig.TAG, LOGIN_FAILED_MESSAGE);
            loginResult.setValue(new SubmissionResult(FAILURE, R.string.email_empty));
            return true;
        } else if (password.isEmpty()) {
            Log.w(FirebaseConfig.TAG, LOGIN_FAILED_MESSAGE);
            loginResult.setValue(new SubmissionResult(FAILURE, R.string.password_empty));
            return true;
        }
        return false;
    }

    public void loginDataChanged(@NonNull String email, @NonNull String password) {
        if (!isEmailNameValid(email)) {
            loginFormState.setValue(new LoginState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginState(true));
        }
    }

    public void signup(@NonNull String email, @NonNull String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, LOGIN_SUCCESS_MESSAGE);
                loginResult.setValue(new SubmissionResult(SUCCESS, R.string.signup_success));
                User user = new User(Objects.requireNonNull(firebaseAuth.getUid()));
                DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS).document(user.getId());
                reference.set(user);
            } else {
                if(task.getException() instanceof FirebaseNetworkException) {
                    loginResult.setValue(new SubmissionResult(NETWORK_FAILURE, R.string.network_failure));
                } else {
                    loginResult.setValue(new SubmissionResult(FAILURE, R.string.signup_failed));
                }
                Log.w(FirebaseConfig.TAG, task.getException());

            }
        });
    }

    private boolean isEmailNameValid(@Nullable String email) {
        if (email == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(@Nullable String password) {
        return password != null && password.trim().length() >= MINIMUM_PASSWORD_LENGTH;
    }
}