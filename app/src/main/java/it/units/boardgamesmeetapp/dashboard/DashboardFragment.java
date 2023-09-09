package it.units.boardgamesmeetapp.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.dashboard.dialog.EventDialog;
import it.units.boardgamesmeetapp.dashboard.dialog.NewEventDialog;
import it.units.boardgamesmeetapp.databinding.FragmentDashboardBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.models.Event;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    private List<Event> userActivities = Collections.emptyList();

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

        binding.newActivityButton.setOnClickListener(v -> NewEventDialog.getInstance(this).show());

        Query query = FirebaseFirestore.getInstance().collection("activities").whereArrayContains("players", FirebaseAuth.getInstance().getUid());
        query = query.where(Filter.greaterThan("timestamp", new Date().getTime()));
        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        FirestoreRecyclerAdapter<Event, EventViewHolder> adapter = new FirestoreRecyclerAdapter<Event, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new EventViewHolder(SingleEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event model) {
                SingleEventBinding activityBinding = holder.getBinding();
                TextView gameTitle = activityBinding.gameTitle;
                TextView place = activityBinding.place;
                TextView date = activityBinding.date;
                TextView people = activityBinding.people;
                place.setText(model.getLocation());
                Date d = new Date(model.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
                date.setText(dateFormat.format(d));
                gameTitle.setText(model.getGame());
                people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.people.setOnClickListener(v -> EventDialog.getInstance(DashboardFragment.this, model).show());
                if(Objects.equals(model.getOwnerId(), FirebaseAuth.getInstance().getUid())) {
                    activityBinding.eventButton.setText("Cancel the event");
                    activityBinding.card.setStrokeColor(Color.BLUE);
                    activityBinding.eventButton.setOnClickListener(v -> {
                        viewModel.deleteEvent(model);
                    });
                } else {
                    activityBinding.eventButton.setText("Unsubscribe");
                    activityBinding.eventButton.setOnClickListener(v -> {
                        viewModel.unsubscribe(model);
                    });
                }

            }
        };
        RecyclerView recyclerView = binding.activitiesRecycler;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}