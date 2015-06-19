package com.example.gleb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by Gleb on 13.06.2015.
 */
public class RegistrationAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public String[] arrayDateRegistration;
    public int[] arrayParticientKod;
    public Context context;
    public TextView dateRegistrationTextView;
    public TextView particientKodTextView;
    private LayoutInflater mInflater;

    public RegistrationAdapter(Context context, String[] arrayDateRegistration, int[] arrayParticientKod) {
        super(context, R.layout.registration_item_row, arrayDateRegistration);
        this.context = context;
        this.arrayDateRegistration = arrayDateRegistration;
        this.arrayParticientKod = arrayParticientKod;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.registration_item_row, parent, false);
        } else {
            view = convertView;
        }

        dateRegistrationTextView = (TextView) view.findViewById(R.id.dateRegistrationTextView);
        particientKodTextView = (TextView) view.findViewById(R.id.particientKodTextView);
        dateRegistrationTextView.setText(arrayDateRegistration[position]);
        particientKodTextView.setText(String.valueOf(arrayParticientKod[position]));

        return view;
    }


}
