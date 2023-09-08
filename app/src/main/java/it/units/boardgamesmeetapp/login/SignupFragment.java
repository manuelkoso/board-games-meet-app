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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

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

        final TextInputLayout usernameLayout = binding.email;
        final TextInputLayout passwordLayout = binding.password;
        final EditText username = usernameLayout.getEditText();
        final EditText password = passwordLayout.getEditText();

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            binding.signup.setEnabled(loginFormState.isDataValid());
            setFieldErrors(loginFormState);
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            binding.loading.setVisibility(View.GONE);
            showLoginResult(loginResult.getMessage());
            if (loginResult.getResult() == Result.FAILURE) {
                usernameLayout.setError(getString(loginResult.getMessage()));
            } else {
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
                loginViewModel.loginDataChanged(Objects.requireNonNull(username).getText().toString(),
                        Objects.requireNonNull(password).getText().toString());
            }
        };

        Objects.requireNonNull(username).addTextChangedListener(afterTextChangedListener);
        Objects.requireNonNull(password).addTextChangedListener(afterTextChangedListener);

        binding.signup.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            loginViewModel.signup(username.getText().toString(),
                    password.getText().toString());
        });

        binding.gotoSignupButton.setOnClickListener(v-> NavHostFragment.findNavController(this).navigateUp());

    }

    private void setFieldErrors(@NonNull LoginState loginFormState) {
        if (loginFormState.getUsernameError() != null) {
            binding.email.setError(getString(loginFormState.getUsernameError()));
        } else {
            binding.email.setError(null);
        }
        if (loginFormState.getPasswordError() != null) {
            binding.password.setError(getString(loginFormState.getPasswordError()));
        } else {
            binding.password.setError(null);
        }
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