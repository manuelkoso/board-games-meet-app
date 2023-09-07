package it.units.boardgamesmeetapp.home;

import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.units.boardgamesmeetapp.R;
import it.units.boardgamesmeetapp.config.FirebaseConfig;
import it.units.boardgamesmeetapp.dashboard.EventViewHolder;
import it.units.boardgamesmeetapp.databinding.DialogEventPlayersBinding;
import it.units.boardgamesmeetapp.databinding.DialogNewEventBinding;
import it.units.boardgamesmeetapp.databinding.FragmentHomeBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.databinding.SinglePlayerBinding;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.UserInfo;
import it.units.boardgamesmeetapp.utils.Result;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private NewEventViewModel newEventViewModel;

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
        newEventViewModel = new ViewModelProvider(this, new NewEventViewModelFactory()).get(NewEventViewModel.class);
        binding.newActivityButton.setOnClickListener(v -> dialogSetUp());
        RecyclerView recyclerView = binding.activitiesRecycler;
        Query query = FirebaseFirestore.getInstance().collection("activities");
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
                TextView people = activityBinding.people;
                place.setText(model.getLocation());
                Date d = new Date(model.getTimestamp());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
                activityBinding.date.setText(dateFormat.format(d));
                gameTitle.setText(model.getGame());
                people.setText(String.valueOf(model.getPlayers().size() + "/" + model.getMaxNumberOfPlayers()));
                activityBinding.eventButton.setText("Submit");
                activityBinding.eventButton.setOnClickListener(v -> {
                    homeViewModel.submit(model);
                });
                if(model.getPlayers().contains(FirebaseAuth.getInstance().getUid()) || model.getMaxNumberOfPlayers() == model.getPlayers().size())
                    activityBinding.eventButton.setEnabled(false);
                activityBinding.people.setOnClickListener(v -> {
                    dialogCardSetUp(model);
                });
            }
        };

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
    }

    private void dialogCardSetUp(Event event) {
        DialogEventPlayersBinding dialogEventPlayersBinding = DialogEventPlayersBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).create();
        RecyclerView recyclerView = dialogEventPlayersBinding.players;
        Query query = FirebaseFirestore.getInstance().collection("users").whereIn("userId", event.getPlayers());

        FirestoreRecyclerOptions<UserInfo> options = new FirestoreRecyclerOptions.Builder<UserInfo>().setQuery(query, UserInfo.class).build();
        FirestoreRecyclerAdapter<UserInfo, PlayersViewHolder> adapter = new FirestoreRecyclerAdapter<UserInfo, PlayersViewHolder>(options) {
            @NonNull
            @Override
            public PlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PlayersViewHolder(SinglePlayerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PlayersViewHolder holder, int position, @NonNull UserInfo model) {
                SinglePlayerBinding binding = holder.getBinding();
                binding.name.append(model.getName());
                binding.surname.append(model.getSurname());
                binding.age.append(String.valueOf(model.getAge()));
                binding.game.append(model.getFavouriteGame());
                binding.place.append(model.getFavouritePlace());
            }
        };
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        dialog.setView(dialogEventPlayersBinding.getRoot());
        dialog.show();
    }

    private void showLoginResult(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void dialogSetUp() {
        DialogNewEventBinding binding = DialogNewEventBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).create();
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        binding.date.setOnClickListener(view1 -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> binding.date.setText(materialDatePicker.getHeaderText().toString()));
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTitleText("Select time").build();
        binding.time.setOnClickListener(view1 -> timePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER"));
        timePicker.addOnPositiveButtonClickListener(selection -> binding.time.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute())));
        binding.button.setOnClickListener(view1 -> {
            binding.loading.setVisibility(View.VISIBLE);
            String game = binding.game.getEditText().getText().toString();
            String numberOfPlayers = binding.numberOfPlayers.getEditText().getText().toString();
            String place = binding.place.getEditText().getText().toString();
            String date = binding.date.getText().toString();
            String time = binding.time.getText().toString();
            newEventViewModel.addNewActivity(game, numberOfPlayers, place, date, time);
        });
        newEventViewModel.getSubmissionResult().observe(getViewLifecycleOwner(), submissionResult -> {
            if (submissionResult == Result.NONE) return;
            if (submissionResult == Result.FAILURE) {
                binding.loading.setVisibility(View.GONE);
                showLoginResult(R.string.new_event_failed);
            };
            if(submissionResult == Result.SUCCESS) {
                showLoginResult(R.string.new_event_success);
                NavHostFragment.findNavController(this).navigate(new ActionOnlyNavDirections(R.id.action_navigation_home_to_navigation_dashboard));
                dialog.hide();
            }
        });
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}