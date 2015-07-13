package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.DepartmentDoctor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 07.07.15.
 */
public class DepartmentDoctorAdapter extends RecyclerView.Adapter<DepartmentDoctorAdapter.DepartmentDoctorViewHolder> {
    private SparseBooleanArray selectedItems;
    private List<DepartmentDoctor> departmentDoctors;

    public static class DepartmentDoctorViewHolder extends RecyclerView.ViewHolder {
        TextView departmentTextView;

        DepartmentDoctorViewHolder(View itemView) {
            super(itemView);
            departmentTextView = (TextView)itemView.findViewById(R.id.departmentDoctor);
        }
    }

    public DepartmentDoctorAdapter(List<DepartmentDoctor> departmentDoctors){
        this.departmentDoctors = departmentDoctors;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DepartmentDoctorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_departmentdoctor, viewGroup, false);
        DepartmentDoctorViewHolder pvh = new DepartmentDoctorViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DepartmentDoctorViewHolder CountryViewHolder, int i) {
        CountryViewHolder.departmentTextView.setText(departmentDoctors.get(i).departmentDoctor);
    }

    public void removeData(int position) {
        departmentDoctors.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return departmentDoctors.size();
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
