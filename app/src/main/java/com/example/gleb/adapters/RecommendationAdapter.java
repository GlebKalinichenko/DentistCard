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
public class RecommendationAdapter extends ArrayAdapter<String> {
    public static final String TAG = "TAG";
    public Context context;
    public int[] arrayTicketKod;
    public int[] arrayDiagnoseKod;
    public String[] arrayTherapy;
    public String[] arrayComplaints;
    public String[] arrayHistoryIllness;
    public String[] arrayObjectiveValues;
    private LayoutInflater mInflater;
    public TextView ticketKodTextView;
    public TextView diagnoseKodTextView;
    public TextView therapyTextView;
    public TextView complaintsTextView;
    public TextView historyIlnessTextView;
    public TextView objectiveValuesTextView;

    public RecommendationAdapter(Context context, int[] arrayTicketKod, int[] arrayDiagnoseKod, String[] arrayTherapy, String[] arrayComplaints, String[] arrayHistoryIllness, String[] arrayObjectiveValues) {
        super(context, R.layout.recommendation_item_row, arrayTherapy);
        this.context = context;
        this.arrayTicketKod = arrayTicketKod;
        this.arrayDiagnoseKod = arrayDiagnoseKod;
        this.arrayTherapy = arrayTherapy;
        this.arrayComplaints = arrayComplaints;
        this.arrayHistoryIllness = arrayHistoryIllness;
        this.arrayObjectiveValues = arrayObjectiveValues;
        this.mInflater = LayoutInflater.from(context);
        Log.d(TAG, "Therapy " + String.valueOf(arrayTherapy[0]));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.recommendation_item_row, parent, false);
        } else {
            view = convertView;
        }

        ticketKodTextView = (TextView) view.findViewById(R.id.ticketKodTextView);
        diagnoseKodTextView = (TextView) view.findViewById(R.id.diagnoseKodTextView);
        therapyTextView = (TextView) view.findViewById(R.id.therapyTextView);
        complaintsTextView = (TextView) view.findViewById(R.id.complaintsTextView);
        historyIlnessTextView = (TextView) view.findViewById(R.id.historyIllnessTextView);
        objectiveValuesTextView = (TextView) view.findViewById(R.id.objectiveValuesTextView);

        ticketKodTextView.setText(String.valueOf(arrayTicketKod[position]));
        diagnoseKodTextView.setText(String.valueOf(arrayDiagnoseKod[position]));
        therapyTextView.setText(arrayTherapy[position]);
        complaintsTextView.setText(arrayComplaints[position]);
        historyIlnessTextView.setText(arrayHistoryIllness[position]);
        objectiveValuesTextView.setText(arrayObjectiveValues[position]);

        return view;
    }
}
