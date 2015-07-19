package com.example.gleb.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.CityAdapter;
import com.example.gleb.adapters.CountryAdapter;
import com.example.gleb.dentistcard.CountryActivity;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertCity;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.tables.City;
import com.example.gleb.tables.Country;
import com.mikepenz.materialdrawer.Drawer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 13.07.15.
 */
public class CityFragment extends Fragment {
    private DatabaseRequest request = new DatabaseRequest();
    public static final String TAG = "TAG";
    public CityAdapter adapter;

    private List<City> cities;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    public Spinner spinners;
    public EditText oldCountryKodEditText;
    public EditText oldCityEditText;
    public EditText newCityEditText;
    public String[] arrayCity = null;
    public int[] arrayIdCity = null;
    public String[] arrayCountrySpinnerKod = null;

    public String[] arrayCountrySpinner = null;
    public int[] arrayIdCountrySpinner = null;
    public int[] arrayOldIdCountry = null;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cityactivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        cities = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertCity.class);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupCountry().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdateCity)
                                .customView(R.layout.update_city, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        new Updater(position).execute();
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();
                        spinners = (Spinner) v.findViewById(R.id.Spinner);

                        oldCountryKodEditText = (EditText) v.findViewById(R.id.oldCountryKodEditText);
                        oldCountryKodEditText.append(arrayCountrySpinnerKod[position]);

                        oldCityEditText = (EditText) v.findViewById(R.id.oldCityEditText);
                        oldCityEditText.append(String.valueOf(arrayCity[position]));
                        newCityEditText = (EditText) v.findViewById(R.id.newCityEditText);

                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {
                        positions.add(position);
                        actionMode = getActivity().startActionMode(callback);
                    }
                })
        );


        return v;
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_header, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    for (int i = 0; i < positions.size(); i++){
                        new Deleter(positions.get(i)).execute();

                    }
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "destroy");
            actionMode = null;
        }

    };

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectCityLookup.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayCity = new String[array.length()];
                arrayIdCity = new int[array.length()];
                arrayCountrySpinnerKod = new String[array.length()];
                arrayOldIdCountry = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    int OldIdCountry = jObject.getInt("IdCountry");
                    String countryKod = jObject.getString("Country");
                    String city = jObject.getString("City");

                    arrayOldIdCountry[i] = OldIdCountry;
                    arrayCountrySpinnerKod[i] = countryKod;
                    arrayCity[i] = city;
                    Log.d(TAG, "Loader City = " + arrayCity[i] + " IdCountry " + arrayOldIdCountry[i] + " Country" + arrayCountrySpinnerKod[i]);

                    cities.add(new City(city, countryKod));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayCity;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new CityAdapter(cities);
            rv.setAdapter(adapter);

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
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayCountrySpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinners.setAdapter(adapterSpinner);

        }
    }

    /**
     * Update record from table CitiesActivity
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldCountryPosition;

        /**
         *
         * @param oldCountryPosition        Position of oldcountry and newcountry in array idcountries
         */
        Updater(int oldCountryPosition){
            this.oldCountryPosition = oldCountryPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "Update OldIdCountry " + arrayOldIdCountry[oldCountryPosition]);
            String oldCountryKod = oldCountryKodEditText.getText().toString();
            String oldCity = oldCityEditText.getText().toString();
            String newCity = newCityEditText.getText().toString();
            int positionCountry = spinners.getSelectedItemPosition();

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateCityInCities.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldCountryKod", arrayOldIdCountry[oldCountryPosition]);
                json.put("newCountryKod", arrayIdCountrySpinner[positionCountry]);
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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete record from table CountriesActivity
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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }
}
