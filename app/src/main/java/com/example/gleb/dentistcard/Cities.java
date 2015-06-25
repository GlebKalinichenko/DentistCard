package com.example.gleb.dentistcard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.adapters.CityAdapter;
import com.example.gleb.insert.InsertCity;
import com.example.gleb.insert.InsertCountry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Cities extends Pattern {
    private DatabaseRequest request = new DatabaseRequest();
    public static final String TAG = "TAG";
    private ListView listView;
    public CityAdapter adapter;
    public EditText oldCountryKodEditText;
    public EditText newCountryKodEditText;
    public EditText oldCityEditText;
    public EditText newCityEditText;
    public String[] arrayCity = null;
    public int[] arrayIdCity = null;
    public int[] arrayCountryKod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dentist_card);

        listView = (ListView) findViewById(R.id.listView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadercity, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Cities.this);
                View dialogView = LayoutInflater.from(Cities.this).inflate(R.layout.update_city, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();
                oldCountryKodEditText = (EditText) dialog.findViewById(R.id.oldCountryKodEditText);
                oldCountryKodEditText.append(String.valueOf(arrayCountryKod[position - 1]));
                newCountryKodEditText = (EditText) dialog.findViewById(R.id.newCountryKodEditText);

                oldCityEditText = (EditText) dialog.findViewById(R.id.oldCityEditText);
                oldCityEditText.append(String.valueOf(arrayCity[position - 1]));
                newCityEditText = (EditText) dialog.findViewById(R.id.newCityEditText);

                imageButton = (ImageButton) dialog.findViewById(R.id.updateImageButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Click");
                        new Updater().execute();
                    }
                });
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoice(listView));

    }

    /**
     * MultiChoice for delete record and add new record in table Countries
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener{
        private AbsListView list;
        public ArrayList<Integer> cities = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                cities.add(position);
                setSubtitle(mode, rows);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_add){
                Log.d(TAG, "Item add");
                Intent intent = new Intent(getBaseContext(), InsertCountry.class);
                startActivity(intent);
            }
            else{
                if (item.getItemId() == R.id.item_delete){
                    for (int i = 0; i < cities.size(); i++){
                        Log.d(TAG, arrayCity[cities.get(i) - 1]);
                        new Deleter(cities.get(i) - 1).execute();

                    }
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        private void setSubtitle(ActionMode mode, int selectedCount) {
            switch (selectedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                default:
                    mode.setTitle(String.valueOf(selectedCount));
                    break;
            }
        }
    }

    /**
     * Delete record from table Countries
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionCountry;

        public Deleter(int positionCountry) {
            this.positionCountry = positionCountry;
        }

        @Override
        protected String doInBackground(String... params) {
            String country = arrayCity[positionCountry];
            Log.d(TAG, country);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteCityScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("city", country);
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


    /**
     * Update record from table Cities
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldCountryKod = oldCountryKodEditText.getText().toString();
            String newCountryKod = newCountryKodEditText.getText().toString();
            String oldCity = oldCityEditText.getText().toString();
            String newCity = newCityEditText.getText().toString();

            Log.d(TAG, "Old CountryKod " + oldCountryKod);
            Log.d(TAG, "New CountryKod " + newCountryKod);
            Log.d(TAG, "Old City " + oldCity);
            Log.d(TAG, "New City " + newCity);


            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateCityInCities.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldCountryKod", oldCountryKod);
                json.put("newCountryKod", newCountryKod);
                json.put("oldCity", oldCity);
                json.put("newCity", newCity);
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
