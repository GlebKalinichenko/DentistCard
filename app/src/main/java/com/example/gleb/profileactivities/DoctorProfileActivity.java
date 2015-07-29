package com.example.gleb.profileactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.notifications.NotificationUtils;
import com.example.gleb.statistic.Statistic;
import com.example.gleb.viewpagers.DoctorViewPagerAdapter;
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

/**
 * Created by gleb on 14.07.15.
 */
public class DoctorProfileActivity extends ProfilePattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private CharSequence[] Titles = {
        "Врачи", "Пациенты", "Билеты", "Рекомендации"
    };
    private int Numboftabs = 4;
    private String fullName;
    public SharedPreferences sharedPreferences;
    public final String SAVED_TEXT = "saved_text";
    public int[] arrayFreshTicket;


    protected HttpClient client;
    protected HttpPost post;

    public String savedText;
    public int freshTicket;

    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullName = getIntent().getStringExtra(Login.FULLNAME);
        Log.d(TAG, "DoctorProfileActivity " + fullName);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.Changes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadFreshTicket();

        new Loader().execute();

        // Creating The AdminViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new DoctorViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs, fullName);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/FreshTicketDoctor.php");
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
                            //parse of array
                            arrayFreshTicket = new int[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                int freshTicket = jObject.getInt("FreshTicket");
                                arrayFreshTicket[i] = freshTicket;

                                Log.d(TAG, "arrayFreshTicket " + String.valueOf(arrayFreshTicket[i]));
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
            //Initialise Navigation Drawer
            drawer = new Drawer();
            drawer.withActivity(DoctorProfileActivity.this);
            drawer.withToolbar(toolbar);
            drawer.withActionBarDrawerToggle(true);
            drawer.withHeader(R.layout.drawer_header);

            Log.d(TAG, "savedText " + savedText);

            if (Integer.parseInt(savedText) < arrayFreshTicket[0]){
                freshTicket = arrayFreshTicket[0] - Integer.parseInt(savedText);
                NotificationUtils n = NotificationUtils.getInstance(DoctorProfileActivity.this);
                n.createInfoNotification("+" + freshTicket + " назначеный билет");
                Log.d(TAG, "saveText and arrayFreshTicket " + freshTicket);

                drawer.addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_item_tickets).withIcon(FontAwesome.Icon.faw_home).withBadge("+" + String.valueOf(freshTicket)).withIdentifier(1),
                    new PrimaryDrawerItem().withName(R.string.AllTicket).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("+" + String.valueOf(arrayFreshTicket[0])).withIdentifier(2),
                    new PrimaryDrawerItem().withName(R.string.AllParticient).withIcon(FontAwesome.Icon.faw_eye).withBadge("").withIdentifier(3),
                    new SectionDrawerItem().withName(R.string.drawer_item_settings),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withIdentifier(5),
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(6)
                );
            }
            else{
                drawer.addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_tickets).withIcon(FontAwesome.Icon.faw_home).withBadge("").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.AllTicket).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("+" + String.valueOf(arrayFreshTicket[0])).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.AllParticient).withIcon(FontAwesome.Icon.faw_eye).withBadge("").withIdentifier(3),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(6)
                );
            }

            drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    // Скрываем клавиатуру при открытии Navigation Drawer
                    InputMethodManager inputMethodManager = (InputMethodManager) DoctorProfileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(DoctorProfileActivity.this.getCurrentFocus().getWindowToken(), 0);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                }
            });

            drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                // Обработка клика
                public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
//                    if (drawerItem instanceof Nameable) {
//                        Toast.makeText(DoctorProfileActivity.this, DoctorProfileActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
//                    }
                    if (drawerItem instanceof Badgeable) {
                        Badgeable badgeable = (Badgeable) drawerItem;
                        int item = drawerItem.getIdentifier();

                        switch(item){
                            case 1: saveFreshTicket(); break;
                            case 3:
                                Intent ticketIntent = new Intent(DoctorProfileActivity.this, Statistic.class);
                                ticketIntent.putExtra(Statistic.TICKET, fullName);
                                startActivity(ticketIntent);
                                break;
                            case 4:
                                Intent intent = new Intent(DoctorProfileActivity.this, DoctorHelp.class);
                                intent.putExtra(Login.FULLNAME, fullName);
                                startActivity(intent);
                                break;

                        }


                        Log.d(TAG, "Badge " + drawerItem.getIdentifier());
//                        if (badgeable.getBadge() != null) {
//                            // учтите, не делайте так, если ваш бейдж содержит символ "+"
//                            try {
//                                int badge = Integer.valueOf(badgeable.getBadge());
//                                Log.d(TAG, "Badge " + drawerItem.getIdentifier());
//
//                                if (badge > 0) {
//                                    drawerResult.updateBadge(String.valueOf(badge - 1), position);
//                                }
//                            } catch (Exception e) {
//                                Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
//                            }
//                        }
//                    }
                    }
                }
            });

            drawer.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                @Override
                // Обработка длинного клика, например, только для SecondaryDrawerItem
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                    if (drawerItem instanceof SecondaryDrawerItem) {
                        //Toast.makeText(DoctorProfileActivity.this, DoctorProfileActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(DoctorProfileActivity.this, DoctorProfileActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            drawer.build();
        }
    }

    public void loadFreshTicket(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        savedText = sharedPreferences.getString(SAVED_TEXT, "0");
    }

    public void saveFreshTicket(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(SAVED_TEXT, String.valueOf(arrayFreshTicket[0]));
        ed.commit();
    }

//    @Override
//    public void onBackPressed() {
////        saveFreshTicket();
//        super.onBackPressed();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_header, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}
