package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Kvalification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 08.07.15.
 */
public class KvalificationAdapter extends RecyclerView.Adapter<KvalificationAdapter.KvalificationViewHolder> {
    private SparseBooleanArray selectedItems;
    private List<Kvalification> kvalifications;

    public static class KvalificationViewHolder extends RecyclerView.ViewHolder {
        TextView kvalificationTextView;

        KvalificationViewHolder(View itemView) {
            super(itemView);
            kvalificationTextView = (TextView)itemView.findViewById(R.id.kvalification);
        }
    }

    public KvalificationAdapter(List<Kvalification> kvalifications){
        this.kvalifications = kvalifications;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public KvalificationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kvalification, viewGroup, false);
        KvalificationViewHolder pvh = new KvalificationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(KvalificationViewHolder kvalificationViewHolder, int i) {
        kvalificationViewHolder.kvalificationTextView.setText(kvalifications.get(i).kvalification);
    }

    public void removeData(int position) {
        kvalifications.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return kvalifications.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

}
