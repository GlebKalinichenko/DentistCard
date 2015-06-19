package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.KvalificationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 06.06.2015.
 */
public class Kvalifications extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private String[] arrayKvalifications = null;
    private int[] arrayIdKvalifications = null;
    private KvalificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvalifications);

        listView = (ListView) findViewById(R.id.kvalificationListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderkvalification, null);
        listView.addHeaderView(header);
        new Loader().execute();


    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/KvalificationScript.php");
            //Fields of table Posts

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayKvalifications = new String[array.length()];
                arrayIdKvalifications = new int[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    int id = jObject.getInt("IdKvalification");
                    String kvalification = jObject.getString("Kvalification");
                    arrayIdKvalifications[i] = id;
                    arrayKvalifications[i] = kvalification;
                    Log.d(TAG, arrayKvalifications[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayKvalifications;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new KvalificationAdapter(getBaseContext(), arrayKvalifications);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}

