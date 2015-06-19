package com.example.gleb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by Gleb on 07.06.2015.
 */
public class KvalificationAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    private String[] kvalifications;
    public TextView kvalificationTextView;
    private LayoutInflater mInflater;

    public KvalificationAdapter(Context context, String[] kvalifications) {
        super(context, R.layout.kvalification_item_row, kvalifications);
        this.context = context;
        this.kvalifications = kvalifications;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
         public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // ������� itemView �� ��������� layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.kvalification_item_row, parent, false);
        } else {
            view = convertView;
        }

        kvalificationTextView = (TextView) view.findViewById(R.id.kvalificationTextView);
        kvalificationTextView.setText(kvalifications[position]);

        return view;
    }
}
