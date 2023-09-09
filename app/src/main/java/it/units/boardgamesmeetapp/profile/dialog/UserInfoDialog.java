package it.units.boardgamesmeetapp.profile.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.DialogProfileInformationBinding;
import it.units.boardgamesmeetapp.models.UserInfo;
import it.units.boardgamesmeetapp.profile.ProfileViewModel;
import it.units.boardgamesmeetapp.profile.ProfileViewModelFactory;

public class UserInfoDialog {

    @NonNull
    private final AlertDialog dialog;
    @NonNull
    final EditText name;
    @NonNull
    final EditText surname;
    @NonNull
    final EditText age;
    @NonNull
    final EditText favouritePlace;
    @NonNull
    final EditText favouriteGame;

    private UserInfoDialog(@NonNull Fragment fragment) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();

        DialogProfileInformationBinding binding = DialogProfileInformationBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        ProfileViewModel viewModel = new ViewModelProvider(fragment.getViewModelStore(), new ProfileViewModelFactory()).get(ProfileViewModel.class);

        name = Objects.requireNonNull(binding.name.getEditText());
        surname = Objects.requireNonNull(binding.surname.getEditText());
        age = Objects.requireNonNull(binding.age.getEditText());
        favouritePlace = Objects.requireNonNull(binding.place.getEditText());
        favouriteGame = Objects.requireNonNull(binding.game.getEditText());

        viewModel.getInitialUserInfo().observe(fragment.getViewLifecycleOwner(), initialUserInfo -> {
            if (initialUserInfo == null) return;
            setTextFieldWith(initialUserInfo);
        });
        viewModel.getCurrentUserInfo().observe(fragment.getViewLifecycleOwner(), currentUserInfo -> binding.modifyButton.setEnabled(!currentUserInfo.equals(viewModel.getInitialUserInfo().getValue())));

        binding.modifyButton.setOnClickListener(v -> {
            viewModel.modifyUserInformation();
            name.clearFocus();
            surname.clearFocus();
            age.clearFocus();
            favouritePlace.clearFocus();
            favouriteGame.clearFocus();
        });

        binding.backButton.setOnClickListener(v -> dialog.hide());

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
                UserInfo userInfo = new UserInfo(name.getText().toString(), surname.getText().toString(), getAgeFromString(age.getText().toString()), favouritePlace.getText().toString(), favouriteGame.getText().toString());
                viewModel.updateCurrentInfoUser(userInfo);
            }
        };

        setAfterTextChangedListener(afterTextChangedListener);

        dialog.setView(binding.getRoot());
    }

    private void setAfterTextChangedListener(TextWatcher afterTextChangedListener) {
        name.addTextChangedListener(afterTextChangedListener);
        surname.addTextChangedListener(afterTextChangedListener);
        age.addTextChangedListener(afterTextChangedListener);
        favouritePlace.addTextChangedListener(afterTextChangedListener);
        favouriteGame.addTextChangedListener(afterTextChangedListener);
    }

    private void setTextFieldWith(@NonNull UserInfo initialUserInfo) {
        name.setText(initialUserInfo.getName());
        surname.setText(initialUserInfo.getSurname());
        age.setText(String.valueOf(initialUserInfo.getAge()));
        favouritePlace.setText(initialUserInfo.getFavouritePlace());
        favouriteGame.setText(initialUserInfo.getFavouriteGame());
    }

    private int getAgeFromString(String stringAge) {
        try {
            return Integer.parseInt(stringAge);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment) {
        UserInfoDialog userInfoDialog = new UserInfoDialog(fragment);
        return userInfoDialog.dialog;
    }


}
