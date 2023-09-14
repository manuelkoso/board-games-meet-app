package it.units.boardgamesmeetapp.dialogs;

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
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.viewmodels.newevent.NewEventViewModel;
import it.units.boardgamesmeetapp.viewmodels.newevent.NewEventViewModelFactory;
import it.units.boardgamesmeetapp.models.Event;

public class NewEventDialog {
    @NonNull
    private final AlertDialog dialog;
    @NonNull
    private final DialogNewEventBinding binding;
    @NonNull
    private final NewEventViewModel viewModel;
    @Nullable
    private final Event initialEvent;
    private EditText game;
    private EditText numberOfPlayers;
    private EditText place;
    private EditText date;
    private EditText time;

    private NewEventDialog(@NonNull Fragment fragment, @Nullable Event initialEvent) {
        dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        binding = DialogNewEventBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        viewModel = new ViewModelProvider(fragment.getViewModelStore(), new NewEventViewModelFactory()).get(NewEventViewModel.class);
        this.initialEvent = initialEvent;

        viewModel.resetSubmissionResult();
        viewModel.setEventKey(getEventKey());

        initDialog(fragment);

        binding.dialogClose.setOnClickListener(v -> dialog.hide());
        binding.submitEventButton.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            submitNewEvent();
        });

        viewModel.getSubmissionResult().observe(fragment.getViewLifecycleOwner(), submissionResult -> {
            removeFieldErrors();
            binding.loading.setVisibility(View.GONE);
            switch (submissionResult.getResult()) {
                case EMPTY_FIELD:
                    setFieldErrorOnEmptyFields();
                    break;
                case OLD_DATE:
                    binding.date.setError(fragment.getResources().getString(submissionResult.getMessage()));
                    binding.time.setError(fragment.getResources().getString(submissionResult.getMessage()));
                    break;
                case SUCCESS:
                    showResult(fragment.requireContext(), submissionResult.getMessage());
                    dialog.hide();
                    break;
                case WRONG_NUMBER_PLAYERS:
                    binding.numberOfPlayers.setError(fragment.getResources().getString(submissionResult.getMessage()));
                    break;
                case NONE:
                    return;
                default:
                    showResult(fragment.requireContext(), submissionResult.getMessage());
            }
        });
        viewModel.resetSubmissionResult();
        dialog.setView(binding.getRoot());
    }

    private void initDialog(@NonNull Fragment fragment) {
        initFormFields();
        buildDataPicker(fragment);
        buildTimePicker(fragment);
        setTextButton();
    }

    private @Nullable String getEventKey() {
        if (initialEvent != null) return initialEvent.getKey();
        return null;
    }

    private void submitNewEvent() {
        viewModel.submit();
    }

    private void setTextButton() {
        if (initialEvent != null) {
            binding.submitEventButton.setText(R.string.modify);
        } else {
            binding.submitEventButton.setText(R.string.create);
        }
    }

    private void addTextWatcher() {
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

    private void buildDataPicker(@NonNull Fragment fragment) {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        date.setOnClickListener(v -> {
            Stream.of(game, numberOfPlayers, place).forEach(View::clearFocus);
            materialDatePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = format.format(calendar.getTime());
            date.setText(formattedDate);
        });
    }

    private void buildTimePicker(@NonNull Fragment fragment) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
        time.setOnClickListener(v -> {
            Stream.of(game, numberOfPlayers, place).forEach(View::clearFocus);
            timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
        });
        timePicker.addOnPositiveButtonClickListener(selection -> time.setText(String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
    }

    private void initFormFields() {
        game = Objects.requireNonNull(binding.game.getEditText());
        numberOfPlayers = Objects.requireNonNull(binding.numberOfPlayers.getEditText());
        place = Objects.requireNonNull(binding.place.getEditText());
        date = Objects.requireNonNull(binding.date.getEditText());
        time = Objects.requireNonNull(binding.time.getEditText());

        game.setText(viewModel.getCurrentGame().getValue());
        numberOfPlayers.setText(viewModel.getCurrentNumberOfPlayers().getValue());
        place.setText(viewModel.getCurrentPlace().getValue());
        date.setText(viewModel.getCurrentDate().getValue());
        time.setText(viewModel.getCurrentTime().getValue());

        addTextWatcher();
    }

    private void removeFieldErrors() {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).forEach(field -> field.setError(null));
    }

    private void setFieldErrorOnEmptyFields() {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).filter(field -> Objects.requireNonNull(field.getEditText()).getText().toString().isEmpty()).forEach(field -> field.setError("Empty field!"));
    }

    private void showResult(@NonNull Context context, @StringRes Integer message) {
        if (context.getApplicationContext() != null) {
            Toast.makeText(
                    context.getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment, @Nullable Event event) {
        NewEventDialog newEventDialog = new NewEventDialog(fragment, event);
        return newEventDialog.dialog;
    }

}
