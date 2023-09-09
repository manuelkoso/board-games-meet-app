package it.units.boardgamesmeetapp.dashboard.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.units.boardgamesmeetapp.databinding.DialogEventPlayersBinding;
import it.units.boardgamesmeetapp.databinding.SinglePlayerBinding;
import it.units.boardgamesmeetapp.home.PlayersViewHolder;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.UserInfo;

public class EventDialog {

    @NonNull
    private final AlertDialog dialog;

    private EventDialog(@NonNull Fragment fragment, @NonNull Event event) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        DialogEventPlayersBinding dialogEventPlayersBinding = DialogEventPlayersBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        dialog.setView(dialogEventPlayersBinding.getRoot());
        
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment, @NonNull Event event) {
        EventDialog eventDialog = new EventDialog(fragment, event);
        return eventDialog.dialog;
    }

}
