package it.units.boardgamesmeetapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.viewholders.EventViewHolder;
import it.units.boardgamesmeetapp.dialogs.PlayersDialog;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentHomeBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.dialogs.FilterDialog;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModelFactory;
import it.units.boardgamesmeetapp.viewmodels.home.HomeViewModel;
import it.units.boardgamesmeetapp.viewmodels.home.HomeViewModelFactory;
import it.units.boardgamesmeetapp.models.Event;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private AlertDialog eventDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory()).get(HomeViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle("Find events");
        mainViewModel.updateActionBarBackButtonState(false);
        if (savedInstanceState != null && (savedInstanceState.getBoolean("IS_EVENT_DIALOG_SHOWN"))) {
            eventDialog = PlayersDialog.getInstance(this, Objects.requireNonNull(homeViewModel.getCurrentEventShown().getValue()));
            eventDialog.show();
        }


        RecyclerView recyclerView = binding.mainRecycler;
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE);
        query = query.where(Filter.greaterThan("timestamp", new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = getFilteredAdapter(options);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();

        homeViewModel.getRadioButtonIdMutableLiveData().observe(getViewLifecycleOwner(), buttonId -> {
            binding.searchBar.setHint(getFilterFieldString(buttonId));
            binding.searchView.setHint(getFilterFieldString(buttonId));
        });

        binding.filterButton.setOnClickListener(v -> FilterDialog.getInstance(this).show());

        binding.searchView.setupWithSearchBar(binding.searchBar);
        binding.searchView.addTransitionListener(
                (searchView, previousState, newState) -> searchView.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // ignored
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // ignored
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Integer radioButtonId = homeViewModel.getRadioButtonIdMutableLiveData().getValue();
                        FirestoreRecyclerOptions<Event> filteredOption = new FirestoreRecyclerOptions.Builder<Event>().setQuery(getFilterQuery(radioButtonId, searchView.getEditText().getText().toString()), Event.class).build();
                        FirestoreRecyclerAdapter<Event, EventViewHolder> filteredAdapter = getFilteredAdapter(filteredOption);
                        binding.filteredRecycler.setAdapter(filteredAdapter);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.filteredRecycler.setLayoutManager(manager);
                        filteredAdapter.startListening();
                    }
                }));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (eventDialog != null) {
            outState.putBoolean("IS_EVENT_DIALOG_SHOWN", eventDialog.isShowing());
        } else {
            outState.putBoolean("IS_EVENT_DIALOG_SHOWN", false);
        }
    }

    @NonNull
    private static Query getFilterQuery(@NonNull Integer buttonId, @NonNull String inputString) {
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE);
        if (buttonId == R.id.radio_button_game)
            query = query.orderBy("game").orderBy("timestamp", Query.Direction.DESCENDING).startAt(inputString.toLowerCase()).endAt(inputString.toLowerCase() + "\uf8ff").limit(20);
        if (buttonId == R.id.radio_button_place)
            query = query.orderBy("location").orderBy("timestamp", Query.Direction.DESCENDING).startAt(inputString.toLowerCase()).endAt(inputString.toLowerCase() + "\uf8ff").limit(20);
        return query;
    }

    @NonNull
    private static String getFilterFieldString(@NonNull Integer buttonId) {
        String field = "Search: ";
        if (buttonId == R.id.radio_button_game) field = field.concat("Game");
        if (buttonId == R.id.radio_button_place) field = field.concat("Place");
        return field;
    }

    @NonNull
    private FirestoreRecyclerAdapter<Event, EventViewHolder> getFilteredAdapter(FirestoreRecyclerOptions<Event> filteredOption) {
        return new FirestoreRecyclerAdapter<Event, EventViewHolder>(filteredOption) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new EventViewHolder(SingleEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event model) {
                SingleEventBinding activityBinding = holder.getBinding();

                Date date = new Date(model.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());

                activityBinding.place.setText(model.getPlace());
                activityBinding.date.setText(dateFormat.format(date));
                activityBinding.gameTitle.setText(model.getGame());
                activityBinding.people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.eventButton.setText(R.string.submit);

                if (model.getTimestamp() < new Date().getTime()) {
                    activityBinding.eventButton.setText("Done");
                    activityBinding.eventButton.setEnabled(false);
                    return;
                }
                if (model.getPlayers().contains(FirebaseAuth.getInstance().getUid()) || model.getMaxNumberOfPlayers() == model.getPlayers().size())
                    activityBinding.eventButton.setEnabled(false);
                activityBinding.eventButton.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(requireContext()).setTitle("Subscription")
                            .setMessage("Do you want to subscribe to this event?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                homeViewModel.submit(model);
                                activityBinding.eventButton.setEnabled(false);
                                showLoginResult(R.string.submit);
                            }).setNegativeButton("No", ((dialogInterface, i) -> {
                            })).show();
                });
                activityBinding.card.setOnClickListener(v -> {
                    homeViewModel.updateCurrentEventShown(model);
                    eventDialog = PlayersDialog.getInstance(HomeFragment.this, model);
                    eventDialog.show();
                });
            }
        };
    }

    private void showLoginResult(@StringRes Integer message) {
        if (requireContext().getApplicationContext() != null) {
            Toast.makeText(
                    requireContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}