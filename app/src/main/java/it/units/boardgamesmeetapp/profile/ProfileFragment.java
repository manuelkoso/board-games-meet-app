package it.units.boardgamesmeetapp.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentProfileBinding;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText name = binding.name.getEditText();
        final EditText surname = binding.surname.getEditText();
        final EditText ageEditText = binding.age.getEditText();
        final EditText favouritePlace = binding.place.getEditText();
        final EditText favouriteGame = binding.game.getEditText();
        final Button button = binding.modifyButton;

        ProfileViewModel viewModel = new ViewModelProvider(this, new ProfileViewModelFactory()).get(ProfileViewModel.class);

        viewModel.getInitialUserInfo().observe(getViewLifecycleOwner(), initialUserInfo -> {
            if(initialUserInfo == null) return;
            name.setText(initialUserInfo.getName());
            surname.setText(initialUserInfo.getSurname());
            ageEditText.setText(String.valueOf(initialUserInfo.getAge()));
            favouritePlace.setText(initialUserInfo.getFavouritePlace());
            favouriteGame.setText(initialUserInfo.getFavouriteGame());
        });

        viewModel.getCurrentUserInfo().observe(getViewLifecycleOwner(), currentUserInfo -> button.setEnabled(!currentUserInfo.equals(viewModel.getInitialUserInfo().getValue())));

        button.setOnClickListener(v -> {
            viewModel.modifyUserInformation();
            name.clearFocus();
            surname.clearFocus();
            ageEditText.clearFocus();
            favouritePlace.clearFocus();
            favouriteGame.clearFocus();
        });

        binding.logout.setOnClickListener(v -> {
                    viewModel.logout();
                    NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_global_loginFragment));
                }
        );
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
                int age;

                try {
                    age = Integer.parseInt(ageEditText.getText().toString());
                } catch (NumberFormatException exception) {
                    age = 0;
                }
                UserInfo userInfo = new UserInfo(name.getText().toString(), surname.getText().toString(), age, favouritePlace.getText().toString(), favouriteGame.getText().toString());
                viewModel.updateCurrentInfoUser(userInfo);
            }
        };

        name.addTextChangedListener(afterTextChangedListener);
        surname.addTextChangedListener(afterTextChangedListener);
        ageEditText.addTextChangedListener(afterTextChangedListener);
        favouritePlace.addTextChangedListener(afterTextChangedListener);
        favouriteGame.addTextChangedListener(afterTextChangedListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}