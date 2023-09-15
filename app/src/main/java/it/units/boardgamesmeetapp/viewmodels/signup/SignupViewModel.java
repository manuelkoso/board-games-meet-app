package it.units.boardgamesmeetapp.viewmodels.signup;

import static it.units.boardgamesmeetapp.viewmodels.Result.FAILURE;
import static it.units.boardgamesmeetapp.viewmodels.Result.NETWORK_FAILURE;
import static it.units.boardgamesmeetapp.viewmodels.Result.SUCCESS;

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

public class SignupViewModel extends ViewModel {
    private static final String SIGNUP_SUCCESS_MESSAGE = "Signup success!";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    @NonNull
    private final FirebaseAuth firebaseAuth;
    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    private final MutableLiveData<SignupFormState> signupFormState = new MutableLiveData<>();
    private final MutableLiveData<SubmissionResult> signupResult = new MutableLiveData<>();

    public SignupViewModel(@NonNull FirebaseAuth firebaseAuth, @NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
    }

    public @NonNull LiveData<SignupFormState> getSignupFormState() {
        return signupFormState;
    }

    public @NonNull LiveData<SubmissionResult> getSignupResult() {
        return signupResult;
    }

    public void signupDataChanged(@NonNull String email, @NonNull String password) {
        if (!isEmailNameValid(email)) {
            signupFormState.setValue(new SignupFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            signupFormState.setValue(new SignupFormState(null, R.string.invalid_password));
        } else {
            signupFormState.setValue(new SignupFormState(true));
        }
    }

    public void signup(@NonNull String email, @NonNull String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(FirebaseConfig.TAG, SIGNUP_SUCCESS_MESSAGE);
                signupResult.setValue(new SubmissionResult(SUCCESS, R.string.signup_success));
                User user = new User(Objects.requireNonNull(firebaseAuth.getUid()));
                DocumentReference reference = firebaseFirestore.collection(FirebaseConfig.USERS_REFERENCE).document(user.getId());
                reference.set(user);
            } else {
                if(task.getException() instanceof FirebaseNetworkException) {
                    signupResult.setValue(new SubmissionResult(NETWORK_FAILURE, R.string.network_failure));
                } else {
                    signupResult.setValue(new SubmissionResult(FAILURE, R.string.signup_failed));
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
