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

import java.util.Date;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.models.EventInfoView;
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

    public static final String EVENT_DIALOG_SHOWN = "IS_EVENT_DIALOG_SHOWN";
    public static final int MAX_NUMBER_FILTERED_EVENTS = 20;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private AlertDialog playersDialog;

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
        mainViewModel.updateActionBarTitle(getString(R.string.find_events));
        mainViewModel.updateActionBarBackButtonState(false);
        if (savedInstanceState != null && (savedInstanceState.getBoolean(EVENT_DIALOG_SHOWN))) {
            playersDialog = PlayersDialog.getInstance(this, Objects.requireNonNull(homeViewModel.getCurrentEventShown().getValue()));
            playersDialog.show();
        }


        RecyclerView recyclerView = binding.mainRecycler;
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE);
        query = query.where(Filter.greaterThan(FirebaseConfig.TIMESTAMP_FIELD_REFERENCE, new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = getFilteredAdapter(options);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();

        homeViewModel.getRadioButtonIdMutableLiveData().observe(getViewLifecycleOwner(), buttonId -> {
            if (buttonId == R.id.radio_button_game) {
                binding.searchBar.setHint(R.string.search_game);
                binding.searchView.setHint(R.string.search_game);
            } else {
                binding.searchBar.setHint(R.string.search_place);
                binding.searchView.setHint(R.string.search_place);
            }

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
                        FirestoreRecyclerOptions<Event> filteredOption = new FirestoreRecyclerOptions.Builder<Event>().setQuery(getFilterQuery(Objects.requireNonNull(radioButtonId), searchView.getEditText().getText().toString()), Event.class).build();
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
        if (playersDialog != null) {
            outState.putBoolean(EVENT_DIALOG_SHOWN, playersDialog.isShowing());
        } else {
            outState.putBoolean(EVENT_DIALOG_SHOWN, false);
        }
    }

    @NonNull
    private static Query getFilterQuery(@NonNull Integer buttonId, @NonNull String inputString) {
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE);
        if (buttonId == R.id.radio_button_game)
            query = query.orderBy(FirebaseConfig.GAME_FIELD_REFERENCE).
                    orderBy(FirebaseConfig.TIMESTAMP_FIELD_REFERENCE, Query.Direction.DESCENDING).
                    startAt(inputString.toUpperCase()).endAt(inputString.toUpperCase() + "\uf8ff").
                    limit(MAX_NUMBER_FILTERED_EVENTS);
        if (buttonId == R.id.radio_button_place)
            query = query.orderBy(FirebaseConfig.PLACE_FIELD_REFERENCE).
                    orderBy(FirebaseConfig.TIMESTAMP_FIELD_REFERENCE, Query.Direction.DESCENDING).
                    startAt(inputString.toUpperCase()).endAt(inputString.toUpperCase() + "\uf8ff").
                    limit(MAX_NUMBER_FILTERED_EVENTS);
        return query;
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
                EventInfoView eventInfoView = new EventInfoView(model);

                activityBinding.place.setText(eventInfoView.getPlace());
                activityBinding.date.setText(eventInfoView.getDateTime());
                activityBinding.gameTitle.setText(eventInfoView.getGame());
                activityBinding.people.setText(eventInfoView.getNumberOfPlayersOverMaxNumber());
                activityBinding.eventButton.setText(R.string.submit);

                if (model.getTimestamp() < new Date().getTime()) {
                    activityBinding.eventButton.setText(R.string.done);
                    activityBinding.eventButton.setEnabled(false);
                    return;
                }
                if (model.getPlayers().contains(FirebaseAuth.getInstance().getUid()) || model.getMaxNumberOfPlayers() == model.getPlayers().size())
                    activityBinding.eventButton.setEnabled(false);
                activityBinding.eventButton.setOnClickListener(v ->
                        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.subscribtion)
                                .setMessage(R.string.event_subscribe_dialog_title)
                                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                    homeViewModel.submit(model);
                                    activityBinding.eventButton.setEnabled(false);
                                    showResult(R.string.submit);
                                }).setNegativeButton(R.string.no, ((dialogInterface, i) -> {
                                })).show());
                activityBinding.card.setOnClickListener(v -> {
                    homeViewModel.updateCurrentEventShown(model);
                    playersDialog = PlayersDialog.getInstance(HomeFragment.this, model);
                    playersDialog.show();
                });
            }
        };
    }

    private void showResult(@StringRes Integer message) {
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