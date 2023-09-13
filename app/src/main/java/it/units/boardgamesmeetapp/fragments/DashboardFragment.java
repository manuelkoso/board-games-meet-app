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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.databinding.DashboardEventBinding;
import it.units.boardgamesmeetapp.viewholders.DashboardEventViewHolder;
import it.units.boardgamesmeetapp.viewmodels.MainViewModel;
import it.units.boardgamesmeetapp.viewmodels.MainViewModelFactory;
import it.units.boardgamesmeetapp.viewmodels.dashboard.DashboardViewModel;
import it.units.boardgamesmeetapp.viewmodels.dashboard.DashboardViewModelFactory;
import it.units.boardgamesmeetapp.viewholders.EventViewHolder;
import it.units.boardgamesmeetapp.dialogs.PlayersDialog;
import it.units.boardgamesmeetapp.dialogs.NewEventDialog;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentDashboardBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.models.Event;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private AlertDialog addEventDialog;
    private AlertDialog eventDialog;

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
        mainViewModel.updateActionBarTitle("My events");
        mainViewModel.updateActionBarBackButtonState(false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("IS_ADD_EVENT_DIALOG_SHOWN")) {
                addEventDialog = NewEventDialog.getInstance(this);
                addEventDialog.show();
            }
            if (savedInstanceState.getBoolean("IS_EVENT_DIALOG_SHOWN")) {
                eventDialog = PlayersDialog.getInstance(this, Objects.requireNonNull(viewModel.getCurrentEventShown().getValue()));
                eventDialog.show();
            }
        }

        binding.newActivityButton.setOnClickListener(v -> {
            addEventDialog = NewEventDialog.getInstance(this);
            addEventDialog.show();
        });

        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS).whereArrayContains("players", Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        query = query.where(Filter.greaterThan("timestamp", new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, DashboardEventViewHolder> adapter = getEventEventViewHolderFirestoreRecyclerAdapter(options);

        RecyclerView recyclerView = binding.mainRecycler;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (addEventDialog != null) {
            outState.putBoolean("IS_ADD_EVENT_DIALOG_SHOWN", addEventDialog.isShowing());
        } else {
            outState.putBoolean("IS_ADD_EVENT_DIALOG_SHOWN", false);
        }
        if (eventDialog != null) {
            outState.putBoolean("IS_EVENT_DIALOG_SHOWN", eventDialog.isShowing());
        } else {
            outState.putBoolean("IS_EVENT_DIALOG_SHOWN", false);
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

                Date date = new Date(model.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());

                activityBinding.place.setText(model.getLocation());
                activityBinding.date.setText(dateFormat.format(date));
                activityBinding.gameTitle.setText(model.getGame());
                activityBinding.people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.card.setOnClickListener(v -> {
                    viewModel.updateCurrentEventShown(model);
                    eventDialog = PlayersDialog.getInstance(DashboardFragment.this, model);
                    eventDialog.show();
                });

                if (Objects.equals(model.getOwnerId(), FirebaseAuth.getInstance().getUid())) {
                    activityBinding.eventButton.setText(R.string.cancel_the_event);
                    activityBinding.eventButton.setOnClickListener(v -> {
                        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.cancel_the_event)
                                .setMessage("Do you want to cancel this event?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    viewModel.deleteEvent(model);
                                    showLoginResult(R.string.cancel_the_event);
                                }).setNegativeButton("No", ((dialogInterface, i) -> {})).show();
                    });
                    activityBinding.modifyButton.setVisibility(View.VISIBLE);
                    activityBinding.modifyButton.setOnClickListener(v -> {
                        eventDialog = NewEventDialog.getInstanceWithInitialEvent(DashboardFragment.this, model);
                        eventDialog.show();
                    });
                } else {
                    activityBinding.eventButton.setText(R.string.unsubscribe);
                    activityBinding.eventButton.setOnClickListener(v -> {
                        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.cancel_the_event)
                                .setMessage("Do you want to unsubscribe to this event?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    viewModel.unsubscribe(model);
                                    showLoginResult(R.string.unsubscribe);
                                }).setNegativeButton("No", ((dialogInterface, i) -> {})).show();
                    });
                }
            }
        };
    }

    private void showLoginResult(@StringRes Integer message) {
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