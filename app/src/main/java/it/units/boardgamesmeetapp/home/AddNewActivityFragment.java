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

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentAddNewActivityBinding;

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

        submitButton.setOnClickListener(v -> {
            viewModel.addNewActivity();
        });

    }


}