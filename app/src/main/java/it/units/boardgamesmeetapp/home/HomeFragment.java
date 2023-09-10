package it.units.boardgamesmeetapp.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.dashboard.EventViewHolder;
import it.units.boardgamesmeetapp.dashboard.dialog.EventDialog;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentHomeBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.models.Event;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

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

        RecyclerView recyclerView = binding.mainRecycler;
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS);
        query = query.where(Filter.greaterThan("timestamp", new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = getFilteredAdapter(options);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();

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
                        Query filteredQuery = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS);
                        filteredQuery = filteredQuery.where(Filter.greaterThan("timestamp", new Date().getTime()));
                        filteredQuery = filteredQuery.whereEqualTo("game", searchView.getEditText().getText().toString());
                        FirestoreRecyclerOptions<Event> filteredOption = new FirestoreRecyclerOptions.Builder<Event>().setQuery(filteredQuery, Event.class).build();
                        FirestoreRecyclerAdapter<Event, EventViewHolder> filteredAdapter = getFilteredAdapter(filteredOption);
                        binding.filteredRecycler.setAdapter(filteredAdapter);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        binding.filteredRecycler.setLayoutManager(manager);
                        filteredAdapter.startListening();
                    }
                }));
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

                activityBinding.place.setText(model.getLocation());
                activityBinding.date.setText(dateFormat.format(date));
                activityBinding.gameTitle.setText(model.getGame());
                activityBinding.people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.eventButton.setText(R.string.submit);

                if (model.getPlayers().contains(FirebaseAuth.getInstance().getUid()) || model.getMaxNumberOfPlayers() == model.getPlayers().size())
                    activityBinding.eventButton.setEnabled(false);
                activityBinding.eventButton.setOnClickListener(v -> {
                    activityBinding.eventButton.setEnabled(false);
                    homeViewModel.submit(model);
                });
                activityBinding.card.setOnClickListener(v -> EventDialog.getInstance(HomeFragment.this, model).show());
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}