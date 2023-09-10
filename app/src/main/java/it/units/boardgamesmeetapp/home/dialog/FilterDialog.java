package it.units.boardgamesmeetapp.home.dialog;

import android.view.LayoutInflater;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.boardgamesmeetapp.databinding.DialogFilterBinding;
import it.units.boardgamesmeetapp.home.HomeViewModel;
import it.units.boardgamesmeetapp.home.HomeViewModelFactory;

public class FilterDialog {
    @NonNull
    private AlertDialog dialog;

    private FilterDialog(@NonNull Fragment fragment) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        DialogFilterBinding binding = DialogFilterBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        HomeViewModel viewModel =  new ViewModelProvider(fragment, new HomeViewModelFactory()).get(HomeViewModel.class);
        binding.radioGroup.setOnCheckedChangeListener((radioGroup, radioButtonId) -> viewModel.updateFilterField(radioButtonId));
        viewModel.getRadioButtonIdMutableLiveData().observe(fragment.getViewLifecycleOwner(), buttonId -> {
            RadioButton button = binding.getRoot().findViewById(buttonId);
            button.setChecked(true);
        });
        dialog.setView(binding.getRoot());
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment) {
        FilterDialog filterDialog = new FilterDialog(fragment);
        return filterDialog.dialog;
    }

}
