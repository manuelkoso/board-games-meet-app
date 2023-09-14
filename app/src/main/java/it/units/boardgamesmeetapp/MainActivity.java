package it.units.boardgamesmeetapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import it.units.boardgamesmeetapp.databinding.ActivityMainBinding;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModelFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainViewModel viewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MainViewModel.class);
        viewModel.getActionBarTitle().observe(this, Objects.requireNonNull(binding.topAppBar)::setTitle);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        navController.addOnDestinationChangedListener(((controller, destination, arguments) -> {
            Menu menu = binding.topAppBar.getMenu();
            if (destination.getId() == R.id.navigation_login || destination.getId() == R.id.navigation_signup) {
                menu.findItem(R.id.more).setVisible(false);
                binding.navView.setVisibility(View.GONE);
            } else {
                menu.findItem(R.id.more).setVisible(true);
                binding.navView.setVisibility(View.VISIBLE);
            }
        }));

        Objects.requireNonNull(binding.topAppBar).setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more) {
                buildLogoutDialog(viewModel, navController).show();
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

    private AlertDialog buildLogoutDialog(MainViewModel viewModel, NavController navController) {
        return new MaterialAlertDialogBuilder(this).setTitle(R.string.logout)
                .setMessage(R.string.logout_question)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    viewModel.logout();
                    navController.navigate(R.id.action_global_loginFragment);
                }).setNegativeButton(R.string.no, ((dialogInterface, i) -> {
                })).show();
    }

}