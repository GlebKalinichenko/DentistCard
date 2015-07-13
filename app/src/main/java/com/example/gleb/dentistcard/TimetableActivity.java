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
import com.example.gleb.adapters.TimetableAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertTimetable;
import com.example.gleb.tables.Change;
import com.example.gleb.tables.Timetable;
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
import java.util.regex.*;

/**
 * Created by gleb on 12.07.15.
 */
public class TimetableActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private TimetableAdapter adapter;
    private int[] arrayIdTimetables = null;
    private String[] arrayDateTimetables = null;
    private String[] arrayDoctorKod = null;
    private String[] arrayChangeKod = null;
    public EditText oldDateWorkEditText;
    public EditText newDateWorkEditText;
    public EditText oldChangeKodEditText;
    public EditText newChangeKodEditText;
    public EditText oldDoctorKodEditText;
    public EditText newDoctorKodEditText;

    private String[] arrayOldDoctorSpinner;
    private int[] arrayOldIdDoctorSpinner;
    private String[] arrayOldChangeSpinner;
    private int[] arrayOldIdChangeSpinner;
    private Spinner doctorKodSpinner;
    private Spinner changeKodSpinner;
    private int[] arrayIdDoctor;
    private int[] arrayIdChange;

    private List<Timetable> timetables;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetableactivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        timetables = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimetableActivity.this, InsertTimetable.class);
                startActivity(intent);
            }
        });

        new Loader().execute();

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Timetables);
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
                InputMethodManager inputMethodManager = (InputMethodManager) TimetableActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(TimetableActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(TimetableActivity.this, TimetableActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TimetableActivity.this, TimetableActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                        new LookupChange().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(TimetableActivity.this)
                                .title(R.string.UpdateTimetable)
                                .customView(R.layout.update_timetable, wrapInScrollView)
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
                        changeKodSpinner = (Spinner) v.findViewById(R.id.changeKodSpinner);

                        oldDoctorKodEditText = (EditText) v.findViewById(R.id.oldDoctorKodEditText);
                        oldDoctorKodEditText.append(arrayDoctorKod[position]);

                        oldChangeKodEditText = (EditText) v.findViewById(R.id.oldChangeKodEditText);
                        oldChangeKodEditText.append(arrayChangeKod[position]);

                        oldDateWorkEditText = (EditText) v.findViewById(R.id.oldDateWorkEditText);
                        oldDateWorkEditText.append(arrayDateTimetables[position]);
                        newDateWorkEditText = (EditText) v.findViewById(R.id.newDateWorkEditText);
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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectTimetableLookup.php");
            //Fields of table Timetables

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdDoctor = new int[array.length()];
                arrayIdChange = new int[array.length()];
                arrayDoctorKod = new String[array.length()];
                arrayChangeKod = new String[array.length()];
                arrayDateTimetables = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayDoctorKod[i] = jObject.getString("FIO");
                    arrayChangeKod[i] = jObject.getString("Changing");
                    arrayDateTimetables[i] = jObject.getString("DateWork");
                    arrayIdDoctor[i] = jObject.getInt("IdDoctor");
                    arrayIdChange[i] = jObject.getInt("IdChange");

                    timetables.add(new Timetable(jObject.getString("DateWork"), jObject.getString("FIO"),
                            jObject.getString("Changing")));

                    Log.d(TAG, arrayDateTimetables[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateTimetables;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new TimetableAdapter(timetables);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table Posts
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldDoctorPosition;
        public int oldChangePosition;

        public Updater(int oldPosition) {
            this.oldDoctorPosition = oldPosition;
            this.oldChangePosition = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            int doctorPosition = doctorKodSpinner.getSelectedItemPosition();
            int changePosition = changeKodSpinner.getSelectedItemPosition();

            Log.d(TAG, "New doctorKod " + String.valueOf(arrayOldIdDoctorSpinner[doctorPosition]));
            Log.d(TAG, "Old doctorKod " + String.valueOf(arrayIdDoctor[doctorPosition]));

            String oldDateWork = oldDateWorkEditText.getText().toString();
            String newDateWork = newDateWorkEditText.getText().toString();

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateTimetableScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDoctorKod", arrayIdDoctor[oldDoctorPosition]);
                json.put("newDoctorKod", arrayOldIdDoctorSpinner[doctorPosition]);

                json.put("oldChangeKod", arrayIdChange[oldChangePosition]);
                json.put("newChangeKod", arrayOldIdChangeSpinner[changePosition]);

                json.put("oldDateWork", oldDateWork);
                json.put("newDateWork", newDateWork);

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
        private int positionIdTimetable;

        public Deleter(int positionIdTimetable) {
            this.positionIdTimetable = positionIdTimetable;
        }

        @Override
        protected String doInBackground(String... params) {
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteTimetableScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("DoctorKod", arrayIdDoctor[positionIdTimetable]);
                json.put("ChangeKod", arrayIdChange[positionIdTimetable]);
                json.put("DateWork", arrayDateTimetables[positionIdTimetable]);

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
                arrayOldDoctorSpinner = new String[array.length()];
                arrayOldIdDoctorSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String dateReception = jObject.getString("FIO");
                    int idTicket = jObject.getInt("IdDoctor");

                    arrayOldDoctorSpinner[i] = dateReception;
                    arrayOldIdDoctorSpinner[i] = idTicket;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayOldDoctorSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayOldDoctorSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorKodSpinner.setAdapter(adapterSpinner);

        }
    }

    public class LookupChange extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ChangeScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayOldChangeSpinner = new String[array.length()];
                arrayOldIdChangeSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String change = jObject.getString("Changing");
                    int idChange = jObject.getInt("IdChange");

                    arrayOldChangeSpinner[i] = change;
                    arrayOldIdChangeSpinner[i] = idChange;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayOldChangeSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayOldChangeSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            changeKodSpinner.setAdapter(adapterSpinner);

        }
    }

}
