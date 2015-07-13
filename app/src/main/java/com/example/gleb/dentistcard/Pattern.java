package com.example.gleb.dentistcard;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by Gleb on 05.06.2015.
 */
public abstract class Pattern extends ActionBarActivity {
    protected ListView listView;
    protected ArrayAdapter<String> adapter;
    protected HttpClient client;
    protected HttpPost post;
    protected ImageButton imageButton;

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        protected OnItemClickListener listener;
        private GestureDetector gestureDetector;

        private View childView;
        private int childViewPosition;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            this.gestureDetector = new GestureDetector(context, new GestureListener());
            this.listener = listener;
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            childView = view.findChildViewUnder(event.getX(), event.getY());
            childViewPosition = view.getChildPosition(childView);

            return childView != null && gestureDetector.onTouchEvent(event);
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent event) {
            // Not needed.
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        /**
         * A click listener for items.
         */
        public interface OnItemClickListener {

            /**
             * Called when an item_country is clicked.
             *
             * @param childView View of the item_country that was clicked.
             * @param position  Position of the item_country that was clicked.
             */
            public void onItemClick(View childView, int position);

            /**
             * Called when an item_country is long pressed.
             *
             * @param childView View of the item_country that was long pressed.
             * @param position  Position of the item_country that was long pressed.
             */
            public void onItemLongPress(View childView, int position);

        }

        protected class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                if (childView != null) {
                    listener.onItemClick(childView, childViewPosition);
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                if (childView != null) {
                    listener.onItemLongPress(childView, childViewPosition);
                }
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

        }

    }

}
