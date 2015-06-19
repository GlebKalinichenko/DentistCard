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
public class ParticientAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    private Context context;
    private String[] arrayFIO;
    private String[] arrayAddress;
    private int[] arrayCityKod;
    private String[] arrayPhoneNumber;
    private String[] arrayFIOParent;
    private String[] arrayDateBorn;
    private LayoutInflater mInflater;
    public TextView FIOTextView;
    public TextView addressTextView;
    public TextView cityKodTextView;
    public TextView phoneNumberTextView;
    public TextView FIOParentTextView;
    public TextView DateBornTextView;

    public ParticientAdapter(Context context, String[] arrayFIO, String[] arrayAddress, int[] arrayCityKod, String[] arrayPhoneNumber,
        String[] arrayFIOParent, String[] arrayDateBorn) {
        super(context, R.layout.particient_item_row, arrayFIO);
        this.context = context;
        this.arrayFIO = arrayFIO;
        this.arrayAddress = arrayAddress;
        this.arrayCityKod = arrayCityKod;
        this.arrayPhoneNumber = arrayPhoneNumber;
        this.arrayFIOParent = arrayFIOParent;
        this.arrayDateBorn = arrayDateBorn;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;  // создаем itemView из заданного layout
        if (convertView == null) {
            view = mInflater.inflate(R.layout.particient_item_row, parent, false);
        } else {
            view = convertView;
        }

        FIOTextView = (TextView) view.findViewById(R.id.FIOTextView);
        addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        cityKodTextView = (TextView) view.findViewById(R.id.cityKodTextView);
        phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumberTextView);
        FIOParentTextView = (TextView) view.findViewById(R.id.FIOParentKodTextView);
        DateBornTextView = (TextView) view.findViewById(R.id.dateBornTextView);

        FIOTextView.setText(arrayFIO[position]);
        addressTextView.setText(arrayAddress[position]);
        cityKodTextView.setText(String.valueOf(arrayCityKod[position]));
        phoneNumberTextView.setText(arrayPhoneNumber[position]);
        FIOParentTextView.setText(arrayFIOParent[position]);
        DateBornTextView.setText(arrayDateBorn[position]);

        return view;
    }
}
