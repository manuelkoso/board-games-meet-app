package it.units.boardgamesmeetapp.dashboard;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.units.boardgamesmeetapp.databinding.SingleActivityBinding;

public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private final SingleActivityBinding binding;

    public ActivityViewHolder(@NonNull SingleActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public SingleActivityBinding getBinding() {
        return binding;
    }
}
