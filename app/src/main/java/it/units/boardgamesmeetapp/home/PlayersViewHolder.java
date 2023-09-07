package it.units.boardgamesmeetapp.home;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import it.units.boardgamesmeetapp.databinding.DialogEventPlayersBinding;
import it.units.boardgamesmeetapp.databinding.SingleEventBinding;
import it.units.boardgamesmeetapp.databinding.SinglePlayerBinding;

public class PlayersViewHolder extends RecyclerView.ViewHolder {
    private final SinglePlayerBinding binding;

    public PlayersViewHolder(@NonNull SinglePlayerBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public SinglePlayerBinding getBinding() {
        return binding;
    }
}
