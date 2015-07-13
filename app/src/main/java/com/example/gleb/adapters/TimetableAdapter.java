package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 09.07.15.
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {
    public static final String TAG = "TAG";
    private SparseBooleanArray selectedItems;
    List<Timetable> timetables;

    public static class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView dateWorkTextView;
        TextView doctorKodTextView;
        TextView changeKodTextView;

        TimetableViewHolder(View itemView) {
            super(itemView);
            doctorKodTextView = (TextView)itemView.findViewById(R.id.doctorKod);
            changeKodTextView = (TextView)itemView.findViewById(R.id.changeKod);
            dateWorkTextView = (TextView)itemView.findViewById(R.id.dateWork);
        }
    }

    public TimetableAdapter(List<Timetable> timetables){
        this.timetables = timetables;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_timetable, viewGroup, false);
        TimetableViewHolder pvh = new TimetableViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TimetableViewHolder timetableViewHolder, int i) {
        timetableViewHolder.doctorKodTextView.setText(timetables.get(i).doctorKod);
        timetableViewHolder.changeKodTextView.setText(timetables.get(i).changeKod);
        timetableViewHolder.dateWorkTextView.setText(timetables.get(i).dateWork);
    }

    public void removeData(int position) {
        timetables.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return timetables.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

}
