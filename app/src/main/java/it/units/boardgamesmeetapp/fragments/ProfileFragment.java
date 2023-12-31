package it.units.boardgamesmeetapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import java.util.Objects;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentProfileBinding;
import it.units.boardgamesmeetapp.models.UserInfoView;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.profile.ProfileViewModel;
import it.units.boardgamesmeetapp.viewmodels.profile.ProfileViewModelFactory;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private EditText name;
    private EditText surname;
    private EditText age;
    private EditText favouritePlace;
    private EditText favouriteGame;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileViewModel viewModel = new ViewModelProvider(this, new ProfileViewModelFactory()).get(ProfileViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle(getString(R.string.profile));

        name = Objects.requireNonNull(binding.name.getEditText());
        surname = Objects.requireNonNull(binding.surname.getEditText());
        age = Objects.requireNonNull(binding.age.getEditText());
        favouritePlace = Objects.requireNonNull(binding.place.getEditText());
        favouriteGame = Objects.requireNonNull(binding.game.getEditText());

        viewModel.getUserParticipatedEvents().observe(getViewLifecycleOwner(), data -> binding.numberParticipatedEvents.setText(String.valueOf(data)));
        viewModel.getUserCreatedEvents().observe(getViewLifecycleOwner(), data -> binding.numberCreatedEvents.setText(String.valueOf(data)));

        viewModel.getInitialUserInfo().observe(getViewLifecycleOwner(), initialUserInfo -> {
            if (initialUserInfo == null) return;
            initTextFields(initialUserInfo);
        });


        viewModel.getCurrentUserInfo().observe(getViewLifecycleOwner(), currentUserInfo -> binding.modifyButton.setEnabled(!currentUserInfo.equals(viewModel.getInitialUserInfo().getValue())));

        binding.modifyButton.setOnClickListener(v -> {
            viewModel.modifyUserInformation();
            Stream.of(name, surname, age, favouriteGame, favouriteGame).forEach(field -> field.clearFocus());
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
                UserInfoView userInfo = new UserInfoView(name.getText().toString(), surname.getText().toString(), age.getText().toString(), favouritePlace.getText().toString(), favouriteGame.getText().toString());
                viewModel.updateCurrentInfoUser(userInfo);
            }
        };

        setAfterTextChangedListener(afterTextChangedListener);
    }

    private void setAfterTextChangedListener(TextWatcher afterTextChangedListener) {
        name.addTextChangedListener(afterTextChangedListener);
        surname.addTextChangedListener(afterTextChangedListener);
        age.addTextChangedListener(afterTextChangedListener);
        favouritePlace.addTextChangedListener(afterTextChangedListener);
        favouriteGame.addTextChangedListener(afterTextChangedListener);
    }

    private void initTextFields(@NonNull UserInfoView initialUserInfo) {
        name.setText(initialUserInfo.getName());
        surname.setText(initialUserInfo.getSurname());
        age.setText(String.valueOf(initialUserInfo.getAge()));
        favouritePlace.setText(initialUserInfo.getFavouritePlace());
        favouriteGame.setText(initialUserInfo.getFavouriteGame());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}