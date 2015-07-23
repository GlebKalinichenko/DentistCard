package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 23.07.15.
 */
public class AutoresationAdapter extends RecyclerView.Adapter<AutoresationAdapter.AutoresationViewHolder>{
    private SparseBooleanArray selectedItems;
    public String[] from;
    public String[] subject;
    public String[] content;

    public static class AutoresationViewHolder extends RecyclerView.ViewHolder {
        TextView fromTextView;
        TextView subjectTextView;
        TextView contentTextView;

        AutoresationViewHolder(View itemView) {
            super(itemView);
            fromTextView = (TextView)itemView.findViewById(R.id.fromTextView);
            subjectTextView = (TextView)itemView.findViewById(R.id.subjectTextView);
            contentTextView = (TextView)itemView.findViewById(R.id.contentTextView);
        }
    }

    public AutoresationAdapter(String[] from, String[] subject, String[] content){
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AutoresationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_autoresation, viewGroup, false);
        AutoresationViewHolder pvh = new AutoresationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(AutoresationViewHolder autoresationViewHolder, int i) {
        autoresationViewHolder.fromTextView.setText(from[i]);
        autoresationViewHolder.subjectTextView.setText(subject[i]);
        autoresationViewHolder.contentTextView.setText(content[i]);
    }

    @Override
    public int getItemCount() {
        return from.length;
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
