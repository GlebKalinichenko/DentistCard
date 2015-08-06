package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Particient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 12.07.15.
 */
public class ParticientAdapter extends RecyclerView.Adapter<ParticientAdapter.ParticientViewHolder> {
    public static final String TAG = "TAG";
    private SparseBooleanArray selectedItems;
    List<Particient> particients;

    public static class ParticientViewHolder extends RecyclerView.ViewHolder {
        TextView FIOTextView;
        TextView addressTextView;
        TextView cityKodTextView;
        TextView phoneNumberTextView;
        TextView dateBornTextView;
        TextView fioParentTextView;

        ParticientViewHolder(View itemView) {
            super(itemView);
            FIOTextView = (TextView)itemView.findViewById(R.id.FIO);
            addressTextView = (TextView)itemView.findViewById(R.id.address);
            cityKodTextView = (TextView)itemView.findViewById(R.id.cityKod);
            phoneNumberTextView = (TextView)itemView.findViewById(R.id.phoneNumber);
            dateBornTextView = (TextView)itemView.findViewById(R.id.dateBorn);
            fioParentTextView = (TextView)itemView.findViewById(R.id.fioParent);
        }
    }

    public ParticientAdapter(List<Particient> particients){
        this.particients = particients;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ParticientViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_particient, viewGroup, false);
        ParticientViewHolder pvh = new ParticientViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ParticientViewHolder particientViewHolder, int i) {
        particientViewHolder.FIOTextView.setText("ФИО: " + particients.get(i).FIO);
        particientViewHolder.addressTextView.setText("Адресс: " + particients.get(i).address);
        particientViewHolder.cityKodTextView.setText("Город: " + particients.get(i).cityKod);
        particientViewHolder.phoneNumberTextView.setText("Телефон: " + particients.get(i).phoneNumber);
        particientViewHolder.dateBornTextView.setText("Дата рождения: " + particients.get(i).dateBorn);
        particientViewHolder.fioParentTextView.setText("Родитель: " + particients.get(i).FIOParent);
    }

    public void removeData(int position) {
        particients.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return particients.size();
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
