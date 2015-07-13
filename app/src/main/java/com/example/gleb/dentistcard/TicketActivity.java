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
import com.example.gleb.adapters.TicketAdapter;
import com.example.gleb.insert.InsertTicket;
import com.example.gleb.tables.Ticket;
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
 * Created by gleb on 08.07.15.
 */
public class TicketActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdTickets = null;
    private String[] arrayDoctorKod = null;
    private String[] arrayRegistrationKod = null;
    private String[] arrayDateReception = null;
    public EditText oldDoctorKodEditText;
    public EditText oldDateReceptionEditText;
    public EditText newDateReceptionEditText;
    public EditText oldRegistrationKodEditText;
    public TicketAdapter adapter;

    private int[] arrayOldIdDoctor = null;
    private int[] arrayOldIdRegistration = null;

    public String[] arrayDoctorSpinner;
    public int[] arrayIdDoctorSpinner;
    public String[] arrayRegistrationSpinner;
    public int[] arrayIdRegistrationSpinner;
    public Spinner doctorKodSpinner;
    public Spinner registrationKodSpinner;

    private List<Ticket> tickets;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticketactivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        tickets = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketActivity.this, InsertTicket.class);
                startActivity(intent);
            }
        });

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Ticket);
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
                InputMethodManager inputMethodManager = (InputMethodManager) TicketActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(TicketActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(TicketActivity.this, TicketActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TicketActivity.this, TicketActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupDoctor().execute();
                        new LookupRegistration().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(TicketActivity.this)
                                .title(R.string.UpdateTicket)
                                .customView(R.layout.update_ticket, wrapInScrollView)
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
                        doctorKodSpinner = (Spinner) v.findViewById(R.id.doctorKodSpinner);
                        registrationKodSpinner = (Spinner) v.findViewById(R.id.registrationKodSpinner);

                        oldDoctorKodEditText = (EditText) v.findViewById(R.id.oldDoctorKodEditText);
                        oldDoctorKodEditText.append(arrayDoctorKod[position]);

                        oldRegistrationKodEditText = (EditText) v.findViewById(R.id.oldRegistrationKodEditText);
                        oldRegistrationKodEditText.append(arrayRegistrationKod[position]);

                        oldDateReceptionEditText = (EditText) v.findViewById(R.id.oldDateReceptionEditText);
                        oldDateReceptionEditText.append(String.valueOf(arrayDateReception[position]));
                        newDateReceptionEditText = (EditText) v.findViewById(R.id.newDateReceptionEditText);

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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectTicketLookup.php");
            //Fields of table tickets

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTickets = new int[array.length()];
                arrayDoctorKod = new String[array.length()];
                arrayRegistrationKod = new String[array.length()];
                arrayDateReception = new String[array.length()];
                arrayOldIdDoctor = new int[array.length()];
                arrayOldIdRegistration = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayOldIdDoctor[i] = jObject.getInt("IdDoctor");
                    arrayOldIdRegistration[i] = jObject.getInt("IdRegistration");
                    arrayDoctorKod[i] = jObject.getString("FIO");
                    arrayRegistrationKod[i] = jObject.getString("DateRegistration");
                    arrayDateReception[i] = jObject.getString("DateReception");

                    tickets.add(new Ticket(jObject.getString("FIO"), jObject.getString("DateRegistration"),
                            jObject.getString("DateReception")));

                    Log.d(TAG, arrayDateReception[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateReception;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new TicketAdapter(tickets);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table PostsActivity
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldDoctorPosition;
        public int oldRegistrationPosition;

        public Updater(int oldPosition) {
            this.oldDoctorPosition = oldPosition;
            this.oldRegistrationPosition = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldDoctorKod = oldDoctorKodEditText.getText().toString();
            String oldRegistrationKod = oldRegistrationKodEditText.getText().toString();

            String oldDateReception = oldDateReceptionEditText.getText().toString();
            String newDateReception = newDateReceptionEditText.getText().toString();

            int doctorPosition = doctorKodSpinner.getSelectedItemPosition();
            int registrationPosition = registrationKodSpinner.getSelectedItemPosition();

            Log.d(TAG, "Old DoctorKod " + oldDoctorKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDoctorKod", arrayOldIdDoctor[oldDoctorPosition]);
                json.put("newDoctorKod", arrayIdDoctorSpinner[doctorPosition]);

                json.put("oldRegistrationKod", arrayOldIdRegistration[oldRegistrationPosition]);
                json.put("newRegistrationKod", arrayIdRegistrationSpinner[registrationPosition]);

                json.put("oldDateReception", oldDateReception);
                json.put("newDateReception", newDateReception);

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

    public class Deleter extends AsyncTask<String, String, String>{
        private int position;

        public Deleter(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {
            int doctorKod = arrayOldIdDoctor[position];
            int registrationKod = arrayOldIdRegistration[position];
            String dateReception = arrayDateReception[position];

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("DoctorKod", doctorKod);
                json.put("RegistrationKod", registrationKod);
                json.put("DateReception", dateReception);
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

    public class LookupDoctor extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DoctorScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDoctorSpinner = new String[array.length()];
                arrayIdDoctorSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String doctor = jObject.getString("FIO");
                    int idDoctor = jObject.getInt("IdDoctor");

                    arrayDoctorSpinner[i] = doctor;
                    arrayIdDoctorSpinner[i] = idDoctor;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDoctorSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, "LookupDoctor " + arrayDoctorSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayDoctorSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupRegistration extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/RegistrationScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayRegistrationSpinner = new String[array.length()];
                arrayIdRegistrationSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String registration = jObject.getString("DateRegistration");
                    int idRegistration = jObject.getInt("IdRegistration");

                    arrayRegistrationSpinner[i] = registration;
                    arrayIdRegistrationSpinner[i] = idRegistration;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayRegistrationSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayRegistrationSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            registrationKodSpinner.setAdapter(adapterSpinner);

        }
    }

}
