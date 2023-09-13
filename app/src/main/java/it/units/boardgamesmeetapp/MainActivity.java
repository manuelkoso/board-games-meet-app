package it.units.boardgamesmeetapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.ActivityMainBinding;
import it.units.boardgamesmeetapp.fragments.HomeFragment;
import it.units.boardgamesmeetapp.viewmodels.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.MainViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MainViewModel.class);
        viewModel.getActionBarTitle().observe(this, Objects.requireNonNull(binding.topAppBar)::setTitle);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        navController.addOnDestinationChangedListener(((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_login || destination.getId() == R.id.navigation_signup) {
                binding.navView.setVisibility(View.GONE);
                Objects.requireNonNull(binding.topAppBar).setVisibility(View.GONE);
            } else {
                binding.navView.setVisibility(View.VISIBLE);
                Objects.requireNonNull(binding.topAppBar).setVisibility(View.VISIBLE);
            }
        }));

        Objects.requireNonNull(binding.topAppBar).setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more) {
                viewModel.logout();
                navController.navigate(R.id.action_global_loginFragment);
                return true;
            }
            return false;
        });

        if (binding.navView instanceof BottomNavigationView) {
            NavigationUI.setupWithNavController((BottomNavigationView) binding.navView, navController);
        } else {
            NavigationUI.setupWithNavController((NavigationView) binding.navView, navController);
        }
    }


}