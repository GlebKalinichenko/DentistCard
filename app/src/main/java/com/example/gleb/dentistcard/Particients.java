package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.DoctorAdapter;
import com.example.gleb.adapters.ParticientAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertParticient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 07.06.2015.
 */
public class Particients extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdParticients = null;
    private String[] arrayFIO = null;
    private String[] arrayAddreses = null;
    private int[] arrayCityKod = null;
    private String[] arrayPhoneNumber = null;
    private String[] arrayFIOParent = null;
    private String[] arrayDateBorn = null;
    private ParticientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particients);

        listView = (ListView) findViewById(R.id.particientListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderparticients, null);
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
            Intent intent = new Intent(this, InsertParticient.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ParticientScript.php");
            //Fields of table Doctors

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdParticients = new int[array.length()];
                arrayFIO = new String[array.length()];
                arrayAddreses = new String[array.length()];
                arrayCityKod = new int[array.length()];
                arrayDateBorn = new String[array.length()];
                arrayFIOParent = new String[array.length()];
                arrayPhoneNumber = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdParticients[i] = jObject.getInt("IdParticient");
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayAddreses[i] = jObject.getString("Address");
                    arrayDateBorn[i] = jObject.getString("DateBorn");
                    arrayPhoneNumber[i] = jObject.getString("PhoneNumber");
                    arrayFIOParent[i] = jObject.getString("FIOParent");
                    arrayCityKod[i] = jObject.getInt("CityKod");

                    Log.d(TAG, arrayFIO[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFIO;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new ParticientAdapter(getBaseContext(), arrayFIO, arrayAddreses, arrayCityKod, arrayPhoneNumber, arrayFIOParent, arrayDateBorn);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}
