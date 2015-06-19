package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.gleb.adapters.TimetableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 07.06.2015.
 */
public class Timetables extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private TimetableAdapter adapter;
    private int[] arrayIdTimetables = null;
    private String[] arrayDateTimetables = null;
    private int[] arrayDoctorKod = null;
    private int[] arrayChangeKod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetables);

        listView = (ListView) findViewById(R.id.timetableListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadertimetables, null);
        listView.addHeaderView(header);
        new Loader().execute();

    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/TimetableScript.php");
            //Fields of table Timetables

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTimetables = new int[array.length()];
                arrayDoctorKod = new int[array.length()];
                arrayChangeKod = new int[array.length()];
                arrayDateTimetables = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdTimetables[i] = jObject.getInt("IdTimetable");
                    arrayDoctorKod[i] = jObject.getInt("DoctorKod");
                    arrayChangeKod[i] = jObject.getInt("ChangeKod");
                    arrayDateTimetables[i] = jObject.getString("DateWork");


                    Log.d(TAG, arrayDateTimetables[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateTimetables;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new TimetableAdapter(getBaseContext(), arrayDateTimetables, arrayDoctorKod, arrayChangeKod);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}
