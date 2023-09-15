package it.units.boardgamesmeetapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.dialogs.PlayersDialog;
import it.units.boardgamesmeetapp.models.EventInfoView;
import it.units.boardgamesmeetapp.viewholders.EventViewHolder;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentHistoryBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.viewmodels.history.HistoryViewModel;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;

public class HistoryFragment extends Fragment {

    private static final String PLAYERS_DIALOG_KEY = "PLAYERS_DIALOG";
    private HistoryViewModel viewModel;
    private FragmentHistoryBinding binding;
    private AlertDialog playersDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle(getString(R.string.history));

        viewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);

        if (savedInstanceState != null && (savedInstanceState.getBoolean(PLAYERS_DIALOG_KEY))) {
                playersDialog = PlayersDialog.getInstance(this, Objects.requireNonNull(viewModel.getEventShown().getValue()));
                playersDialog.show();

        }

        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE).whereArrayContains(FirebaseConfig.PLAYERS_REFERENCE, Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        query = query.where(Filter.lessThan(FirebaseConfig.TIMESTAMP_REFERENCE, new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = getEventEventViewHolderFirestoreRecyclerAdapter(options);

        RecyclerView recyclerView = binding.historyRecycler;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (playersDialog != null) {
            outState.putBoolean(PLAYERS_DIALOG_KEY, playersDialog.isShowing());
        } else {
            outState.putBoolean(PLAYERS_DIALOG_KEY, false);
        }
    }

    @NonNull
    private FirestoreRecyclerAdapter<Event, EventViewHolder> getEventEventViewHolderFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        return new FirestoreRecyclerAdapter<Event, EventViewHolder>(options) {
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
                activityBinding.eventButton.setText(R.string.done);
                activityBinding.eventButton.setEnabled(false);
                activityBinding.card.setOnClickListener(v -> {
                    viewModel.setEventShown(model);
                    playersDialog = PlayersDialog.getInstance(HistoryFragment.this, model);
                    playersDialog.show();
                });
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}