package com.example.gleb.insert;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.autoresationregistrator.Autoresation;
import com.example.gleb.charts.ChartActivity;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gleb on 15.06.2015.
 */
public class InsertCountry extends InsertPattern {
    public static final String PROFILE = "Profile";
    private EditText countryEditText;
    public ImageButton insertButton;

    public String profile;

    public Toolbar toolbar;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;
    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_value);

        profile = getIntent().getStringExtra(InsertDoctor.PROFILE);

        switch (profile){
            case "admin":
                countryEditText = (EditText) findViewById(R.id.countryEditText);
                insertButton = (ImageButton) findViewById(R.id.insertCountryButton);
                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (countryEditText.getText().toString().equals("")){
                            Toast.makeText(getBaseContext(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new Sender().execute();
                        }
                    }
                });

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
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertCountry.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertCountry.this.getCurrentFocus().getWindowToken(), 0);
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
                            Toast.makeText(InsertCountry.this, InsertCountry.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();
                            switch(item){
                                case 2:
                                    Intent intent = new Intent(InsertCountry.this, Autoresation.class);
                                    startActivity(intent);
                                    Log.d(TAG, "RegistrationProfileActivity");
                                    break;

                                case 3:
                                    Intent chartIntent = new Intent(InsertCountry.this, ChartActivity.class);
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
                            Toast.makeText(InsertCountry.this, InsertCountry.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
                break;

            case "registrator":{
                countryEditText = (EditText) findViewById(R.id.countryEditText);
                insertButton = (ImageButton) findViewById(R.id.insertCountryButton);
                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (countryEditText.getText().toString().equals("")){
                            Toast.makeText(getBaseContext(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new Sender().execute();
                        }
                    }
                });
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
                        InputMethodManager inputMethodManager = (InputMethodManager) InsertCountry.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(InsertCountry.this.getCurrentFocus().getWindowToken(), 0);
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
                            Toast.makeText(InsertCountry.this, InsertCountry.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            int item = drawerItem.getIdentifier();
                            switch(item){
                                case 2:
                                    Intent intent = new Intent(InsertCountry.this, Autoresation.class);
                                    startActivity(intent);
                                    Log.d(TAG, "RegistrationProfileActivity");
                                    break;

                                case 3:
                                    Intent chartIntent = new Intent(InsertCountry.this, ChartActivity.class);
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
                            Toast.makeText(InsertCountry.this, InsertCountry.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
                break;
            }

        }
    }

    public class Sender extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String country = countryEditText.getText().toString();
            Log.d(TAG, country);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertCountry.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("country", country);
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
