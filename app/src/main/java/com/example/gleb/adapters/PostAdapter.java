package com.example.gleb.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 08.07.15.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private SparseBooleanArray selectedItems;
    private List<Post> posts;

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTextView;

        PostViewHolder(View itemView) {
            super(itemView);
            postTextView = (TextView)itemView.findViewById(R.id.post);
        }
    }

    public PostAdapter(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        PostViewHolder pvh = new PostViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int i) {
        postViewHolder.postTextView.setText(posts.get(i).post);
    }

    public void removeData(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
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



