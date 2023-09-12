package it.units.boardgamesmeetapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;


import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.FragmentProfileBinding;
import it.units.boardgamesmeetapp.viewmodels.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.MainViewModelFactory;
import it.units.boardgamesmeetapp.viewmodels.profile.ProfileViewModel;
import it.units.boardgamesmeetapp.viewmodels.profile.ProfileViewModelFactory;
import it.units.boardgamesmeetapp.dialogs.UserInfoDialog;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private AlertDialog profileInformationDialog;

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
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle("Profile");
        mainViewModel.updateActionBarBackButtonState(false);
        if(savedInstanceState != null && (savedInstanceState.getBoolean("IS_PROFILE_DIALOG_SHOWN"))) {
            profileInformationDialog = UserInfoDialog.getInstance(this);
            profileInformationDialog.show();
        }

        binding.informationButton.setOnClickListener(v -> {
            profileInformationDialog = UserInfoDialog.getInstance(this);
            profileInformationDialog.show();
        });
        binding.logout.setOnClickListener(v -> {
                    viewModel.logout();
                    NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_global_loginFragment));
                }
        );
        binding.historyButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_profile_to_historyFragment));
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (profileInformationDialog != null) {
            outState.putBoolean("IS_PROFILE_DIALOG_SHOWN", profileInformationDialog.isShowing());
        } else {
            outState.putBoolean("IS_PROFILE_DIALOG_SHOWN", false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}