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
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.autoresationregistrator.Autoresation;
import com.example.gleb.charts.ChartActivity;
import com.example.gleb.connection.ConnectionRegistrator;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by gleb on 21.06.15.
 */
public class InsertRecommendation extends InsertPattern {
    public static final String PROFILE = "Profile";
    public static final String FULLNAME = "FullName";
    private DatabaseRequest request = new DatabaseRequest();
    private EditText ticketKodEditText;
    private EditText diagnoseKodEditText;
    private EditText therapyEditText;
    private EditText complaintsEditText;
    private EditText historyIllnessEditText;
    private EditText objectiveValuesEditText;
    public MaterialSpinner ticketSpinner;
    public MaterialSpinner diagnoseSpinner;

    public int[] arrayIdTicket;
    public int[] arrayIdDiagnose;
    public String[] arrayTicket;
    public String[] arrayDiagnose;
    public String[] arrayParticient;
    public String[] arrayTicketParticient;
    public ImageButton insertButton;

    public Toolbar toolbar;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;

    public Drawer drawer;

    public int freshTicket;
    public int allTicket;

    public String fullName;
    public String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_recommendation);

        profile = getIntent().getStringExtra(InsertRecommendation.PROFILE);

        switch (profile){
            case "doctor":
                fullName = getIntent().getStringExtra(InsertRecommendation.FULLNAME);
                Log.d(TAG, "DoctorProfileActivity " + fullName);
                freshTicket = getIntent().getIntExtra(InsertDoctor.FRESHTICKET, 0);
                allTicket = getIntent().getIntExtra(InsertDoctor.ALLTICKET, 0);

                therapyEditText = (EditText) findViewById(R.id.therapyEditText);
                complaintsEditText = (EditText) findViewById(R.id.complaintsEditText);
                historyIllnessEditText = (EditText) findViewById(R.id.historyIllnessEditText);
                objectiveValuesEditText = (EditText) findViewById(R.id.objectiveValuesEditText);
                insertButton = (ImageButton) findViewById(R.id.insertRecommendationButton);
                ticketSpinner = (MaterialSpinner) findViewById(R.id.ticketSpinner);
                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Sender().execute();
                    }
                });

                new LoaderTicket().execute();
                new LoaderDiagnose().execute();

                toolbar = (Toolbar) findViewById(R.id.tool_bar);
                setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.Changes);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Initialise Navigation Drawer
                drawer = new Drawer();
                drawer.withActivity(InsertRecommendation.this);
                drawer.withToolbar(toolbar);
                drawer.withActionBarDrawerToggle(true);
                drawer.withHeader(R.layout.drawer_header);

                drawer.addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.NewMessage).withIcon(FontAwesome.Icon.faw_home).withBadge("+" + freshTicket).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.SendMessage).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("+" + allTicket).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("").withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withBadge("").withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withBadge("").withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(6)
                );

                drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertRecommendation.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertRecommendation.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                });

                drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
//                if (drawerItem instanceof Nameable) {
//                    Toast.makeText(ParticientProfileActivity.this, ParticientProfileActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
//                }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();

                            switch (item) {
                                case 1:
                                    break;
                                case 2:
//                            Intent intent = new Intent(ParticientProfileActivity.this, ConnectionRegistrator.class);
//                            intent.putExtra(ConnectionRegistrator.EMAIL, emailParticient);
//                            startActivity(intent);
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
                            Toast.makeText(InsertRecommendation.this, InsertRecommendation.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
                break;

            case "admin":
                therapyEditText = (EditText) findViewById(R.id.therapyEditText);
                complaintsEditText = (EditText) findViewById(R.id.complaintsEditText);
                historyIllnessEditText = (EditText) findViewById(R.id.historyIllnessEditText);
                objectiveValuesEditText = (EditText) findViewById(R.id.objectiveValuesEditText);
                insertButton = (ImageButton) findViewById(R.id.insertRecommendationButton);
                ticketSpinner = (MaterialSpinner) findViewById(R.id.ticketSpinner);
                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Sender().execute();
                    }
                });

                new LoaderTicketAllProfile().execute();
                new LoaderDiagnose().execute();

                toolbar = (Toolbar) findViewById(R.id.tool_bar);
                setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.Changes);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Initialise Navigation Drawer
                drawer = new Drawer();
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
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertRecommendation.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertRecommendation.this.getCurrentFocus().getWindowToken(), 0);
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
                            Toast.makeText(InsertRecommendation.this, InsertRecommendation.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();
                            switch(item){
                                case 2:
                                    Intent intent = new Intent(InsertRecommendation.this, Autoresation.class);
                                    startActivity(intent);
                                    Log.d(TAG, "RegistrationProfileActivity");
                                    break;

                                case 3:
                                    Intent chartIntent = new Intent(InsertRecommendation.this, ChartActivity.class);
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
                            Toast.makeText(InsertRecommendation.this, InsertRecommendation.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
                break;

            case "particient":
                therapyEditText = (EditText) findViewById(R.id.therapyEditText);
                complaintsEditText = (EditText) findViewById(R.id.complaintsEditText);
                historyIllnessEditText = (EditText) findViewById(R.id.historyIllnessEditText);
                objectiveValuesEditText = (EditText) findViewById(R.id.objectiveValuesEditText);
                insertButton = (ImageButton) findViewById(R.id.insertRecommendationButton);
                ticketSpinner = (MaterialSpinner) findViewById(R.id.ticketSpinner);
                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Sender().execute();
                    }
                });

                new LoaderTicketAllProfile().execute();
                new LoaderDiagnose().execute();

                toolbar = (Toolbar) findViewById(R.id.tool_bar);
                setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.Changes);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //Initialise Navigation Drawer
                drawer = new Drawer();
                drawer.withActivity(this);
                drawer.withToolbar(toolbar);
                drawer.withActionBarDrawerToggle(true);
                drawer.withHeader(R.layout.drawer_header);

                drawer.addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.NewMessage).withIcon(FontAwesome.Icon.faw_home).withBadge("+").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.SendMessage).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("").withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("").withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withBadge("").withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withBadge("").withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(6)
                );

                drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertRecommendation.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertRecommendation.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                });

                drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();

                            switch (item) {
                                case 1:
                                    break;
                                case 2:
                                    Intent intent = new Intent(InsertRecommendation.this, ConnectionRegistrator.class);
//                                    intent.putExtra(ConnectionRegistrator.EMAIL, emailParticient);
                                    startActivity(intent);
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
                            Toast.makeText(InsertRecommendation.this, InsertRecommendation.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
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
            if (therapyEditText.getText().toString().equals("") || complaintsEditText.getText().toString().equals("") ||
                    historyIllnessEditText.getText().toString().equals("") || objectiveValuesEditText.getText().toString().equals("")){
                value = 1;
            }
            else {
                int ticketPosition = ticketSpinner.getSelectedItemPosition() - 1;
                int diagnosePosition = diagnoseSpinner.getSelectedItemPosition() - 1;
                String therapy = therapyEditText.getText().toString();
                String complaints = complaintsEditText.getText().toString();
                String historyIllness = historyIllnessEditText.getText().toString();
                String objectiveValues = objectiveValuesEditText.getText().toString();

                Log.d(TAG, "ObjectiveValues " + objectiveValues);

                client = new DefaultHttpClient();
                post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertRecommendationScript.php");
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    json.put("ticketKod", arrayIdTicket[ticketPosition]);
                    json.put("diagnoseKod", arrayIdDiagnose[diagnosePosition]);
                    json.put("therapy", therapy);
                    json.put("complaints", complaints);
                    json.put("historyIllness", historyIllness);
                    json.put("objectiveValues", objectiveValues);
                    post.setHeader("json", json.toString());
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    value = 0;

                    if (response != null) {
                        InputStream in = response.getEntity().getContent(); // Get the
                        Log.i("Read from Server", in.toString());
                    }
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

    public class LoaderTicket extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectTicketParticient.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);

                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the
                    Log.i("Read from Server", in.toString());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "Sb " + sb.toString());
                    if (!sb.toString().equals("")) {
                        JSONArray array = new JSONArray(sb.toString());

                        try{
                            arrayIdTicket = new int[array.length()];
                            arrayTicket = new String[array.length()];
                            arrayParticient = new String[array.length()];
                            arrayTicketParticient = new String[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                arrayIdTicket[i] = jObject.getInt("IdTicket");
                                arrayTicket[i] = jObject.getString("DateReception");
                                arrayParticient[i] = jObject.getString("FIO");
                                arrayTicketParticient[i] = jObject.getString("FIO") + " " + jObject.getString("DateReception");

                                Log.d(TAG, "TicketParticient" + arrayTicketParticient[i]);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayTicketParticient);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ticketSpinner = (MaterialSpinner) findViewById(R.id.ticketSpinner);
            ticketSpinner.setOnItemSelectedListener(spinnerSelected);
            ticketSpinner.setAdapter(adapter);
        }
    }

    public class LoaderTicketAllProfile extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectTicketParticientAllProfile.php");
            //Fields of table RegistrationsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTicket = new int[array.length()];
                arrayTicket = new String[array.length()];
                arrayParticient = new String[array.length()];
                arrayTicketParticient = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdTicket[i] = jObject.getInt("IdTicket");
                    arrayTicket[i] = jObject.getString("DateReception");
                    arrayParticient[i] = jObject.getString("FIO");
                    arrayTicketParticient[i] = jObject.getString("FIO") + " " + jObject.getString("DateReception");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayTicketParticient);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ticketSpinner = (MaterialSpinner) findViewById(R.id.ticketSpinner);
            ticketSpinner.setOnItemSelectedListener(spinnerSelected);
            ticketSpinner.setAdapter(adapter);
        }
    }

    public class LoaderDiagnose extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DiagnoseScript.php");
            //Fields of table RegistrationsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdDiagnose = new int[array.length()];
                arrayDiagnose = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdDiagnose[i] = jObject.getInt("IdDiagnose");
                    arrayDiagnose[i] = jObject.getString("Diagnose");

                    Log.d(TAG, arrayDiagnose[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDiagnose;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayDiagnose);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            diagnoseSpinner = (MaterialSpinner) findViewById(R.id.diagnoseSpinner);
            diagnoseSpinner.setOnItemSelectedListener(spinnerSelected);
            diagnoseSpinner.setAdapter(adapter);
        }
    }
}
