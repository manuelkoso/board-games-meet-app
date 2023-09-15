package it.units.boardgamesmeetapp.fragments;

import android.os.Bundle;
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
import it.units.boardgamesmeetapp.databinding.DashboardEventBinding;
import it.units.boardgamesmeetapp.models.EventInfoView;
import it.units.boardgamesmeetapp.viewholders.DashboardEventViewHolder;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.main.MainViewModelFactory;
import it.units.boardgamesmeetapp.viewmodels.dashboard.DashboardViewModel;
import it.units.boardgamesmeetapp.viewmodels.dashboard.DashboardViewModelFactory;
import it.units.boardgamesmeetapp.dialogs.PlayersDialog;
import it.units.boardgamesmeetapp.dialogs.SubmitEventDialog;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentDashboardBinding;
import it.units.boardgamesmeetapp.models.Event;

public class DashboardFragment extends Fragment {

    public static final String ADD_EVENT_KEY = "IS_ADD_EVENT_DIALOG_SHOWN";
    public static final String PLAYERS_DIALOG_KEY = "IS_EVENT_DIALOG_SHOWN";
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private AlertDialog addEventDialog;
    private AlertDialog playersDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new DashboardViewModelFactory()).get(DashboardViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory()).get(MainViewModel.class);
        mainViewModel.updateActionBarTitle(getString(R.string.my_events));

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ADD_EVENT_KEY)) {
                addEventDialog = SubmitEventDialog.getInstance(this, viewModel.getCurrentEventShown().getValue());
                addEventDialog.show();
            }
            if (savedInstanceState.getBoolean(PLAYERS_DIALOG_KEY)) {
                playersDialog = PlayersDialog.getInstance(this, Objects.requireNonNull(viewModel.getCurrentEventShown().getValue()));
                playersDialog.show();
            }
        }

        binding.newActivityButton.setOnClickListener(v -> {
            viewModel.updateCurrentEventShown(null);
            addEventDialog = SubmitEventDialog.getInstance(this, null);
            addEventDialog.show();
        });

        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS_REFERENCE).whereArrayContains(FirebaseConfig.PLAYERS_FIELD_REFERENCE, Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        query = query.where(Filter.greaterThan(FirebaseConfig.TIMESTAMP_FIELD_REFERENCE, getCurrentTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, DashboardEventViewHolder> adapter = getEventEventViewHolderFirestoreRecyclerAdapter(options);

        RecyclerView recyclerView = binding.mainRecycler;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private static long getCurrentTime() {
        return new Date().getTime();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (addEventDialog != null) {
            outState.putBoolean(ADD_EVENT_KEY, addEventDialog.isShowing());
        } else {
            outState.putBoolean(ADD_EVENT_KEY, false);
        }
        if (playersDialog != null) {
            outState.putBoolean(PLAYERS_DIALOG_KEY, playersDialog.isShowing());
        } else {
            outState.putBoolean(PLAYERS_DIALOG_KEY, false);
        }
    }

    @NonNull
    private FirestoreRecyclerAdapter<Event, DashboardEventViewHolder> getEventEventViewHolderFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        return new FirestoreRecyclerAdapter<Event, DashboardEventViewHolder>(options) {
            @NonNull
            @Override
            public DashboardEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new DashboardEventViewHolder(DashboardEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull DashboardEventViewHolder holder, int position, @NonNull Event model) {
                DashboardEventBinding activityBinding = holder.getBinding();
                EventInfoView eventInfoView = new EventInfoView(model);

                activityBinding.place.setText(eventInfoView.getPlace());
                activityBinding.date.setText(eventInfoView.getDateTime());
                activityBinding.gameTitle.setText(eventInfoView.getGame());
                activityBinding.people.setText(eventInfoView.getNumberOfPlayersOverMaxNumber());
                activityBinding.card.setOnClickListener(v -> {
                    viewModel.updateCurrentEventShown(model);
                    playersDialog = PlayersDialog.getInstance(DashboardFragment.this, model);
                    playersDialog.show();
                });

                if (Objects.equals(model.getOwnerId(), FirebaseAuth.getInstance().getUid())) {
                    activityBinding.eventButton.setText(R.string.cancel);
                    activityBinding.eventButton.setOnClickListener(v ->
                            new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.cancel)
                                    .setMessage(R.string.cancel_event_dialog_title)
                                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                        viewModel.deleteEvent(model);
                                        showResult(R.string.event_canceled);
                                    }).setNegativeButton(R.string.no, ((dialogInterface, i) -> {
                                    })).show());
                    activityBinding.modifyButton.setVisibility(View.VISIBLE);
                    activityBinding.modifyButton.setOnClickListener(v -> {
                        viewModel.updateCurrentEventShown(model);
                        addEventDialog = SubmitEventDialog.getInstance(DashboardFragment.this, model);
                        addEventDialog.show();
                    });
                } else {
                    activityBinding.eventButton.setText(R.string.unsubscribe);
                    activityBinding.eventButton.setOnClickListener(v ->
                            new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.unsubscribe)
                                    .setMessage(R.string.unsubscribe_event_dialog_title)
                                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                        viewModel.unsubscribe(model);
                                        showResult(R.string.event_unsubscribed);
                                    }).setNegativeButton(R.string.no, ((dialogInterface, i) -> {
                                    })).show());
                }
            }
        };
    }

    private void showResult(@StringRes Integer message) {
        if (requireContext().getApplicationContext() != null) {
            Toast.makeText(
                    requireContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}