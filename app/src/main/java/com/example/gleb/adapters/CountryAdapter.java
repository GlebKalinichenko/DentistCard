package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.tables.Country;
import com.example.gleb.dentistcard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 04.07.15.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder>{
    private SparseBooleanArray selectedItems;

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView countryTextView;

        CountryViewHolder(View itemView) {
            super(itemView);
            countryTextView = (TextView)itemView.findViewById(R.id.country);
        }
    }

    List<Country> countries;

    public CountryAdapter(List<Country> countries){
        this.countries = countries;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_country, viewGroup, false);
        CountryViewHolder pvh = new CountryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CountryViewHolder CountryViewHolder, int i) {
        CountryViewHolder.countryTextView.setText(countries.get(i).country);
    }

    public void removeData(int position) {
        countries.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return countries.size();
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
