package com.example.gleb.insert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by gleb on 20.06.15.
 */
public class InsertCity extends InsertPattern {
    private DatabaseRequest request = new DatabaseRequest();
    private EditText cityEditText;
    public String[] arrayCountrySpinner;
    public int[] arrayIdCountrySpinner;
    public Spinner countrySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_city);

        cityEditText = (EditText) findViewById(R.id.cityEditText);
        insertButton = (Button) findViewById(R.id.insertCityButton);
        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Sender().execute();
            }
        });

        new LookupCountry().execute();

    }

    public class Sender extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String city = cityEditText.getText().toString();
            int countryPosition = countrySpinner.getSelectedItemPosition();

            Log.d(TAG, city);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertCityScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("countryKod", arrayIdCountrySpinner[countryPosition]);
                json.put("city", city);
                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the
                    Log.i("Read from Server", in.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    public class LookupCountry extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/CountryScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayCountrySpinner = new String[array.length()];
                arrayIdCountrySpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String countryKod = jObject.getString("Country");
                    int idCountry = jObject.getInt("IdCountry");

                    arrayIdCountrySpinner[i] = idCountry;
                    arrayCountrySpinner[i] = countryKod;
                    Log.d(TAG, "CountryKod in CitiesActivity " + arrayCountrySpinner[i] + " CountryKod " + arrayIdCountrySpinner[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayCountrySpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
//            Log.d(TAG, "Country Post " + arrayCountrySpinner[0] + " CountryKod Post " + arrayIdCountrySpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), R.layout.spinners, arrayCountrySpinner);
            adapterSpinner.setDropDownViewResource(R.layout.spinner_drop_item);
            countrySpinner.setAdapter(adapterSpinner);

        }
    }
}
