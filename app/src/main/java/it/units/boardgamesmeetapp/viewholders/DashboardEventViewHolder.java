package it.units.boardgamesmeetapp.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.units.boardgamesmeetapp.databinding.DashboardEventBinding;

public class DashboardEventViewHolder extends RecyclerView.ViewHolder {

    private final DashboardEventBinding binding;

    public DashboardEventViewHolder(@NonNull DashboardEventBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public DashboardEventBinding getBinding() {
        return binding;
    }
}