package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.ParticientAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 07.06.2015.
 */
public class Diagnoses extends Pattern {
    public static final String TAG = "TAG";
    private int[] arrayIdDiagnoses = null;
    private String[] arrayDiagnoses = null;
    private DatabaseRequest request = new DatabaseRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnoses);

        listView = (ListView) findViewById(R.id.diagnoseListView);
        new Loader().execute();
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DiagnoseScript.php");
            //Fields of table Doctors

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdDiagnoses = new int[array.length()];
                arrayDiagnoses = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdDiagnoses[i] = jObject.getInt("IdDiagnose");
                    arrayDiagnoses[i] = jObject.getString("Diagnose");

                    Log.d(TAG, arrayDiagnoses[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDiagnoses;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            //adapter = new ParticientAdapter(getBaseContext(), arrayFIO, arrayAddreses, arrayCityKod, arrayPhoneNumber, arrayFIOParent, arrayDateBorn);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }



}
