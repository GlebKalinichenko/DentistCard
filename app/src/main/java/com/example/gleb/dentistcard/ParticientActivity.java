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
import com.example.gleb.adapters.ParticientAdapter;
import com.example.gleb.insert.InsertParticient;
import com.example.gleb.tables.Particient;
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
 * Created by gleb on 12.07.15.
 */
public class ParticientActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdParticients = null;
    private String[] arrayFIO = null;
    private String[] arrayAddreses = null;
    //field for male lookup CityKod for City in table CitiesActivity
    private String[] arrayLookupCityKod = null;
    private String[] arrayPhoneNumber = null;
    private String[] arrayFIOParent = null;
    private String[] arrayDateBorn = null;
    private ParticientAdapter adapter;
    public EditText oldFIOEditText;
    public EditText newFIOEditText;
    public EditText oldAddressEditText;
    public EditText newAddressEditText;
    public EditText oldDateBornEditText;
    public EditText newDateBornEditText;
    public EditText oldCityKodEditText;
    public EditText oldPhoneEditText;
    public EditText newPhoneEditText;
    public EditText oldFIOParentEditText;
    public EditText newFIOParentEditText;
    public Spinner cityKodSpinner;
    public int[] arrayIdCitySpinner;
    public String[] arrayCitySpinner;
    public int[] arrayOldIdCity = null;

    private List<Particient> particients;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particientactivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        particients = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticientActivity.this, InsertParticient.class);
                startActivity(intent);
            }
        });

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Particients);
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
                InputMethodManager inputMethodManager = (InputMethodManager) ParticientActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(ParticientActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(ParticientActivity.this, ParticientActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ParticientActivity.this, ParticientActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupCity().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(ParticientActivity.this)
                                .title(R.string.UpdateParticient)
                                .customView(R.layout.update_particients, wrapInScrollView)
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
                        cityKodSpinner = (Spinner) v.findViewById(R.id.cityKodSpinner);
                        oldCityKodEditText = (EditText) v.findViewById(R.id.oldCityKodEditText);
                        oldCityKodEditText.append(arrayLookupCityKod[position]);

                        oldFIOEditText = (EditText) v.findViewById(R.id.oldFIOParticientEditText);
                        oldFIOEditText.append(String.valueOf(arrayFIO[position]));
                        newFIOEditText = (EditText) v.findViewById(R.id.newFIOParticientEditText);

                        oldAddressEditText = (EditText) v.findViewById(R.id.oldAddressEditText);
                        oldAddressEditText.append(arrayAddreses[position]);
                        newAddressEditText = (EditText) v.findViewById(R.id.newAddressEditText);

                        oldDateBornEditText = (EditText) v.findViewById(R.id.oldDateBornEditText);
                        oldDateBornEditText.append(arrayDateBorn[position]);
                        newDateBornEditText = (EditText) v.findViewById(R.id.newDateBornEditText);

                        oldPhoneEditText = (EditText) v.findViewById(R.id.oldPhoneEditText);
                        oldPhoneEditText.append(arrayPhoneNumber[position]);
                        newPhoneEditText = (EditText) v.findViewById(R.id.newPhoneEditText);

                        oldFIOParentEditText = (EditText) v.findViewById(R.id.oldFIOParentEditText);
                        oldFIOParentEditText.append(arrayFIOParent[position]);
                        newFIOParentEditText = (EditText) v.findViewById(R.id.newFIOParentEditText);

                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {
                        positions.add(position);
                        actionMode = startActionMode(callback);
                    }
                })
        );

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
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectParticientLookup.php");
            //Fields of table DoctorsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdParticients = new int[array.length()];
                arrayFIO = new String[array.length()];
                arrayAddreses = new String[array.length()];
                arrayLookupCityKod = new String[array.length()];
                arrayDateBorn = new String[array.length()];
                arrayFIOParent = new String[array.length()];
                arrayPhoneNumber = new String[array.length()];
                arrayOldIdCity = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayAddreses[i] = jObject.getString("Address");
                    arrayDateBorn[i] = jObject.getString("DateBorn");
                    arrayPhoneNumber[i] = jObject.getString("PhoneNumber");
                    arrayFIOParent[i] = jObject.getString("FIOParent");
                    arrayLookupCityKod[i] = jObject.getString("City");
                    arrayOldIdCity[i] = jObject.getInt("IdCity");

                    particients.add(new Particient(jObject.getString("FIO"), jObject.getString("Address"), jObject.getString("City"),
                            jObject.getString("PhoneNumber"), jObject.getString("DateBorn"), jObject.getString("FIOParent")));

                    Log.d(TAG, arrayFIO[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFIO;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new ParticientAdapter(particients);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table particients
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldCityPosition;

        Updater(int oldCityPosition){
            this.oldCityPosition = oldCityPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldFIO = oldFIOEditText.getText().toString();
            String newFIO = newFIOEditText.getText().toString();

            String oldAddress = oldAddressEditText.getText().toString();
            String newAddress = newAddressEditText.getText().toString();

            String oldCityKod = oldCityKodEditText.getText().toString();

            String oldPhone = oldPhoneEditText.getText().toString();
            String newPhone = newPhoneEditText.getText().toString();

            String oldFIOParent = oldFIOParentEditText.getText().toString();
            String newFIOParent = newFIOParentEditText.getText().toString();

            String oldDateBorn = oldDateBornEditText.getText().toString();
            String newDateBorn = newDateBornEditText.getText().toString();

            int positionCity = cityKodSpinner.getSelectedItemPosition();

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateParticientScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldFio", oldFIO);
                json.put("newFio", newFIO);

                json.put("oldAddress", oldAddress);
                json.put("newAddress", newAddress);

                Log.d(TAG, "CityKod " + arrayOldIdCity[oldCityPosition]);

                json.put("oldCityKod", arrayOldIdCity[oldCityPosition]);
                json.put("newCityKod", arrayIdCitySpinner[positionCity]);

                json.put("oldPhoneNumber", oldPhone);
                json.put("newPhoneNumber", newPhone);

                json.put("oldDateBorn", oldDateBorn);
                json.put("newDateBorn", newDateBorn);

                json.put("oldFIOParent", oldFIOParent);
                json.put("newFIOParent", newFIOParent);

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
     * Delete record from table ParticientsActivity
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionPartcient;

        public Deleter(int positionPartcient) {
            this.positionPartcient = positionPartcient;
        }

        @Override
        protected String doInBackground(String... params) {
            String fio = arrayFIO[positionPartcient];
            Log.d(TAG, fio);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteParticientScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fio);
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

    public class LookupCity extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/CityScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayCitySpinner = new String[array.length()];
                arrayIdCitySpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String city = jObject.getString("City");
                    int idCity = jObject.getInt("IdCity");

                    arrayIdCitySpinner[i] = idCity;
                    arrayCitySpinner[i] = city;
                    Log.d(TAG, "CityKod in ParticientsActivity " + arrayIdCitySpinner[i] + " City " + arrayCitySpinner[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayCitySpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayCitySpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cityKodSpinner.setAdapter(adapterSpinner);

        }
    }
}
