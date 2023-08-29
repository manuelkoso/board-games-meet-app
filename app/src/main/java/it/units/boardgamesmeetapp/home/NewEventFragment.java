package it.units.boardgamesmeetapp.home;

import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentNewEventBinding;
import it.units.boardgamesmeetapp.utils.Result;


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
        final Button date = binding.date;
        final Button time = binding.time;
        final EditText place = binding.place.getEditText();
        final EditText numberOfPlayers = binding.numberOfPlayers.getEditText();
        final ProgressBar loadingProgressBar = binding.loading;

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        date.setOnClickListener(v -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> date.setText(materialDatePicker.getHeaderText()));

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
        time.setOnClickListener(v -> {
            timePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
        });
        timePicker.addOnPositiveButtonClickListener(selection -> time.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
        submitButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            viewModel.addNewActivity(game.getText().toString(), numberOfPlayers.getText().toString(), place.getText().toString(), date.getText().toString(), time.getText().toString());
        });

        viewModel.getSubmissionResult().observe(getViewLifecycleOwner(), submissionResult -> {
            if (submissionResult == Result.NONE) return;
            if (submissionResult == Result.FAILURE) showLoginFailed(R.string.new_event_failed);
            if(submissionResult == Result.SUCCESS) {
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_new_event_to_navigation_dashboard));
            }
        });

    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

}