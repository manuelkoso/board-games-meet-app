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


import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import it.units.boardgamesmeetapp.databinding.FragmentNewEventBinding;


public class NewEventFragment extends Fragment {

    private NewEventViewModel viewModel;
    private FragmentNewEventBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new NewEventViewModelFactory()).get(NewEventViewModel.class);

        final Button submitButton = binding.button;
        final EditText game = binding.game.getEditText();
        final EditText date = binding.date.getEditText();
        final EditText time = binding.time.getEditText();
        final EditText place = binding.place.getEditText();
        final EditText numberOfPlayers = binding.numberOfPlayers.getEditText();

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        date.setOnClickListener(v -> {
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
           date.setText(materialDatePicker.getHeaderText());
        });

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
        time.setOnClickListener(v -> {
            timePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
        });
        timePicker.addOnPositiveButtonClickListener(selection -> {
            time.setText(timePicker.getHour() + ":" + timePicker.getMinute());
        });
        submitButton.setOnClickListener(v -> viewModel.addNewActivity(game.getText().toString(), date.getText().toString(), 2, Integer.parseInt(numberOfPlayers.getText().toString()), place.getText().toString()));

    }


}