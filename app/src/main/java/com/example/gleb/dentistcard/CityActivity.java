package com.example.gleb.dentistcard;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.CityAdapter;
import com.example.gleb.insert.InsertCity;
import com.example.gleb.tables.City;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 05.07.15.
 */
public class CityActivity extends Pattern {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cityactivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        cities = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityActivity.this, InsertCity.class);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupCountry().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(CityActivity.this)
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
                        actionMode = startActionMode(callback);
                    }
                })
        );

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Cities);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialise Navigation Drawer
        Drawer drawer = new Drawer();
        drawer.withActivity(this);
        drawer.withToolbar(toolbar);
        drawer.withActionBarDrawerToggle(true);
        drawer.withHeader(R.layout.drawer_header);

        drawer.addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2),
                new SectionDrawerItem().withName(R.string.drawer_item_settings),
                new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
        );

        drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Скрываем клавиатуру при открытии Navigation Drawer
                InputMethodManager inputMethodManager = (InputMethodManager) CityActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(CityActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        });

        drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            // Обработка клика
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                if (drawerItem instanceof Nameable) {
                    Toast.makeText(CityActivity.this, CityActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                if (drawerItem instanceof Badgeable) {
                    Badgeable badgeable = (Badgeable) drawerItem;
                    if (badgeable.getBadge() != null) {
                        // учтите, не делайте так, если ваш бейдж содержит символ "+"
                        try {
                            int badge = Integer.valueOf(badgeable.getBadge());
                            if (badge > 0) {
                                drawerResult.updateBadge(String.valueOf(badge - 1), position);
                            }
                        } catch (Exception e) {
                            Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                        }
                    }
                }
            }
        });

        drawer.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
            @Override
            // Обработка длинного клика, например, только для SecondaryDrawerItem
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                if (drawerItem instanceof SecondaryDrawerItem) {
                    Toast.makeText(CityActivity.this, CityActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

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

    public class Loader extends AsyncTask<String, String, String[]>{

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
//            Log.d(TAG, "Country Post " + arrayCountrySpinner[0] + " CountryKod Post " + arrayIdCountrySpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayCountrySpinner);
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
            Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

}
