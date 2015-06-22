package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.example.gleb.adapters.RegistrationAdapter;
import com.example.gleb.adapters.TicketAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertTicket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 13.06.2015.
 */
public class Tickets extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdTickets = null;
    private int[] arrayDoctorKod = null;
    private int[] arrayRegistrationKod = null;
    private String[] arrayDateReception = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets);

        listView = (ListView) findViewById(R.id.ticketListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadertickets, null);
        listView.addHeaderView(header);
        new Loader().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dentist_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addRecord) {
            Intent intent = new Intent(this, InsertTicket.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/TicketScript.php");
            //Fields of table Registrations

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTickets = new int[array.length()];
                arrayDoctorKod = new int[array.length()];
                arrayRegistrationKod = new int[array.length()];
                arrayDateReception = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdTickets[i] = jObject.getInt("IdTicket");
                    arrayDoctorKod[i] = jObject.getInt("DoctorKod");
                    arrayRegistrationKod[i] = jObject.getInt("RegistrationKod");
                    arrayDateReception[i] = jObject.getString("DateReception");

                    Log.d(TAG, arrayDateReception[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateReception;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new TicketAdapter(getBaseContext(), arrayDoctorKod, arrayRegistrationKod, arrayDateReception);
            listView.setAdapter(adapter);

        }
    }
}
