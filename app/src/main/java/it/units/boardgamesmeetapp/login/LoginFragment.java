package it.units.boardgamesmeetapp.login;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.FragmentLoginBinding;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.utils.Result;

public class LoginFragment extends Fragment {
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

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            binding.loading.setVisibility(View.GONE);
            if (loginResult.getResult() == Result.FAILURE) {
                setFieldsErrors(loginResult);
                showLoginFailed(loginResult.getMessage());
            } else {
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_home));
            }
        });

        binding.login.setOnClickListener(v -> {
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

    }

    private void setFieldsErrors(LoginResult loginResult) {
        if (loginResult.getMessage() == R.string.email_empty) {
            binding.email.setError(getResources().getString(loginResult.getMessage()));
        } else if (loginResult.getMessage() == R.string.password_empty) {
            binding.password.setError(getResources().getString(loginResult.getMessage()));
        } else {
            binding.email.setError(getResources().getString(loginResult.getMessage()));
            binding.password.setError(getResources().getString(loginResult.getMessage()));
        }
    }

    private void removeFieldErrors() {
        binding.email.setError(null);
        binding.password.setError(null);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
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