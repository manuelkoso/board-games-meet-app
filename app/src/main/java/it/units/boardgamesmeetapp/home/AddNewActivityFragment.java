package it.units.boardgamesmeetapp.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.LocalTime;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentAddNewActivityBinding;
import it.units.boardgamesmeetapp.models.Activity;

public class AddNewActivityFragment extends Fragment {

    private AddNewActivityViewModel viewModel;
    private FragmentAddNewActivityBinding binding;

    public static AddNewActivityFragment newInstance() {
        return new AddNewActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new AddNewActivityViewModelFactory()).get(AddNewActivityViewModel.class);

        final Button submitButton = binding.button;
        final EditText game = binding.game.getEditText();
        final EditText date = binding.date.getEditText();
        final EditText time = binding.time.getEditText();
        final EditText place = binding.place.getEditText();
        final EditText numberOfPlayers = binding.numberOfPlayers.getEditText();

        submitButton.setOnClickListener(v -> viewModel.addNewActivity(game.getText().toString(), date.getText().toString(), time.getText().toString(), Integer.parseInt(numberOfPlayers.getText().toString()), place.getText().toString()));

    }


}