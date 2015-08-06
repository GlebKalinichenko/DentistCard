package com.example.gleb.insert;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.autoresationregistrator.Autoresation;
import com.example.gleb.charts.ChartActivity;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.rengwuxian.materialedittext.MaterialEditText;

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

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by gleb on 21.06.15.
 */
public class InsertRegistration extends InsertPattern {
    public static final String PROFILE = "Profile";
    public static final String FRESHTICKET = "FreshTicket";
    public static final String ALLTICKET = "AllTicket";
    private DatabaseRequest request = new DatabaseRequest();
    private MaterialEditText dateRegistrationEditText;
    private EditText particientKodEditText;
    public ImageButton insertButton;
    public MaterialSpinner particientSpinner;

    public int[] arrayIdParticient;
    public String[] arrayParticient;

    public int freshTicket;
    public int allTicket;

    public Toolbar toolbar;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;

    public Drawer drawer;

    public String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_registration);

        profile = getIntent().getStringExtra(InsertRegistration.PROFILE);

        switch (profile){
            case "admin":
                dateRegistrationEditText = (MaterialEditText) findViewById(R.id.dateRegistrationEditText);
                particientSpinner = (MaterialSpinner) findViewById(R.id.particientSpinner);
                insertButton = (ImageButton) findViewById(R.id.insertRegistrationButton);

                toolbar = (Toolbar) findViewById(R.id.tool_bar);
                setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.Changes);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Initialise Navigation Drawer
                Drawer drawer = new Drawer();
                drawer.withActivity(this);
                drawer.withToolbar(toolbar);
                drawer.withActionBarDrawerToggle(true);
                drawer.withHeader(R.layout.drawer_header);

                drawer.addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.ShowMail).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.SkillDoctor).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
                );

                drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertRegistration.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertRegistration.this.getCurrentFocus().getWindowToken(), 0);
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
                            Toast.makeText(InsertRegistration.this, InsertRegistration.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();
                            switch(item){
                                case 2:
                                    Intent intent = new Intent(InsertRegistration.this, Autoresation.class);
                                    startActivity(intent);
                                    Log.d(TAG, "RegistrationProfileActivity");
                                    break;

                                case 3:
                                    Intent chartIntent = new Intent(InsertRegistration.this, ChartActivity.class);
                                    startActivity(chartIntent);
                                    break;
                            }
                        }
                    }
                });

                drawer.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(InsertRegistration.this, InsertRegistration.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();

                new LoaderParticient().execute();

                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Sender().execute();
                    }
                });
                break;
        }
    }

    private AdapterView.OnItemSelectedListener spinnerSelected = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.accent));
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public class Sender extends AsyncTask<String, String, String> {
        public int value;

        @Override
        protected String doInBackground(String... params) {
            if (dateRegistrationEditText.getText().toString().equals("")){
                value = 1;
            }
            else {
                String dateRegistration = dateRegistrationEditText.getText().toString();
                int particientPosition = particientSpinner.getSelectedItemPosition() - 1;

                client = new DefaultHttpClient();
                post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertRegistrationScript.php");
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    json.put("dateRegistration", dateRegistration);
                    json.put("particientKod", arrayIdParticient[particientPosition]);
                    post.setHeader("json", json.toString());
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    if (response != null) {
                        InputStream in = response.getEntity().getContent(); // Get the
                        Log.i("Read from Server", in.toString());
                    }

                    value = 0;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return String.valueOf(value);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("0")) {
                Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(), R.string.AddContent, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class LoaderParticient extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ParticientScript.php");
            //Fields of table RegistrationsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdParticient = new int[array.length()];
                arrayParticient = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdParticient[i] = jObject.getInt("IdParticient");
                    arrayParticient[i] = jObject.getString("FIO");

                    Log.d(TAG, arrayParticient[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayParticient;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayParticient);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            particientSpinner = (MaterialSpinner) findViewById(R.id.particientSpinner);
            particientSpinner.setOnItemSelectedListener(spinnerSelected);
            particientSpinner.setAdapter(adapter);
        }
    }
}
