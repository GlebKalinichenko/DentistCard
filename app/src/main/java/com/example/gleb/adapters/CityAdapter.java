package com.example.gleb.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by Gleb on 06.06.2015.
 */
public class CityAdapter extends ArrayAdapter<String> {
    public Context context;
    public String[] cities;
    public String[] countryKod;
    public static final String TAG = "TAG";
    private LayoutInflater mInflater;
    public TextView countryKodTextView;
    public TextView cityTextView;

    public CityAdapter(Context context, String[] cities, String[] countryKod) {
        super(context, R.layout.cities_item_row, cities);
        this.context = context;
        this.cities = cities;
        this.countryKod = countryKod;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, "Adapter " + String.valueOf(countryKod[0]));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.cities_item_row, parent, false);
        } else {
            view = convertView;
        }

        countryKodTextView = (TextView) view.findViewById(R.id.countryKodTextView);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);

        countryKodTextView.setText(String.valueOf(countryKod[position]));
        cityTextView.setText(cities[position]);

        return view;
    }
}
