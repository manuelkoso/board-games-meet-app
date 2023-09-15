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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.models.EventInfoView;
import it.units.boardgamesmeetapp.viewmodels.submit.SubmitEventViewModel;
import it.units.boardgamesmeetapp.viewmodels.submit.SubmitEventViewModelFactory;
import it.units.boardgamesmeetapp.models.Event;

public class SubmitEventDialog {
    @NonNull
    private final AlertDialog dialog;
    @NonNull
    private final DialogNewEventBinding binding;
    @NonNull
    private final SubmitEventViewModel viewModel;
    private EditText game;
    private EditText numberOfPlayers;
    private EditText place;
    private EditText date;
    private EditText time;

    private SubmitEventDialog(@NonNull Fragment fragment, @Nullable Event initialEvent) {
        dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        binding = DialogNewEventBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        viewModel = new ViewModelProvider(fragment.getViewModelStore(), new SubmitEventViewModelFactory()).get(SubmitEventViewModel.class);

        if (initialEvent != null && viewModel.isCurrentEventInfoViewNull() && viewModel.isInitialEventInfoViewNull()) {
            viewModel.setInitialEventInfoView(new EventInfoView(initialEvent));
            viewModel.setEventKey(initialEvent.getKey());
            viewModel.updateCurrentEvent(new EventInfoView(initialEvent));
        } else if (initialEvent == null && !viewModel.isInitialEventInfoViewNull() && !viewModel.isCurrentEventInfoViewNull()) {
            viewModel.setInitialEventInfoView(null);
            viewModel.setEventKey(null);
            viewModel.updateCurrentEvent(null);
        }

        initDialog(fragment);

        binding.dialogClose.setOnClickListener(v -> dialog.dismiss());
        binding.submitEventButton.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            viewModel.submit();
        });

        viewModel.getSubmissionResult().observe(fragment.getViewLifecycleOwner(), submissionResult -> {
            removeFieldErrors();
            binding.loading.setVisibility(View.GONE);
            switch (submissionResult.getResult()) {
                case EMPTY_FIELD:
                    setFieldErrorOnEmptyFields(fragment.getString(submissionResult.getMessage()));
                    break;
                case OLD_DATE:
                    binding.date.setErrorEnabled(true);
                    binding.time.setErrorEnabled(true);
                    binding.date.setError(fragment.getString(submissionResult.getMessage()));
                    binding.time.setError(fragment.getResources().getString(submissionResult.getMessage()));
                    break;
                case WRONG_NUMBER_PLAYERS:
                    binding.numberOfPlayers.setErrorEnabled(true);
                    binding.numberOfPlayers.setError(fragment.getString(submissionResult.getMessage()));
                    break;
                case NONE:
                    return;
                default:
                    showResult(fragment.requireContext(), submissionResult.getMessage());
                    dialog.dismiss();
            }
        });

        viewModel.getCurrentEventInfoView().observe(fragment.getViewLifecycleOwner(), eventInfoView -> {
            if (eventInfoView == null) {
                binding.submitEventButton.setEnabled(viewModel.getInitialEventInfoView() != null);
            } else {
                binding.submitEventButton.setEnabled(!eventInfoView.equals(viewModel.getInitialEventInfoView().getValue()));
            }
        });

        if (initialEvent == null) {
            binding.submitEventButton.setText(R.string.create);
        } else {
            binding.submitEventButton.setText(R.string.modify);
        }

        dialog.setView(binding.getRoot());
    }

    private void initDialog(@NonNull Fragment fragment) {
        initFormFields();
        buildDataPicker(fragment);
        buildTimePicker(fragment);
    }

    private void initFormFields() {
        game = Objects.requireNonNull(binding.game.getEditText());
        numberOfPlayers = Objects.requireNonNull(binding.numberOfPlayers.getEditText());
        place = Objects.requireNonNull(binding.place.getEditText());
        date = Objects.requireNonNull(binding.date.getEditText());
        time = Objects.requireNonNull(binding.time.getEditText());

        if (viewModel.getCurrentEventInfoView().getValue() != null) {
            game.setText(viewModel.getCurrentEventInfoView().getValue().getGame());
            numberOfPlayers.setText(viewModel.getCurrentEventInfoView().getValue().getMaxNumberOfPlayers());
            place.setText(viewModel.getCurrentEventInfoView().getValue().getPlace());
            date.setText(viewModel.getCurrentEventInfoView().getValue().getDate());
            time.setText(viewModel.getCurrentEventInfoView().getValue().getTime());
        }

        addTextWatcherToAllFields();
    }

    private void addTextWatcherToAllFields() {
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
                viewModel.resetSubmissionResult();
                EventInfoView eventInfoView = new EventInfoView(game.getText().toString(), numberOfPlayers.getText().toString(), place.getText().toString(), date.getText().toString(), time.getText().toString());
                viewModel.updateCurrentEvent(eventInfoView);
            }
        };
        Stream.of(game, numberOfPlayers, place, date, time).forEach(field -> field.addTextChangedListener(afterTextChangedListener));
    }

    private void buildDataPicker(@NonNull Fragment fragment) {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.date_picker_title).build();
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
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText(R.string.time_picker_title).build();
        time.setOnClickListener(v -> {
            Stream.of(game, numberOfPlayers, place).forEach(View::clearFocus);
            timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
        });
        timePicker.addOnPositiveButtonClickListener(selection -> time.setText(String.format(Locale.getDefault(), "%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
    }

    private void removeFieldErrors() {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).forEach(field -> {
            field.setErrorEnabled(false);
            field.setError(null);
        });
    }

    private void setFieldErrorOnEmptyFields(String errorMessage) {
        Stream.of(binding.game, binding.numberOfPlayers, binding.place, binding.date, binding.time).filter(field -> Objects.requireNonNull(field.getEditText()).getText().toString().isEmpty()).forEach(field -> {
            field.setErrorEnabled(true);
            field.setError(errorMessage);
        });
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
        SubmitEventDialog newEventDialog = new SubmitEventDialog(fragment, event);
        return newEventDialog.dialog;
    }

}
