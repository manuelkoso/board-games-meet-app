package it.units.boardgamesmeetapp.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.FragmentLoginBinding;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.viewmodels.Result;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModelFactory;
import it.units.boardgamesmeetapp.viewmodels.SubmissionResult;
import it.units.boardgamesmeetapp.viewmodels.login.LoginViewModel;
import it.units.boardgamesmeetapp.viewmodels.login.LoginViewModelFactory;

public class LoginFragment extends Fragment {
    public static final String EMAIL_KEY = "EMAIL";
    public static final String PASSWORD_KEY = "PASSWORD";
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginViewModel loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle(getResources().getString(R.string.board_games));

        if (savedInstanceState != null) {
            Objects.requireNonNull(binding.email.getEditText()).setText(savedInstanceState.getString(EMAIL_KEY));
            Objects.requireNonNull(binding.password.getEditText()).setText(savedInstanceState.getString(PASSWORD_KEY));
        }

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            binding.loading.setVisibility(View.GONE);
            if (loginResult.getResult() == Result.SUCCESS) {
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_home));
            } else {
                setFieldsErrors(loginResult);
            }
        });

        binding.loginButton.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            removeFieldErrors();
            EditText email = binding.email.getEditText();
            EditText password = binding.password.getEditText();
            loginViewModel.login(Objects.requireNonNull(email).getText().toString(),
                    Objects.requireNonNull(password).getText().toString());
        });

        binding.gotoSignupButton.setOnClickListener(v -> {
            loginViewModel.resetLoginResult();
            removeFieldErrors();
            NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_signup));
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // ignored
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // ignored
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.email.setErrorEnabled(false);
                binding.password.setErrorEnabled(false);
                binding.email.setError(null);
                binding.password.setError(null);
            }
        };

        Objects.requireNonNull(binding.email.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(binding.password.getEditText()).addTextChangedListener(textWatcher);

    }

    private void setFieldsErrors(SubmissionResult loginResult) {
        if (loginResult.getMessage() == R.string.email_empty) {
            binding.email.setErrorEnabled(true);
            binding.email.setError(getResources().getString(loginResult.getMessage()));
        } else if (loginResult.getMessage() == R.string.password_empty) {
            binding.password.setErrorEnabled(true);
            binding.password.setError(getResources().getString(loginResult.getMessage()));
        } else {
            binding.email.setErrorEnabled(true);
            binding.password.setErrorEnabled(true);
            binding.email.setError(getResources().getString(loginResult.getMessage()));
            binding.password.setError(getResources().getString(loginResult.getMessage()));
        }
    }

    private void removeFieldErrors() {
        binding.email.setErrorEnabled(false);
        binding.password.setErrorEnabled(false);
        binding.email.setError(null);
        binding.password.setError(null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(EMAIL_KEY, Objects.requireNonNull(binding.email.getEditText()).getText().toString());
        outState.putString(PASSWORD_KEY, Objects.requireNonNull(binding.password.getEditText()).getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}