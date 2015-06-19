package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.gleb.adapters.ChangeAdapter;
import com.example.gleb.adapters.RecommendationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 13.06.2015.
 */
public class Recommendations extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayTicketKod = null;
    private int[] arrayIdRecommendation = null;
    private int[] arrayDiagnoseKod = null;
    private String[] arrayTherapy = null;
    private String[] arrayComplaints = null;
    private String[] arrayHistoryIllness = null;
    private String[] arrayObjectiveValues = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);

        listView = (ListView) findViewById(R.id.recommendationListView);
        View header = (View) getLayoutInflater().inflate(R.layout.recommendation, null);
        listView.addHeaderView(header);
        new Loader().execute();
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/RecomendationScript.php");
            //Fields of table Recommendation

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdRecommendation = new int[array.length()];
                arrayTicketKod = new int[array.length()];
                arrayDiagnoseKod = new int[array.length()];
                arrayTherapy = new String[array.length()];
                arrayComplaints = new String[array.length()];
                arrayHistoryIllness = new String[array.length()];
                arrayObjectiveValues = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdRecommendation[i] = jObject.getInt("IdRecomendation");
                    arrayTicketKod[i] = jObject.getInt("TicketKod");
                    arrayDiagnoseKod[i] = jObject.getInt("DiagnoseKod");
                    arrayTherapy[i] = jObject.getString("Therapy");
                    arrayHistoryIllness[i] = jObject.getString("HistoryIllness");
                    arrayObjectiveValues[i] = jObject.getString("ObjectiveValues");
                    arrayComplaints[i] = jObject.getString("Complaints");

                    Log.d(TAG, arrayTherapy[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayTherapy;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new RecommendationAdapter(getBaseContext(), arrayTicketKod, arrayDiagnoseKod, arrayTherapy, arrayComplaints, arrayHistoryIllness, arrayObjectiveValues);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}
