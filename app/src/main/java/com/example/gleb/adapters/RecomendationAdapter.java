package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Recomendation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 12.07.15.
 */
public class RecomendationAdapter extends RecyclerView.Adapter<RecomendationAdapter.RecomendationViewHolder> {
    public static final String TAG = "TAG";
    private SparseBooleanArray selectedItems;
    List<Recomendation> recomendations;

    public static class RecomendationViewHolder extends RecyclerView.ViewHolder {
        TextView ticketKodTextView;
        TextView diagnoseKodTextView;
        TextView therapyTextView;
        TextView complaintsTextView;
        TextView historyIllnessTextView;
        TextView objectiveValuesTextView;

        RecomendationViewHolder(View itemView) {
            super(itemView);
            ticketKodTextView = (TextView)itemView.findViewById(R.id.ticketKod);
            diagnoseKodTextView = (TextView)itemView.findViewById(R.id.diagnoseKod);
            therapyTextView = (TextView)itemView.findViewById(R.id.therapy);
            complaintsTextView = (TextView)itemView.findViewById(R.id.complaints);
            historyIllnessTextView = (TextView)itemView.findViewById(R.id.historyIllness);
            objectiveValuesTextView = (TextView)itemView.findViewById(R.id.objectiveValues);
        }
    }

    public RecomendationAdapter(List<Recomendation> recomendations){
        this.recomendations = recomendations;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecomendationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recomendation, viewGroup, false);
        RecomendationViewHolder pvh = new RecomendationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RecomendationViewHolder recomendationViewHolder, int i) {
        recomendationViewHolder.ticketKodTextView.setText("Пациент: " + recomendations.get(i).ticketKod);
        recomendationViewHolder.diagnoseKodTextView.setText("Диагнозы: " + recomendations.get(i).diagnoseKod);
        recomendationViewHolder.therapyTextView.setText("Лечение: " + recomendations.get(i).therapy);
        recomendationViewHolder.complaintsTextView.setText("Симптоны: " + recomendations.get(i).complaints);
        recomendationViewHolder.historyIllnessTextView.setText("История болезни: " + recomendations.get(i).historyIllness);
        recomendationViewHolder.objectiveValuesTextView.setText("Объек. данные: " + recomendations.get(i).objectiveValues);
    }

    public void removeData(int position) {
        recomendations.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return recomendations.size();
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
