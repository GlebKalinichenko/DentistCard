package com.example.gleb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by gleb on 19.06.15.
 */
public class DiagnoseAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public String[] diagnoses;
    public TextView diagnoseTextView;
    private LayoutInflater mInflater;

    public DiagnoseAdapter(Context context, String[] diagnoses) {
        super(context, R.layout.diagnose_item_row, diagnoses);
        this.context = context;
        this.diagnoses = diagnoses;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // ������� itemView �� ��������� layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.diagnose_item_row, parent, false);
        } else {
            view = convertView;
        }

        diagnoseTextView = (TextView) view.findViewById(R.id.diagnoseTextView);
        diagnoseTextView.setText(diagnoses[position]);

        return view;
    }
}
