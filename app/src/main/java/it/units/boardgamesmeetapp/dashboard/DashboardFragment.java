package it.units.boardgamesmeetapp.dashboard;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
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

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference().child("activities"), Event.class).build();
        FirebaseRecyclerAdapter<Event, EventViewHolder> adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
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
                TextView time = activityBinding.time;
                TextView people = activityBinding.people;
                place.setText(model.getLocation().toString());
                date.setText(model.getDate());
                time.setText(model.getTime());
                gameTitle.setText(model.getGame().toString());
                // people.setText(model.getPlayers().size());
            }
        };
        RecyclerView recyclerView = binding.activitiesRecycler;
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}