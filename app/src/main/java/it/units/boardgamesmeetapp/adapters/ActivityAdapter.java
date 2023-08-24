package it.units.boardgamesmeetapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.units.boardgamesmeetapp.databinding.SingleActivityBinding;
import it.units.boardgamesmeetapp.models.Activity;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private final List<Activity> userActivities;

    public ActivityAdapter(List<Activity> userActivities) {
        this.userActivities = userActivities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(SingleActivityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SingleActivityBinding binding = holder.getBinding();
        TextView textView = binding.gameTitle;
        textView.setText(userActivities.get(position).getGame().toString());
    }

    @Override
    public int getItemCount() {
        return userActivities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final SingleActivityBinding binding;

        public ViewHolder(@NonNull SingleActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public SingleActivityBinding getBinding() {
            return binding;
        }
    }




}
