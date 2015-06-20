package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gleb.adapters.CityAdapter;
import com.example.gleb.insert.InsertCity;
import com.example.gleb.insert.InsertCountry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Cities extends Pattern {
    private DatabaseRequest request = new DatabaseRequest();
    public static final String TAG = "TAG";
    private ListView listView;
    //public ArrayAdapter<String> adapter;
    public CityAdapter adapter;

    String[] arrayCity = null;
    int[] arrayIdCity = null;
    int[] arrayCountryKod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dentist_card);

        listView = (ListView) findViewById(R.id.listView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadercity, null);
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
            Intent intent = new Intent(this, InsertCity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/CityScript.php");;

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayCity = new String[array.length()];
                arrayIdCity = new int[array.length()];
                arrayCountryKod = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    int id = jObject.getInt("IdCity");
                    int countryKod = jObject.getInt("CountryKod");
                    String city = jObject.getString("City");
                    arrayIdCity[i] = id;
                    arrayCountryKod[i] = countryKod;
                    arrayCity[i] = city;
                    Log.d(TAG, arrayCity[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayCity;
        }

        @Override
        protected void onPostExecute(String[] value) {
            /*for (int i = 0; i < value.length; i++){
                textView.append(value[i]);
            }*/
            Log.d(TAG, "Execute " + String.valueOf(arrayCountryKod[0]));
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new CityAdapter(getBaseContext(), arrayCity, arrayCountryKod);
            listView.setAdapter(adapter);

        }
    }
}
