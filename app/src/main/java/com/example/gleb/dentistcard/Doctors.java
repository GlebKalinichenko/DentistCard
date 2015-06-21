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
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertDoctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 06.06.2015.
 */
public class Doctors extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private String[] arrayFIO = null;
    private int[] arrayIdDoctors = null;
    private int[] arrayPostKod = null;
    private int[] arrayDepartmentKod = null;
    private int[] arrayKvalificationKod = null;
    private int[] arrayExpiriences = null;
    private DoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors);

        listView = (ListView) findViewById(R.id.doctorListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderdoctors, null);
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
            Intent intent = new Intent(this, InsertDoctor.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DoctorScript.php");
            //Fields of table Doctors


            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayFIO = new String[array.length()];
                arrayIdDoctors = new int[array.length()];
                arrayDepartmentKod = new int[array.length()];
                arrayKvalificationKod = new int[array.length()];
                arrayExpiriences = new int[array.length()];
                arrayPostKod = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdDoctors[i] = jObject.getInt("IdDoctor");
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayDepartmentKod[i] = jObject.getInt("DepartmentKod");
                    arrayKvalificationKod[i] = jObject.getInt("KvalificationKod");
                    arrayPostKod[i] = jObject.getInt("PostKod");
                    arrayExpiriences[i] = jObject.getInt("Expirience");


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
            Log.d(TAG, String.valueOf(arrayPostKod[0]));
            adapter = new DoctorAdapter(getBaseContext(), arrayFIO, arrayPostKod, arrayKvalificationKod, arrayDepartmentKod, arrayExpiriences);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }


}
