package com.example.gleb.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by Gleb on 13.06.2015.
 */
public class TicketAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public String[] arrayDoctorKod;
    public String[] arrayRegistrationKod;
    public String[] arrayDateReception;
    private LayoutInflater mInflater;
    public TextView doctorKodTextView;
    public TextView registrationKodTextView;
    public TextView dateReceptionTextView;

    public TicketAdapter(Context context, String[] arrayDoctorKod, String[] arrayRegistrationKod, String[] arrayDateReception) {
        super(context, R.layout.ticket_item_row, arrayDateReception);
        this.context = context;
        this.arrayDoctorKod = arrayDoctorKod;
        this.arrayRegistrationKod = arrayRegistrationKod;
        this.arrayDateReception = arrayDateReception;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, arrayDateReception[0]);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.ticket_item_row, parent, false);
        } else {
            view = convertView;
        }

        doctorKodTextView = (TextView) view.findViewById(R.id.doctorKodTextView);
        registrationKodTextView = (TextView) view.findViewById(R.id.registrationKodTextView);
        dateReceptionTextView = (TextView) view.findViewById(R.id.dateReceptionTextView);
        doctorKodTextView.setText(String.valueOf(arrayDoctorKod[position]));
        registrationKodTextView.setText(String.valueOf(arrayRegistrationKod[position]));
        dateReceptionTextView.setText(arrayDateReception[position]);

        return view;
    }
}
