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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

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

        final TextInputLayout usernameLayout = binding.email;
        final TextInputLayout passwordLayout = binding.password;
        final EditText username = usernameLayout.getEditText();
        final EditText password = passwordLayout.getEditText();
        final Button loginButton = binding.login;
        final FloatingActionButton signupButton = binding.gotoSignupButton;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getResult() == Result.FAILURE) {
                showLoginFailed(loginResult.getMessage());
            } else {
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_home));
            }
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(username.getText().toString(),
                    password.getText().toString());
        });

        signupButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_signup)));

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