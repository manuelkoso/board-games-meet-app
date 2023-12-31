package it.units.boardgamesmeetapp.dialogs;

import android.view.LayoutInflater;
import android.view.View;
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

import it.units.boardgamesmeetapp.database.FirebaseConfig;
import it.units.boardgamesmeetapp.databinding.DialogEventPlayersBinding;
import it.units.boardgamesmeetapp.databinding.SinglePlayerBinding;
import it.units.boardgamesmeetapp.models.UserInfoView;
import it.units.boardgamesmeetapp.viewholders.PlayersViewHolder;
import it.units.boardgamesmeetapp.models.Event;
import it.units.boardgamesmeetapp.models.User;

public class PlayersDialog {

    @NonNull
    private final AlertDialog dialog;

    private PlayersDialog(@NonNull Fragment fragment, @NonNull Event event) {
        this.dialog = new MaterialAlertDialogBuilder(fragment.requireContext()).create();
        DialogEventPlayersBinding dialogEventPlayersBinding = DialogEventPlayersBinding.inflate(LayoutInflater.from(fragment.requireContext()));
        
        RecyclerView recyclerView = dialogEventPlayersBinding.players;
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConfig.USERS_REFERENCE).whereIn(FirebaseConfig.USERS_ID_REFERENCE, event.getPlayers());

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        FirestoreRecyclerAdapter<User, PlayersViewHolder> adapter = new FirestoreRecyclerAdapter<User, PlayersViewHolder>(options) {
            @NonNull
            @Override
            public PlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PlayersViewHolder(SinglePlayerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PlayersViewHolder holder, int position, @NonNull User model) {
                SinglePlayerBinding binding = holder.getBinding();
                UserInfoView userInfoView = new UserInfoView(model);

                binding.name.append(userInfoView.getNameAndSurname());
                binding.age.append(userInfoView.getAge());
                binding.game.append(userInfoView.getFavouriteGame());
                binding.place.append(userInfoView.getFavouritePlace());
                if (model.getId().equals(event.getOwnerId())) {
                    binding.ownerBadge.setVisibility(View.VISIBLE);
                } else {
                    binding.ownerBadge.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.VERTICAL, false);
        
        recyclerView.setLayoutManager(layoutManager);
        adapter.startListening();
        dialogEventPlayersBinding.dialogClose.setOnClickListener(v -> dialog.dismiss());
        dialog.setView(dialogEventPlayersBinding.getRoot());
    }

    public static @NonNull AlertDialog getInstance(@NonNull Fragment fragment, @NonNull Event event) {
        PlayersDialog playersDialog = new PlayersDialog(fragment, event);
        return playersDialog.dialog;
    }

}
