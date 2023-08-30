package it.units.boardgamesmeetapp.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentSignupBinding;
import it.units.boardgamesmeetapp.utils.Result;

public class SignupFragment extends Fragment {

    private LoginViewModel loginViewModel;

    private FragmentSignupBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final TextInputLayout usernameLayout = binding.username;
        final TextInputLayout passwordLayout = binding.password;
        final EditText username = usernameLayout.getEditText();
        final EditText password = passwordLayout.getEditText();
        final Button signupButton = binding.signupButton;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            signupButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameLayout.setError(getString(loginFormState.getUsernameError()));
            } else {
                usernameLayout.setError(null);
            }
            if (loginFormState.getPasswordError() != null) {
                passwordLayout.setError(getString(loginFormState.getPasswordError()));
            } else {
                passwordLayout.setError(null);
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getResult() == Result.FAILURE) {
                showLoginResult(loginResult.getMessage());
            } else {
                showLoginResult(R.string.sign_up_success);
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_signup_to_navigation_profile));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(username.getText().toString(),
                        password.getText().toString());
            }
        };
        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(username.getText().toString(),
                        password.getText().toString());
            }
            return false;
        });

        signupButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.signup(username.getText().toString(),
                    password.getText().toString());
        });

        binding.backLogin.setOnClickListener(v-> {
            NavHostFragment.findNavController(this).navigateUp();
        });

    }

    private void showLoginResult(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}