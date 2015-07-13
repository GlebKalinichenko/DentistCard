package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Diagnose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 08.07.15.
 */
public class DiagnoseAdapter extends RecyclerView.Adapter<DiagnoseAdapter.DiagnoseViewHolder> {
    private SparseBooleanArray selectedItems;
    List<Diagnose> diagnoses;

    public static class DiagnoseViewHolder extends RecyclerView.ViewHolder {
        TextView diagnoseTextView;

        DiagnoseViewHolder(View itemView) {
            super(itemView);
            diagnoseTextView = (TextView)itemView.findViewById(R.id.diagnose);
        }
    }


    public DiagnoseAdapter(List<Diagnose> diagnoses){
        this.diagnoses = diagnoses;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DiagnoseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_diagnose, viewGroup, false);
        DiagnoseViewHolder pvh = new DiagnoseViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DiagnoseViewHolder diagnoseViewHolder, int i) {
        diagnoseViewHolder.diagnoseTextView.setText(diagnoses.get(i).diagnose);
    }

    public void removeData(int position) {
        diagnoses.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return diagnoses.size();
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
