package it.units.boardgamesmeetapp.viewmodels.login;

import static it.units.boardgamesmeetapp.viewmodels.Result.FAILURE;
import static it.units.boardgamesmeetapp.viewmodels.Result.NETWORK_FAILURE;
import static it.units.boardgamesmeetapp.viewmodels.Result.SUCCESS;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.viewmodels.SubmissionResult;

public class LoginViewModel extends ViewModel {
    private static final String LOGIN_FAILED_MESSAGE = "Login failed!";
    private static final String LOGIN_SUCCESS_MESSAGE = "Login success!";
    @NonNull
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<SubmissionResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public @NonNull LiveData<SubmissionResult> getLoginResult() {
        if(isUserAlreadyLoggedIn()) {
            loginResult.setValue(new SubmissionResult(SUCCESS));
        }
        return loginResult;
    }

    private boolean isUserAlreadyLoggedIn() {
        return firebaseAuth.getUid() != null;
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
                if(task.getException() instanceof FirebaseNetworkException) {
                    loginResult.setValue(new SubmissionResult(NETWORK_FAILURE, R.string.network_failure));
                } else {
                    loginResult.setValue(new SubmissionResult(FAILURE, R.string.login_failed));
                }
                Log.w(FirebaseConfig.TAG, task.getException());
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

}