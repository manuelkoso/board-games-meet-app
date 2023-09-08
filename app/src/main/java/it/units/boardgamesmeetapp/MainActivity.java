package it.units.boardgamesmeetapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        navController.addOnDestinationChangedListener(((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_login || destination.getId() == R.id.navigation_signup) {
                binding.navView.setVisibility(View.GONE);
            } else {
                binding.navView.setVisibility(View.VISIBLE);
            }
        }));

        NavigationUI.setupWithNavController(binding.navView, navController);

    }
}