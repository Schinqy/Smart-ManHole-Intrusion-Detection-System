package com.luitech.smids.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luitech.smids.Manhole;
import com.luitech.smids.R;

import java.util.List;

public class ManholeAdapter extends RecyclerView.Adapter<ManholeAdapter.ManholeViewHolder> {

    private List<Manhole> manholes;
    private OnManholeClickListener listener;

    public interface OnManholeClickListener {
        void onManholeClick(Manhole manhole);
    }

    public ManholeAdapter(List<Manhole> manholes, OnManholeClickListener listener) {
        this.manholes = manholes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManholeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manhole, parent, false);
        return new ManholeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManholeViewHolder holder, int position) {
        Manhole manhole = manholes.get(position);
        holder.bind(manhole, listener);
    }

    @Override
    public int getItemCount() {
        return manholes.size();
    }

    static class ManholeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewManholeName;

        ManholeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewManholeName = itemView.findViewById(R.id.textViewManholeName);
        }

        void bind(final Manhole manhole, final OnManholeClickListener listener) {
            textViewManholeName.setText(manhole.getName());
            itemView.setOnClickListener(v -> listener.onManholeClick(manhole));
        }
    }

    public void updateManholes(List<Manhole> newManholes) {
        this.manholes = newManholes;
        notifyDataSetChanged();
    }
}