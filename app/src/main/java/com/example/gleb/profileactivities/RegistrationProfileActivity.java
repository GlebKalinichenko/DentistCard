package com.example.gleb.profileactivities;

import android.app.Activity;
import android.content.Intent;
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

import com.example.gleb.autoresationregistrator.Autoresation;
import com.example.gleb.charts.ChartActivity;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.viewpagers.RegistrationViewPagerAdapter;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

/**
 * Created by gleb on 14.07.15.
 */
public class RegistrationProfileActivity extends ProfilePattern {
    public static final String TAG = "TAG";
    private CharSequence[] Titles = {
        "Смены", "Врачи", "Пациенты", "Билеты", "Регистрации", "Расписание"
    };
    private int Numboftabs = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                InputMethodManager inputMethodManager = (InputMethodManager) RegistrationProfileActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(RegistrationProfileActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(RegistrationProfileActivity.this, RegistrationProfileActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                if (drawerItem instanceof Badgeable) {
                    Badgeable badgeable = (Badgeable) drawerItem;
                    int item = drawerItem.getIdentifier();
                    switch(item){
                        case 2:
                            //new Loader("pop.yandex.ru", "pop3", "Makbluming@yandex.ua", "0954023873").execute();
                            Intent intent = new Intent(RegistrationProfileActivity.this, Autoresation.class);
                            startActivity(intent);
                            Log.d(TAG, "RegistrationProfileActivity");
                            break;

                        case 3:
                            Intent chartIntent = new Intent(RegistrationProfileActivity.this, ChartActivity.class);
                            startActivity(chartIntent);
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
                    Toast.makeText(RegistrationProfileActivity.this, RegistrationProfileActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        // Creating The AdminViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new RegistrationViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

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
        public String pop3Host;
        public String storeType;
        public String user;
        public String password;

        public Loader(String pop3Host, String storeType, String user, String password) {
            this.pop3Host = pop3Host;
            this.storeType = storeType;
            this.user = user;
            this.password = password;
        }

        @Override
        protected String[] doInBackground(String... params) {
            Properties props = new Properties();
            props.put("mail.smtp.port", 993);
            props.put("mail.smtp.socketFactory.port", 993);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.store.protocol", "imaps");
            try {
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect("imap.yandex.ru", "Makbluming@yandex.ua", "0954023873");
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                Message messages[] = inbox.search(ft);

                for (Message msg : messages) {
                    Address[] in = msg.getFrom();
                    for (Address address : in) {
                        System.out.println("FROM:" + address.toString());
                    }

                    Object content = msg.getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        Log.d(TAG, "SENT DATE: " + msg.getSentDate());
                        Log.d(TAG, "SUBJECT: " + msg.getSubject());
                        Log.d(TAG, "Content " + body);
                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
//                String mp = (String) msg.getContent();
                        Log.d(TAG, "SENT DATE: " + msg.getSentDate());
                        Log.d(TAG, "SUBJECT: " + msg.getSubject());
                        Log.d(TAG, "Content " + bp.getContent());

                    }
                }

//                Multipart mp = (Multipart) msg.getContent();
//                BodyPart bp = mp.getBodyPart(0);
////                String mp = (String) msg.getContent();
//                System.out.println("SENT DATE: " + msg.getSentDate());
//                System.out.println("SUBJECT: " + msg.getSubject());
//                Log.d(TAG, "Content " + msg.getContent().toString());
////                System.out.println("CONTENT: " + mp);
            } catch (Exception mex) {
                mex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {

        }
    }

    private String parseAddresses(Address[] address) {

        String listOfAddress = "";
        if ((address == null) || (address.length < 1))
            return null;
        if (!(address[0] instanceof InternetAddress))
            return null;

        for (int i = 0; i < address.length; i++) {
            InternetAddress internetAddress =
                    (InternetAddress) address[0];
            listOfAddress += internetAddress.getAddress()+",";
        }
        return listOfAddress;
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
