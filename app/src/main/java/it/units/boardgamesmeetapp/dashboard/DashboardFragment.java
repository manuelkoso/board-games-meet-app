package it.units.boardgamesmeetapp.dashboard;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.dashboard.dialog.EventDialog;
import it.units.boardgamesmeetapp.dashboard.dialog.NewEventDialog;
import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.databinding.FragmentDashboardBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.models.Event;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private AlertDialog dialog;

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

        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("IS_DIALOG_SHOWN")) {
                dialog = NewEventDialog.getInstance(this);
                dialog.show();
            }
        }

        binding.newActivityButton.setOnClickListener(v -> {
            dialog = NewEventDialog.getInstance(this);
            dialog.show();
        });

        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.EVENTS).whereArrayContains("players", Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        query = query.where(Filter.greaterThan("timestamp", new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = getEventEventViewHolderFirestoreRecyclerAdapter(options);

        RecyclerView recyclerView = binding.mainRecycler;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) {
            outState.putBoolean("IS_DIALOG_SHOWN", dialog.isShowing());
        } else {
            outState.putBoolean("IS_DIALOG_SHOWN", false);
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

                Date date = new Date(model.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());

                activityBinding.place.setText(model.getLocation());
                activityBinding.date.setText(dateFormat.format(date));
                activityBinding.gameTitle.setText(model.getGame());
                activityBinding.people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.card.setOnClickListener(v -> EventDialog.getInstance(DashboardFragment.this, model).show());

                if(Objects.equals(model.getOwnerId(), FirebaseAuth.getInstance().getUid())) {
                    activityBinding.eventButton.setText(R.string.cancel_the_event);
                    activityBinding.eventButton.setOnClickListener(v -> {
                        viewModel.deleteEvent(model);
                        showLoginResult(R.string.cancel_the_event);
                    });
                    activityBinding.modifyButton.setVisibility(View.VISIBLE);
                    activityBinding.modifyButton.setOnClickListener(v -> {

                    });
                } else {
                    activityBinding.eventButton.setText(R.string.unsubscribe);
                    activityBinding.eventButton.setOnClickListener(v -> {
                        viewModel.unsubscribe(model);
                        showLoginResult(R.string.unsubscribe);
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