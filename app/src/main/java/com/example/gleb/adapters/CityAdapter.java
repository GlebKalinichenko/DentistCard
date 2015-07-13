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
 * Created by gleb on 05.07.15.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>{
    private SparseBooleanArray selectedItems;
    List<City> cities;

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView countryKodTextView;
        TextView cityTextView;

        CityViewHolder(View itemView) {
            super(itemView);
            countryKodTextView = (TextView)itemView.findViewById(R.id.countryKod);
            cityTextView = (TextView)itemView.findViewById(R.id.city);
        }
    }

    public CityAdapter(List<City> cities){
        this.cities = cities;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_city, viewGroup, false);
        CityViewHolder pvh = new CityViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CityViewHolder CountryViewHolder, int i) {
        CountryViewHolder.countryKodTextView.setText(cities.get(i).countryKod);
        CountryViewHolder.cityTextView.setText(cities.get(i).city);
    }

    public void removeData(int position) {
        cities.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return cities.size();
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
