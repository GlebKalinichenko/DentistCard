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
public class DepartmentsDoctorsAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public String[] departments;
    public Context context;
    public TextView departmentTextView;
    private LayoutInflater mInflater;

    public DepartmentsDoctorsAdapter(Context context, String[] departments) {
        super(context, R.layout.department_item_row, departments);
        this.context = context;
        this.departments = departments;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.department_item_row, parent, false);
        } else {
            view = convertView;
        }

        departmentTextView = (TextView) view.findViewById(R.id.departmentTextView);
        departmentTextView.setText(String.valueOf(departments[position]));

        return view;
    }


}
