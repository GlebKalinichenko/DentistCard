package com.example.gleb.autoresationregistrator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gleb.adapters.AutoresationAdapter;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.example.gleb.notifications.NotificationUtils;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

/**
 * Created by gleb on 23.07.15.
 */
public class AutoresationMail extends ActionBarActivity{
    public static final String TAG = "TAG";
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public String email;
    public String password;
    private AutoresationAdapter adapter;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public String[] arrayFrom;
    public String[] arraySubject;
    public String[] arrayContent;
    protected Toolbar toolbar;
    protected ViewPager pager;
    protected SlidingTabLayout tabs;
    protected ActionMode actionMode;
    protected Drawer.Result drawerResult = null;

    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoresation);

        email = getIntent().getStringExtra(AutoresationMail.EMAIL);
        password = getIntent().getStringExtra(AutoresationMail.PASSWORD);
        Log.d(TAG, "AutoresationMail email " + email);
        Log.d(TAG, "AutoresationMail password " + password);

        String host = email.substring(email.lastIndexOf("@") + 1);
        Log.d(TAG, "Host email " + host);

        rv = (RecyclerView) findViewById(R.id.rv);
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);

        LinearLayoutManager llm = new LinearLayoutManager(AutoresationMail.this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        if (host.equals("yandex.ru") || host.equals("yandex.ua")) {
            new Loader("imap.yandex.ru", email, password).execute();
        }
        else{
            if (host.equals("gmail.com")) {
                new Loader("imap.googlemail.com", email, password).execute();
            }
            else{
                if (host.equals("ukr.net")) {
                    new Loader("imap.ukr.net", email, password).execute();
                }
                else{
                    if (host.equals("rambler.ru")) {
                        new Loader("imap.rambler.ru", email, password).execute();
                    }
                }
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.Changes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialise Navigation Drawer
        drawer = new Drawer();
        drawer.withActivity(AutoresationMail.this);
        drawer.withToolbar(toolbar);
        drawer.withActionBarDrawerToggle(true);
        drawer.withHeader(R.layout.drawer_header);


        drawer.addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.drawer_item_tickets).withIcon(FontAwesome.Icon.faw_home).withBadge("+").withIdentifier(1),
                new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad).withBadge("").withIdentifier(2),
                new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("").withIdentifier(3),
                new SectionDrawerItem().withName(R.string.drawer_item_settings),
                new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withIdentifier(5),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(6)
        );

        drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Скрываем клавиатуру при открытии Navigation Drawer
                InputMethodManager inputMethodManager = (InputMethodManager) AutoresationMail.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(AutoresationMail.this.getCurrentFocus().getWindowToken(), 0);
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
                        case 1: break;
                        case 4: break;

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
                    Toast.makeText(AutoresationMail.this, AutoresationMail.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

    }

    public class Loader extends AsyncTask<String, String, String[]> {
        public String imapHost;
        public String user;
        public String password;

        public Loader(String imapHost, String user, String password) {
            this.imapHost = imapHost;
            this.user = user;
            this.password = password;
        }

        @Override
        protected String[] doInBackground(String... params) {
            Properties props = new Properties();
//            props.put("mail.smtp.port", 993);
//            props.put("mail.smtp.socketFactory.port", 993);
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.imap.port", 993);
            props.put("mail.imap.socketFactory.port", 993);
            props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.imap.socketFactory.fallback", "false");
            props.setProperty("mail.store.protocol", "imaps");

            try {
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect(imapHost, email, password);
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                Message messages[] = inbox.search(ft);

                Log.d(TAG, "Новые сообщения " + messages.length);

                if (messages.length > 0){
                    NotificationUtils n = NotificationUtils.getInstance(AutoresationMail.this);
                    n.createInfoNotification("У вас +" + messages.length + " непрочитанных сообщений");

                }

                arrayFrom = new String[messages.length];
                arraySubject = new String[messages.length];
                arrayContent = new String[messages.length];

                for (int i = 0; i < messages.length; i++) {
                    Address[] in = messages[i].getFrom();
                    for (Address address : in) {
                        System.out.println("FROM:" + address.toString());
                        arrayFrom[i] = address.toString();
                    }

                    Object content = messages[i].getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        Log.d(TAG, "SENT DATE: " + messages[i].getSentDate());
                        arraySubject[i] = messages[i].getSubject();
                        Log.d(TAG, "SUBJECT: " + messages[i].getSubject());
                        arrayContent[i] = body;
                        Log.d(TAG, "Content " + body);
                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
                        Log.d(TAG, "SENT DATE: " + messages[i].getSentDate());
                        arraySubject[i] = messages[i].getSubject();
                        Log.d(TAG, "SUBJECT: " + messages[i].getSubject());
                        arrayContent[i] = bp.getContent().toString();
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
            adapter = new AutoresationAdapter(arrayFrom, arraySubject, arrayContent);
            rv.setAdapter(adapter);
        }
    }
}
