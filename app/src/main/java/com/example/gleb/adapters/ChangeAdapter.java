package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Change;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 07.07.15.
 */
public class ChangeAdapter extends RecyclerView.Adapter<ChangeAdapter.ChangeViewHolder> {
    private SparseBooleanArray selectedItems;
    private List<Change> changes;

    public static class ChangeViewHolder extends RecyclerView.ViewHolder {
        TextView changeTextView;

        ChangeViewHolder(View itemView) {
            super(itemView);
            changeTextView = (TextView)itemView.findViewById(R.id.change);
        }
    }

    public ChangeAdapter(List<Change> changes){
        this.changes = changes;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ChangeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_change, viewGroup, false);
        ChangeViewHolder pvh = new ChangeViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ChangeViewHolder changeViewHolder, int i) {
        changeViewHolder.changeTextView.setText(changes.get(i).changing);
    }

    public void removeData(int position) {
        changes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return changes.size();
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
