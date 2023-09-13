package it.units.boardgamesmeetapp.fragments;

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

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentSignupBinding;
import it.units.boardgamesmeetapp.utils.Result;
import it.units.boardgamesmeetapp.viewmodels.login.LoginState;
import it.units.boardgamesmeetapp.viewmodels.login.LoginViewModel;
import it.units.boardgamesmeetapp.viewmodels.login.LoginViewModelFactory;

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

        if (savedInstanceState != null) {
            binding.email.getEditText().setText(savedInstanceState.getString("EMAIL"));
            binding.password.getEditText().setText(savedInstanceState.getString("PASSWORD"));
        }


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
            if (loginResult.getResult() == Result.FAILURE) {
                binding.email.setError(getString(loginResult.getMessage()));
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
                loginViewModel.loginDataChanged(Objects.requireNonNull(binding.email.getEditText()).getText().toString(),
                        Objects.requireNonNull(binding.password.getEditText()).getText().toString());
            }
        };

        Objects.requireNonNull(binding.email.getEditText()).addTextChangedListener(afterTextChangedListener);
        Objects.requireNonNull(binding.password.getEditText()).addTextChangedListener(afterTextChangedListener);

        binding.signup.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            loginViewModel.signup(binding.email.getEditText().getText().toString(),
                    binding.password.getEditText().getText().toString());
        });

        binding.gotoSignupButton.setOnClickListener(v-> NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_global_loginFragment)));
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("EMAIL", binding.email.getEditText().getText().toString());
        outState.putString("PASSWORD", binding.password.getEditText().getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}