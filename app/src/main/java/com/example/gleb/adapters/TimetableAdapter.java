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
 * Created by Gleb on 07.06.2015.
 */
public class TimetableAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public String[] dateWork;
    public int[] doctorsKod;
    public int[] changesKod;
    private LayoutInflater mInflater;
    public TextView dateWorkTextView;
    public TextView doctorKodTextView;
    public TextView changeKodTextView;


    public TimetableAdapter(Context context, String[] dateWork, int[] doctorsKod, int[] changesKod) {
        super(context, R.layout.timetable_item_row, dateWork);
        this.context = context;
        this.dateWork = dateWork;
        this.changesKod = changesKod;
        this.doctorsKod = doctorsKod;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, "Adapter timetable" + String.valueOf(dateWork[0]));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.timetable_item_row, parent, false);
        } else {
            view = convertView;
        }

        dateWorkTextView = (TextView) view.findViewById(R.id.dateWorkTextView);
        doctorKodTextView = (TextView) view.findViewById(R.id.doctorKodTextView);
        changeKodTextView = (TextView) view.findViewById(R.id.changeKodTextView);

        dateWorkTextView.setText(dateWork[position]);
        doctorKodTextView.setText(String.valueOf(doctorsKod[position]));
        changeKodTextView.setText(String.valueOf(changesKod[position]));

        return view;
    }
}
