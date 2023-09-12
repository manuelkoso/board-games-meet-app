package it.units.boardgamesmeetapp.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.units.boardgamesmeetapp.databinding.SingleEventBinding;

public class EventViewHolder extends RecyclerView.ViewHolder {

    private final SingleEventBinding binding;

    public EventViewHolder(@NonNull SingleEventBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public SingleEventBinding getBinding() {
        return binding;
    }
}
