package com.example.gleb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;

/**
 * Created by gleb on 20.06.15.
 */
public class PostAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public String[] arrayPost;
    private LayoutInflater mInflater;
    public TextView postTextView;


    public PostAdapter(Context context, String[] arrayPost) {
        super(context, R.layout.post_item_row, arrayPost);
        this.context = context;
        this.arrayPost = arrayPost;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.post_item_row, parent, false);
        } else {
            view = convertView;
        }

        postTextView = (TextView) view.findViewById(R.id.postTextView);
        postTextView.setText(arrayPost[position]);

        return view;
    }


}
