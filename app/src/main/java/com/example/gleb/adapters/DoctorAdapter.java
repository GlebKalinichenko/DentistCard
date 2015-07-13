package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Doctor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 10.07.15.
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    public static final String TAG = "TAG";
    private SparseBooleanArray selectedItems;
    List<Doctor> doctors;

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView FIOTextView;
        TextView postKodTextView;
        TextView kvalificationKodTextView;
        TextView departmentKodTextView;
        TextView expirienceTextView;

        DoctorViewHolder(View itemView) {
            super(itemView);
            FIOTextView = (TextView)itemView.findViewById(R.id.FIO);
            postKodTextView = (TextView)itemView.findViewById(R.id.postKod);
            kvalificationKodTextView = (TextView)itemView.findViewById(R.id.kvalificationKod);
            departmentKodTextView = (TextView)itemView.findViewById(R.id.departmentKod);
            expirienceTextView = (TextView)itemView.findViewById(R.id.expirience);
        }
    }

    public DoctorAdapter(List<Doctor> doctors){
        this.doctors = doctors;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_doctor, viewGroup, false);
        DoctorViewHolder pvh = new DoctorViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DoctorViewHolder doctorViewHolder, int i) {
        doctorViewHolder.FIOTextView.setText(doctors.get(i).FIO);
        doctorViewHolder.postKodTextView.setText(doctors.get(i).postKod);
        doctorViewHolder.kvalificationKodTextView.setText(doctors.get(i).kvalificationKod + " квалификация");
        doctorViewHolder.departmentKodTextView.setText(doctors.get(i).departmentKod);
        doctorViewHolder.expirienceTextView.setText(String.valueOf(doctors.get(i).expirience + " лет опыта"));
    }

    public void removeData(int position) {
        doctors.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
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
