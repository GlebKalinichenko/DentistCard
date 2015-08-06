package com.example.gleb.profileactivities;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gleb.connection.ConnectionRegistrator;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.viewpagers.AdminViewPagerAdapter;
import com.example.gleb.viewpagers.ParticientViewPagerAdapter;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.sun.mail.pop3.POP3Store;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;

/**
 * Created by gleb on 14.07.15.
 */
public class ParticientProfileActivity extends ProfilePattern {
    public static final String EMAIL = "Email";
    private CharSequence[] Titles = {"Пациенты", "Врачи", "Рекомендации", "Смены", "Билеты"};
    private int Numboftabs = 5;
    public String emailParticient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particient_activity_main);

        emailParticient = getIntent().getStringExtra(ParticientProfileActivity.EMAIL);

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
                InputMethodManager inputMethodManager = (InputMethodManager) ParticientProfileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(ParticientProfileActivity.this.getCurrentFocus().getWindowToken(), 0);
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

                    switch(item){
                        case 1: break;
                        case 2:
                            Intent intent = new Intent(ParticientProfileActivity.this, ConnectionRegistrator.class);
                            intent.putExtra(ConnectionRegistrator.EMAIL, emailParticient);
                            intent.putExtra(ConnectionRegistrator.PROFILE, "particient");
                            startActivity(intent);
                            break;
                    }
//                    if (badgeable.getBadge() != null) {
//                        // учтите, не делайте так, если ваш бейдж содержит символ "+"
//                        try {
//                            int badge = Integer.valueOf(badgeable.getBadge());
//                            if (badge > 0) {
//                                drawerResult.updateBadge(String.valueOf(badge - 1), position);
//                            }
//                        } catch (Exception e) {
//                            Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
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
                    Toast.makeText(ParticientProfileActivity.this, ParticientProfileActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        // Creating The AdminViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ParticientViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

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
