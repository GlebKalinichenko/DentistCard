package com.example.gleb.statistic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.connection.ConnectionRegistrator;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.tables.Ticket;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by gleb on 25.07.15.
 */
public class Statistic extends ActionBarActivity {
    public static final String TAG = "TAG";
    public static final String TICKET = "Ticket";
    public static final String ALLTICKET = "AllTicket";
    public static final String FRESHTICKET = "FreshTicket";
    public EditText fromDateEditText;
    public EditText toDateEditText;

    protected HttpClient client;
    protected HttpPost post;

    public String fullName;
    public String workTicket;
    public String workTicketDate;
    public int freshTicket;
    public int allTicket;

    public Spinner dateSpinner;
    public String[] dates;

    public String fromDate;
    public String toDate;
    public Button spinner;

    public ImageButton addImageButton;

    public Toolbar toolbar;
    public ViewPager pager;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;

    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);

        fullName = getIntent().getStringExtra(Statistic.TICKET);
        freshTicket = getIntent().getIntExtra(Statistic.FRESHTICKET, 0);
        allTicket = getIntent().getIntExtra(Statistic.ALLTICKET, 0);
        Log.d(TAG, "Statistic " + fullName);

        new Statister().execute();

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

        fromDateEditText = (EditText) findViewById(R.id.fromDateEditText);
        toDateEditText = (EditText) findViewById(R.id.toDateEditText);

        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StatisterDate().execute();
            }
        });

        fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            Calendar calendar = Calendar.getInstance();
            return new DatePickerDialog(this, fromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        else{
            if (id == 1) {
                Calendar calendar = Calendar.getInstance();
                return new DatePickerDialog(this, toDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            fromDate = arg1 + "-" + arg2 + "-" + arg3;
            fromDateEditText.setText(fromDate);
            Log.d(TAG, "Date change from " + fromDate);
        }
    };

    private DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            toDate = arg1 + "-" + arg2 + "-" + arg3;
            toDateEditText.setText(toDate);
            Log.d(TAG, "Date change to " + toDate);

        }
    };

    public class Statister extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectWorkTicket.php");
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
                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                workTicket = jObject.getString("Ticket");

                                Log.d(TAG, workTicket);
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
            drawer.addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.NewMessage).withIcon(FontAwesome.Icon.faw_home).withBadge("+" + freshTicket).withIdentifier(1),
                    new PrimaryDrawerItem().withName(R.string.SendMessage).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("+" + allTicket).withIdentifier(2),
                    new PrimaryDrawerItem().withName(R.string.AllParticient).withIcon(FontAwesome.Icon.faw_eye).withBadge("+" + workTicket).withIdentifier(3),
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
                    InputMethodManager inputMethodManager = (InputMethodManager) Statistic.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(Statistic.this.getCurrentFocus().getWindowToken(), 0);
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
//                            Intent intent = new Intent(Statistic.this, ConnectionRegistrator.class);
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
                        Toast.makeText(Statistic.this, Statistic.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            drawer.build();
        }
    }

    public class StatisterDate extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectTicketDate.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);
                json.put("FROM", fromDate);
                json.put("TO", toDate);

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
                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                workTicketDate = jObject.getString("Ticket");

                                Log.d(TAG, workTicketDate);
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
            Toast.makeText(getBaseContext(), "Количество пациентов: " + workTicketDate + " за период с " + fromDate + " по " + toDate, Toast.LENGTH_LONG).show();
        }
    }

}
