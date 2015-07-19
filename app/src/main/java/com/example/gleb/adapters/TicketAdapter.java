package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 08.07.15.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    public static final String TAG = "TAG";
    private SparseBooleanArray selectedItems;
    List<Ticket> tickets;

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView doctorKodTextView;
        TextView registrationKodTextView;
        TextView dateReceptionTextView;

        TicketViewHolder(View itemView) {
            super(itemView);
            doctorKodTextView = (TextView)itemView.findViewById(R.id.doctorKod);
            registrationKodTextView = (TextView)itemView.findViewById(R.id.registrationKod);
            dateReceptionTextView = (TextView)itemView.findViewById(R.id.dateReception);
        }
    }

    public TicketAdapter(List<Ticket> tickets){
        this.tickets = tickets;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ticket, viewGroup, false);
        TicketViewHolder pvh = new TicketViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TicketViewHolder ticketViewHolder, int i) {
        Log.d(TAG, "TicketAdapter " + tickets.get(i).registrationKod);
        ticketViewHolder.doctorKodTextView.setText("Врач " + tickets.get(i).doctorKod);
        ticketViewHolder.registrationKodTextView.setText("Пациент  " + tickets.get(i).registrationKod.toString());
        ticketViewHolder.dateReceptionTextView.setText("Прием " + tickets.get(i).dateReception);
    }

    public void removeData(int position) {
        tickets.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
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
