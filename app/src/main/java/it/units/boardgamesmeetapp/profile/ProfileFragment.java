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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DialogProfileInformationBinding;
import it.units.boardgamesmeetapp.databinding.FragmentProfileBinding;
import it.units.boardgamesmeetapp.models.UserInfo;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory()).get(ProfileViewModel.class);

        binding.informationButton.setOnClickListener(v -> setupDialog());
        binding.logout.setOnClickListener(v -> {
                    viewModel.logout();
                    NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_global_loginFragment));
                }
        );

    }

    public void setupDialog() {
        DialogProfileInformationBinding informationBinding = DialogProfileInformationBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext()).create();

        final EditText name = informationBinding.name.getEditText();
        final EditText surname = informationBinding.surname.getEditText();
        final EditText ageEditText = informationBinding.age.getEditText();
        final EditText favouritePlace = informationBinding.place.getEditText();
        final EditText favouriteGame = informationBinding.game.getEditText();
        final Button button = informationBinding.modifyButton;

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

        informationBinding.backButton.setOnClickListener(v -> dialog.hide());

        name.addTextChangedListener(afterTextChangedListener);
        surname.addTextChangedListener(afterTextChangedListener);
        ageEditText.addTextChangedListener(afterTextChangedListener);
        favouritePlace.addTextChangedListener(afterTextChangedListener);
        favouriteGame.addTextChangedListener(afterTextChangedListener);
        dialog.setView(informationBinding.getRoot());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}