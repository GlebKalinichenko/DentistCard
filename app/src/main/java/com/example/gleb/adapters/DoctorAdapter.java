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
public class DoctorAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    private Context context;
    private String[] FIO;
    private int[] postKod;
    private int[] departmentKod;
    private int[] kvalificationKod;
    private int[] expirience;
    private LayoutInflater mInflater;
    public TextView FIOTextView;
    public TextView postKodTextView;
    public TextView kvalificationKodTextView;
    public TextView departmentKodTextView;
    public TextView expirienceTextView;

    public DoctorAdapter(Context context, String[] FIO, int[] postKod, int[] kvalificationKod, int[] departmentKod, int[] expirience) {
        super(context, R.layout.doctor_item_row, FIO);
        this.context = context;
        this.FIO = FIO;
        this.postKod = postKod;
        this.departmentKod = departmentKod;
        this.kvalificationKod = kvalificationKod;
        this.expirience = expirience;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, "Adapter doctor " + String.valueOf(postKod[0]));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.doctor_item_row, parent, false);
        } else {
            view = convertView;
        }

        FIOTextView = (TextView) view.findViewById(R.id.FIOTextView);
        kvalificationKodTextView = (TextView) view.findViewById(R.id.KvalificationKodTextView);
        departmentKodTextView = (TextView) view.findViewById(R.id.DepartmentKodTextView);
        postKodTextView = (TextView) view.findViewById(R.id.PostKodTextView);
        expirienceTextView = (TextView) view.findViewById(R.id.ExpirienceTextView);

        FIOTextView.setText(FIO[position]);
        kvalificationKodTextView.setText(String.valueOf(kvalificationKod[position]));
        departmentKodTextView.setText(String.valueOf(departmentKod[position]));

        postKodTextView.setText(String.valueOf(postKod[position]));
        expirienceTextView.setText(String.valueOf(expirience[position]));


        return view;
    }
}
