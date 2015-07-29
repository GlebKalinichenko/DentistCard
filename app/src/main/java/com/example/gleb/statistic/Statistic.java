package com.example.gleb.statistic;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.tables.Ticket;
import com.mikepenz.materialdrawer.Drawer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by gleb on 25.07.15.
 */
public class Statistic extends ActionBarActivity {
    public static final String TAG = "TAG";
    public static final String TICKET = "Ticket";
    public TextView allTicketTextView;
    public TextView fromDateTextView;
    public TextView toDateTextView;
    public TextView dateTextView;

    protected HttpClient client;
    protected HttpPost post;

    public String fullName;
    public String workTicket;
    public String workTicketDate;

    public Spinner dateSpinner;
    public String[] dates;

    public String fromDate;
    public String toDate;
    public Button spinner;

    public ImageButton addImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);

        fullName = getIntent().getStringExtra(Statistic.TICKET);
        Log.d(TAG, "Statistic " + fullName);

        allTicketTextView = (TextView) findViewById(R.id.allTicketTextView);
        fromDateTextView = (TextView) findViewById(R.id.fromDateTextView);
        toDateTextView = (TextView) findViewById(R.id.toDateTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StatisterDate().execute();
            }
        });

        dates = new String[]{"from", "to"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dates);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dateSpinner.setAdapter(adapter);
//        Log.d(TAG, "Spinner " + dateSpinner.getPrompt());

        spinner = (Button) findViewById(R.id.btnSpinnerPlanets);
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Statistic.this, android.R.layout.simple_spinner_dropdown_item, dates);
                new AlertDialog.Builder(Statistic.this).setTitle("the prompt").setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //((Button) findViewById(R.id.btnSpinnerPlanets)).setText(dates[which]);
                        if (dates[which].equals("to")) {
                            showDialog(1);
                        }
                        else{
                            if (dates[which].equals("from")) {
                                showDialog(0);
                            }
                        }
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });

        new Statister().execute();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            Calendar calendar = Calendar.getInstance();
            return new DatePickerDialog(this, fromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        else{
            if (id == 1) {
                Calendar calendar = Calendar.getInstance();
                return new DatePickerDialog(this, toDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            fromDate = arg1 + "-" + arg2 + "-" + arg3;
            fromDateTextView.setText(fromDate);
            Log.d(TAG, "Date change from " + fromDate);
        }
    };

    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            toDate = arg1 + "-" + arg2 + "-" + arg3;
            toDateTextView.setText(toDate);
            Log.d(TAG, "Date change to " + toDate);

        }
    };

    public class Statister extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectWorkTicket.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);

                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the
                    Log.i("Read from Server", in.toString());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "Sb " + sb.toString());
                    if (!sb.toString().equals("")) {
                        JSONArray array = new JSONArray(sb.toString());

                        try{
                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                workTicket = jObject.getString("Ticket");

                                Log.d(TAG, workTicket);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {
            allTicketTextView.setText(workTicket);
        }
    }

    public class StatisterDate extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectTicketDate.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);
                json.put("FROM", fromDate);
                json.put("TO", toDate);

                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the
                    Log.i("Read from Server", in.toString());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "Sb " + sb.toString());
                    if (!sb.toString().equals("")) {
                        JSONArray array = new JSONArray(sb.toString());

                        try{
                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                workTicketDate = jObject.getString("Ticket");

                                Log.d(TAG, workTicketDate);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {
            dateTextView.setText(workTicketDate);
        }
    }


}
