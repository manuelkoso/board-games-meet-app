package it.units.boardgamesmeetapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.databinding.FragmentHomeBinding;
import it.units.boardgamesmeetapp.utils.Result;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private NewEventViewModel newEventViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        newEventViewModel = new ViewModelProvider(this, new NewEventViewModelFactory()).get(NewEventViewModel.class);

        final TextView textView = binding.textHome;
        final FloatingActionButton newActivityButton = binding.newActivityButton;

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        newActivityButton.setOnClickListener(v -> {
            DialogNewEventBinding binding = DialogNewEventBinding.inflate(LayoutInflater.from(getContext()));;
            AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).create();
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
            binding.date.setOnClickListener(view1 -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                binding.date.setText(materialDatePicker.getHeaderText().toString());
            });
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
            binding.time.setOnClickListener(view1 -> timePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER"));
            timePicker.addOnPositiveButtonClickListener(selection -> binding.time.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
            binding.button.setOnClickListener(view1 -> {
                binding.loading.setVisibility(View.VISIBLE);
                String game = binding.game.getEditText().getText().toString();
                String numberOfPlayers = binding.numberOfPlayers.getEditText().getText().toString();
                String place = binding.place.getEditText().getText().toString();
                String date = binding.date.getText().toString();
                String time = binding.time.getText().toString();
                newEventViewModel.addNewActivity(game, numberOfPlayers, place, date, time);
            });
            newEventViewModel.getSubmissionResult().observe(getViewLifecycleOwner(), submissionResult -> {
                if (submissionResult == Result.NONE) return;
                if (submissionResult == Result.FAILURE) {
                    binding.loading.setVisibility(View.GONE);
                    showLoginResult(R.string.new_event_failed);
                };
                if(submissionResult == Result.SUCCESS) {
                    showLoginResult(R.string.new_event_success);
                    NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_home_to_navigation_dashboard));
                    dialog.hide();
                }
            });
            dialog.setView(binding.getRoot());
            dialog.show();
        });
    }

    private void showLoginResult(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}