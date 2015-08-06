package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Registration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 08.07.15.
 */
public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {
    private SparseBooleanArray selectedItems;
    List<Registration> registrations;

    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView particientKodTextView;
        TextView dateRegistrationTextView;

        RegistrationViewHolder(View itemView) {
            super(itemView);
            particientKodTextView = (TextView)itemView.findViewById(R.id.particientKod);
            dateRegistrationTextView = (TextView)itemView.findViewById(R.id.dateRegistration);
        }
    }

    public RegistrationAdapter(List<Registration> registrations){
        this.registrations = registrations;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RegistrationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_registration, viewGroup, false);
        RegistrationViewHolder pvh = new RegistrationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RegistrationViewHolder registrationViewHolder, int i) {
        registrationViewHolder.particientKodTextView.setText("Пациент: " + registrations.get(i).particientKod);
        registrationViewHolder.dateRegistrationTextView.setText("Дата регистрации: " + registrations.get(i).dateRegistration);
    }

    public void removeData(int position) {
        registrations.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return registrations.size();
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
