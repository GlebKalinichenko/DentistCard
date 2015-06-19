package com.example.gleb.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by Gleb on 05.06.2015.
 */
public class CountryAdapter extends ArrayAdapter<String> {
    public Context context;
    public String[] values;
    public TextView countryTextView;
    public static final String TAG = "TAG";
    private LayoutInflater mInflater;

    public CountryAdapter(Context context, String[] values) {
        super(context, R.layout.countries_item_row, values);
        this.context = context;
        this.values = values;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, values[0]);
    }

    @Override
         public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.countries_item_row, parent, false);
        } else {
            view = convertView;
        }

        countryTextView = (TextView) view.findViewById(R.id.countryTextView);



        countryTextView.setText(values[position]);

        return view;
    }
}
