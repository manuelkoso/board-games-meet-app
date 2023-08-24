package it.units.boardgamesmeetapp.dashboard;

import android.app.DownloadManager;
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
import com.google.firebase.database.Query;

import java.util.Collections;
import java.util.List;

import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.FragmentDashboardBinding;
import it.units.boardgamesmeetapp.databinding.SingleActivityBinding;
import it.units.boardgamesmeetapp.models.Activity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    private List<Activity> userActivities = Collections.emptyList();

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

        FirebaseRecyclerOptions<Activity> options = new FirebaseRecyclerOptions.Builder<Activity>().setQuery(FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference().child("activities"), Activity.class).build();
        FirebaseRecyclerAdapter<Activity, ActivityViewHolder> adapter = new FirebaseRecyclerAdapter<Activity, ActivityViewHolder>(options) {
            @NonNull
            @Override
            public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ActivityViewHolder(SingleActivityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ActivityViewHolder holder, int position, @NonNull Activity model) {
                SingleActivityBinding activityBinding = holder.getBinding();
                TextView textView = activityBinding.gameTitle;
                textView.setText(model.getGame().toString());
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