package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by Gleb on 05.06.2015.
 */
abstract class Pattern extends ActionBarActivity {
    protected ListView listView;
    protected ArrayAdapter<String> adapter;
    protected HttpClient client;
    protected HttpPost post;
    protected ImageButton imageButton;

}
