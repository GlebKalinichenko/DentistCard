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
import com.example.gleb.adapters.DoctorAdapter;
import com.example.gleb.insert.InsertDoctor;
import com.example.gleb.tables.Doctor;
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
 * Created by gleb on 10.07.15.
 */
public class DoctorActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    public String[] arrayFIO = null;
    public int[] arrayIdDoctors = null;
    public String[] arrayPostKod = null;
    public String[] arrayDepartmentKod = null;
    public String[] arrayKvalificationKod = null;
    public int[] arrayExpiriences = null;
    private DoctorAdapter adapter;
    public EditText oldFIOEditText;
    public EditText newFIOEditText;
    public EditText oldPostKodEditText;
    public EditText oldKvalificationKodEditText;
    public EditText oldDepartmentKodEditText;
    public EditText oldExpirienceEditText;
    public EditText newExpirienceEditText;

    public Spinner postKodSpinner;
    public Spinner kvalificationKodSpinner;
    public Spinner departmentKodSpinner;

    public String[] arrayPostSpinner;
    public int[] arrayIdPostSpinner;

    public String[] arrayKvalificationSpinner;
    public int[] arrayIdKvalificationSpinner;

    public String[] arrayDepartmentSpinner;
    public int[] arrayIdDepartmentSpinner;

    public int[] arrayOldIdPost;
    public int[] arrayOldIdKvalification;
    public int[] arrayOldIdDepartment;

    private List<Doctor> doctors;
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
        doctors = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this, InsertDoctor.class);
                startActivity(intent);
            }
        });

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Doctors);
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
                InputMethodManager inputMethodManager = (InputMethodManager) DoctorActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(DoctorActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(DoctorActivity.this, DoctorActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DoctorActivity.this, DoctorActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupPost().execute();
                        new LookupKvalification().execute();
                        new LookupDepartment().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(DoctorActivity.this)
                                .title(R.string.UpdateDoctors)
                                .customView(R.layout.update_doctors, wrapInScrollView)
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
                        postKodSpinner = (Spinner) v.findViewById(R.id.postKodSpinner);
                        kvalificationKodSpinner = (Spinner) v.findViewById(R.id.kvalificationKodSpinner);
                        departmentKodSpinner = (Spinner) v.findViewById(R.id.departmentKodSpinner);

                        oldKvalificationKodEditText = (EditText) v.findViewById(R.id.oldKvalificationKodEditText);
                        oldKvalificationKodEditText.append(String.valueOf(arrayKvalificationKod[position]));

                        oldPostKodEditText = (EditText) v.findViewById(R.id.oldPostKodEditText);
                        oldPostKodEditText.append(String.valueOf(arrayPostKod[position]));

                        oldDepartmentKodEditText = (EditText) v.findViewById(R.id.oldDepartmentKodEditText);
                        oldDepartmentKodEditText.append(String.valueOf(arrayDepartmentKod[position]));

                        oldExpirienceEditText = (EditText) v.findViewById(R.id.oldExpirienceEditText);
                        oldExpirienceEditText.append(String.valueOf(arrayExpiriences[position]));
                        newExpirienceEditText = (EditText) v.findViewById(R.id.newExpirienceEditText);

                        oldFIOEditText = (EditText) v.findViewById(R.id.oldFIOEditText);
                        oldFIOEditText.append(String.valueOf(arrayFIO[position]));
                        newFIOEditText = (EditText) v.findViewById(R.id.newFIOEditText);

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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectDoctorLookup.php");
            //Fields of table DoctorsActivity


            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayFIO = new String[array.length()];
                arrayOldIdPost = new int[array.length()];
                arrayOldIdKvalification = new int[array.length()];
                arrayOldIdDepartment = new int[array.length()];
                arrayDepartmentKod = new String[array.length()];
                arrayKvalificationKod = new String[array.length()];
                arrayExpiriences = new int[array.length()];
                arrayPostKod = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayOldIdPost[i] = jObject.getInt("IdPost");
                    arrayOldIdKvalification[i] = jObject.getInt("IdKvalification");
                    arrayOldIdDepartment[i] = jObject.getInt("IdDepartment");
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayDepartmentKod[i] = jObject.getString("Department");
                    arrayKvalificationKod[i] = jObject.getString("Kvalification");
                    arrayPostKod[i] = jObject.getString("Post");
                    arrayExpiriences[i] = jObject.getInt("Expirience");

                    doctors.add(new Doctor(jObject.getString("FIO"), jObject.getString("Post"), jObject.getString("Kvalification"),
                            jObject.getString("Department"), jObject.getInt("Expirience")));

                    Log.d(TAG, arrayFIO[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFIO;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new DoctorAdapter(doctors);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table doctors
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldPostPosition;
        public int oldKvalificationPosition;
        public int oldDepartmentPosition;

        public Updater(int oldPosition) {
            this.oldPostPosition = oldPosition;
            this.oldKvalificationPosition = oldPosition;
            this.oldDepartmentPosition = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldFIO = oldFIOEditText.getText().toString();
            String newFIO = newFIOEditText.getText().toString();

            String oldPostKod = oldPostKodEditText.getText().toString();
            String oldKvalificationKod = oldKvalificationKodEditText.getText().toString();
            String oldDepartmentKod = oldDepartmentKodEditText.getText().toString();

            String oldExpirience = oldExpirienceEditText.getText().toString();
            String newExpirience = newExpirienceEditText.getText().toString();

            int positionPost = postKodSpinner.getSelectedItemPosition();
            int positionKvalification = kvalificationKodSpinner.getSelectedItemPosition();
            int positionDepartment = departmentKodSpinner.getSelectedItemPosition();

            Log.d(TAG, "Old Diagnose " + oldFIO);
            Log.d(TAG, "New Diagnose " + newFIO);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateDoctorScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldFIO", oldFIO);
                json.put("newFIO", newFIO);

                json.put("oldPostKod", arrayOldIdPost[oldPostPosition]);
                json.put("newPostKod", arrayIdPostSpinner[positionPost]);

                json.put("oldKvalificationKod", arrayOldIdKvalification[oldKvalificationPosition]);
                json.put("newKvalificationKod", arrayIdKvalificationSpinner[positionKvalification]);

                json.put("oldDepartmentKod", arrayOldIdDepartment[oldDepartmentPosition]);
                json.put("newDepartmentKod", arrayIdDepartmentSpinner[positionDepartment]);

                json.put("oldExpirience", oldExpirience);
                json.put("newExpirience", newExpirience);
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
     * Delete record from table doctors
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionFIO;

        public Deleter(int positionFIO) {
            this.positionFIO = positionFIO;
        }

        @Override
        protected String doInBackground(String... params) {
            String fio = arrayFIO[positionFIO];
            Log.d(TAG, fio);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteDoctorScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("fio", fio);
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

    public class LookupPost extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/PostScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayPostSpinner = new String[array.length()];
                arrayIdPostSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String post = jObject.getString("Post");
                    int idPost = jObject.getInt("IdPost");

                    arrayPostSpinner[i] = post;
                    arrayIdPostSpinner[i] = idPost;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPostSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayPostSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            postKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupKvalification extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/KvalificationScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayKvalificationSpinner = new String[array.length()];
                arrayIdKvalificationSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String kvalification = jObject.getString("Kvalification");
                    int idKvalification = jObject.getInt("IdKvalification");

                    arrayKvalificationSpinner[i] = kvalification;
                    arrayIdKvalificationSpinner[i] = idKvalification;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPostSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayKvalificationSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kvalificationKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupDepartment extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DepartmentsDoctorsScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDepartmentSpinner = new String[array.length()];
                arrayIdDepartmentSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String department = jObject.getString("Department");
                    int idDepartment = jObject.getInt("IdDepartment");

                    arrayDepartmentSpinner[i] = department;
                    arrayIdDepartmentSpinner[i] = idDepartment;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDepartmentSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayDepartmentSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            departmentKodSpinner.setAdapter(adapterSpinner);
        }
    }

}
