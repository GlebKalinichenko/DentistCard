package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.RegistrationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 12.06.2015.
 */
public class Registrations extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdRegistrations = null;
    private String[] arrayDateRegistrations = null;
    private int[] arrayParticientKod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        listView = (ListView) findViewById(R.id.registrationListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderregistrations, null);
        listView.addHeaderView(header);
        new Loader().execute();
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/RegistrationScript.php");
            //Fields of table Registrations

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdRegistrations = new int[array.length()];
                arrayDateRegistrations = new String[array.length()];
                arrayParticientKod = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdRegistrations[i] = jObject.getInt("IdRegistration");
                    arrayDateRegistrations[i] = jObject.getString("DateRegistration");
                    arrayParticientKod[i] = jObject.getInt("ParticientKod");

                    Log.d(TAG, arrayDateRegistrations[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateRegistrations;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new RegistrationAdapter(getBaseContext(), arrayDateRegistrations, arrayParticientKod);
            listView.setAdapter(adapter);

        }
    }
}
