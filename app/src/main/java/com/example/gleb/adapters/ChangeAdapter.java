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
public class ChangeAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public TextView changeTextView;
    public String[] changes;
    private LayoutInflater mInflater;

    public ChangeAdapter(Context context, String[] changes) {
        super(context, R.layout.changes_item_row, changes);
        this.context = context;
        this.changes = changes;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.changes_item_row, parent, false);
        } else {
            view = convertView;
        }

        changeTextView = (TextView) view.findViewById(R.id.changeTextView);
        changeTextView.setText(changes[position]);

        return view;
    }


}
