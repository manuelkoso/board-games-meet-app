package it.units.boardgamesmeetapp.dashboard.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.home.NewEventViewModel;
import it.units.boardgamesmeetapp.home.NewEventViewModelFactory;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.utils.Result;

public class NewEventDialog {

    private AlertDialog dialog;
    @NonNull
    private final DialogNewEventBinding binding;
    @NonNull
    private final NewEventViewModel viewModel;
    private EditText game;
    private EditText numberOfPlayers;
    private EditText place;
    private EditText date;
    private EditText time;

    private NewEventDialog(@NonNull Fragment fragment) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        binding = DialogNewEventBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        viewModel = new ViewModelProvider(fragment.getViewModelStore(), new NewEventViewModelFactory()).get(NewEventViewModel.class);
        viewModel.resetSubmissionResult();
        initFormFields();
        buildDataTimePickers(fragment);
        addTextWatcherOnFields();

        binding.button.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            viewModel.addNewEvent(game.getText().toString(), numberOfPlayers.getText().toString(), place.getText().toString(), date.getText().toString(), time.getText().toString());
        });
        viewModel.getSubmissionResult().observe(fragment.getViewLifecycleOwner(), submissionResult -> {
            removeFieldErrors();
            if (submissionResult == Result.NONE) return;
            if (submissionResult == Result.FAILURE) {
                binding.loading.setVisibility(View.GONE);
                setFieldErrors();
                showLoginResult(fragment.requireContext(), R.string.new_event_failed);
            } else if (submissionResult == Result.SUCCESS) {
                showLoginResult(fragment.requireContext(), R.string.new_event_success);
                dialog.hide();
            } else if(submissionResult == Result.OLD_DATE) {
                binding.loading.setVisibility(View.GONE);
                binding.date.setError("We cannot travel in the past!");
                binding.time.setError("We cannot travel in the past!");
            }
        });
        dialog.setView(binding.getRoot());
    }

    private NewEventDialog(@NonNull Fragment fragment, @NonNull Event initialEvent) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        binding = DialogNewEventBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        viewModel = new ViewModelProvider(fragment.getViewModelStore(), new NewEventViewModelFactory()).get(NewEventViewModel.class);
        viewModel.resetSubmissionResult();
        viewModel.updateCurrentEvent(initialEvent);
        initFormFields();
        buildDataTimePickers(fragment);
        addTextWatcherOnFields();

        binding.title.setText("Modify event");

        binding.button.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            viewModel.modifyEvent(initialEvent.getKey(), game.getText().toString(), numberOfPlayers.getText().toString(), place.getText().toString(), date.getText().toString(), time.getText().toString());
        });

        viewModel.getSubmissionResult().observe(fragment.getViewLifecycleOwner(), submissionResult -> {
            removeFieldErrors();
            if (submissionResult == Result.NONE) return;
            if (submissionResult == Result.FAILURE) {
                binding.loading.setVisibility(View.GONE);
                setFieldErrors();
                showLoginResult(fragment.requireContext(), R.string.new_event_failed);
            } else if (submissionResult == Result.SUCCESS) {
                showLoginResult(fragment.requireContext(), R.string.new_event_success);
                dialog.hide();
            } else if(submissionResult == Result.OLD_DATE) {
                binding.loading.setVisibility(View.GONE);
                binding.date.setError("We cannot travel in the past!");
                binding.time.setError("We cannot travel in the past!");
            }
        });

        dialog.setView(binding.getRoot());
    }

    private void addTextWatcherOnFields() {
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
                viewModel.updateCurrentEvent(game.getText().toString(), numberOfPlayers.getText().toString(), place.getText().toString(), date.getText().toString(), time.getText().toString());
            }
        };
        Stream.of(game, numberOfPlayers, place, date, time).forEach(field -> field.addTextChangedListener(afterTextChangedListener));
    }

    private void buildDataTimePickers(@NonNull Fragment fragment) {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        date.setOnClickListener(v -> materialDatePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> date.setText(materialDatePicker.getHeaderText()));

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
        time.setOnClickListener(v -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER"));
        timePicker.addOnPositiveButtonClickListener(selection -> time.setText(String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
    }

    private void initFormFields() {
        this.game = Objects.requireNonNull(binding.game.getEditText());
        this.numberOfPlayers = Objects.requireNonNull(binding.numberOfPlayers.getEditText());
        this.place = Objects.requireNonNull(binding.place.getEditText());
        this.date = Objects.requireNonNull(binding.date.getEditText());
        this.time = Objects.requireNonNull(binding.time.getEditText());

        game.setText(viewModel.getCurrentGame().getValue());
        numberOfPlayers.setText(viewModel.getCurrentNumberOfPlayers().getValue());
        place.setText(viewModel.getCurrentPlace().getValue());
        date.setText(viewModel.getCurrentDate().getValue());
        time.setText(viewModel.getCurrentTime().getValue());
    }

    private void removeFieldErrors() {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).forEach(field -> field.setError(null));
    }

    private void setFieldErrors() {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).filter(field -> Objects.requireNonNull(field.getEditText()).getText().toString().isEmpty()).forEach(field -> field.setError("Empty field!"));
    }

    private void showLoginResult(@NonNull Context context, @StringRes Integer message) {
        if (context.getApplicationContext() != null) {
            Toast.makeText(
                    context.getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment) {
        NewEventDialog newEventDialog = new NewEventDialog(fragment);
        return newEventDialog.dialog;
    }

    public static @NonNull AlertDialog getInstanceWithInitialEvent(@NonNull Fragment fragment, @NonNull Event event) {
        NewEventDialog newEventDialog = new NewEventDialog(fragment, event);
        return newEventDialog.dialog;
    }

}
