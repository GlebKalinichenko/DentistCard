package com.example.gleb.insert;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by gleb on 20.06.15.
 */
abstract class InsertPattern extends ActionBarActivity {
    protected static final String TAG = "TAG";
    protected HttpClient client;
    protected HttpPost post;
    protected Button insertButton;

}
