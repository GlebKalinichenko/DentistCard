package com.example.gleb.connection;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gleb.adapters.ChangeAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.Mail;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.tables.Change;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gleb on 21.07.15.
 */
public class ConnectionRegistrator extends ActionBarActivity {
    public static final String TAG = "TAG";
    public static final String EMAIL = "Email";
    public static final String PROFILE = "Profile";
    private DatabaseRequest request = new DatabaseRequest();
    public EditText themeEditText;
    public EditText textEditText;
    public Spinner emailSpinner;
    public ImageButton sendButton;

    public String[] arrayFullName;
    public String[] arrayEmail;

    public String emailParticient;
    public String profile;

    public Toolbar toolbar;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;
    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_doctor);

        profile = getIntent().getStringExtra(ConnectionRegistrator.PROFILE);

        switch (profile){
            case "particient":
                emailParticient = getIntent().getStringExtra(ConnectionRegistrator.EMAIL);
                Log.d(TAG, "ConnectionRegistrator " + emailParticient);

                themeEditText = (EditText) findViewById(R.id.themeEditText);
                textEditText = (EditText) findViewById(R.id.textEditText);
                emailSpinner = (Spinner) findViewById(R.id.emailSpinner);
                sendButton = (ImageButton) findViewById(R.id.sendButton);

                new Loader().execute();

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                        Log.d(TAG, "to " + arrayEmail[emailSpinner.getSelectedItemPosition()]);
                        String[] toArr = {arrayEmail[emailSpinner.getSelectedItemPosition()]}; // This is an array, you can add more emails, just separate them with a coma
                        m.setTo(toArr); // load array to setTo function
                        Log.d(TAG, "from " + emailParticient);
                        m.setFrom(emailParticient); // who is sending the email
                        m.setSubject(themeEditText.getText().toString());
                        m.setBody(textEditText.getText().toString());

                        try {
                            if(m.send()) {
                                // success
                                Toast.makeText(ConnectionRegistrator.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                // failure
                                Toast.makeText(ConnectionRegistrator.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        InputMethodManager inputMethodManager = (InputMethodManager) ConnectionRegistrator.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(ConnectionRegistrator.this.getCurrentFocus().getWindowToken(), 0);
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
                                    Intent intent = new Intent(ConnectionRegistrator.this, ConnectionRegistrator.class);
                                    intent.putExtra(ConnectionRegistrator.EMAIL, emailParticient);
                                    intent.putExtra(ConnectionRegistrator.PROFILE, "particient");
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
                            Toast.makeText(ConnectionRegistrator.this, ConnectionRegistrator.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                drawer.build();
                break;
        }



    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/ConnectionScript/SearchRegistrations.php");
            //Fields of table PostsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayFullName = new String[array.length()];
                arrayEmail = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    String fullName = jObject.getString("FullName");
                    String email = jObject.getString("Email");
                    arrayFullName[i] = fullName;
                    arrayEmail[i] = email;

                    Log.d(TAG, "FullName " + arrayFullName[i]);
                    Log.d(TAG, "Email " + arrayEmail[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFullName;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(ConnectionRegistrator.this, android.R.layout.simple_spinner_item, arrayFullName);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            emailSpinner.setAdapter(adapterSpinner);

        }
    }
}
