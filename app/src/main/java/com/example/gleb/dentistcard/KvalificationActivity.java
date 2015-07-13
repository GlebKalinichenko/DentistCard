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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.KvalificationAdapter;
import com.example.gleb.insert.InsertKvalification;
import com.example.gleb.tables.Kvalification;
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
 * Created by gleb on 08.07.15.
 */
public class KvalificationActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private String[] arrayKvalifications = null;
    private int[] arrayIdKvalifications = null;
    private KvalificationAdapter adapter;
    public EditText oldKvalificationEditText;
    public EditText newKvalificationEditText;

    private List<Kvalification> kvalifications;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvalificationactivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        kvalifications = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KvalificationActivity.this, InsertKvalification.class);
                startActivity(intent);
            }
        });

        new Loader().execute();

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Kvalifications);
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
                InputMethodManager inputMethodManager = (InputMethodManager) KvalificationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(KvalificationActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(KvalificationActivity.this, KvalificationActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(KvalificationActivity.this, KvalificationActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(KvalificationActivity.this)
                                .title(R.string.UpdateKvalification)
                                .customView(R.layout.update_kvalification, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        new Updater().execute();
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();

                        oldKvalificationEditText = (EditText) v.findViewById(R.id.oldKvalificationEditText);
                        oldKvalificationEditText.append(arrayKvalifications[position]);
                        newKvalificationEditText = (EditText) v.findViewById(R.id.newKvalificationEditText);
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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/KvalificationScript.php");
            //Fields of table PostsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayKvalifications = new String[array.length()];
                arrayIdKvalifications = new int[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    int id = jObject.getInt("IdKvalification");
                    String kvalification = jObject.getString("Kvalification");
                    arrayIdKvalifications[i] = id;
                    arrayKvalifications[i] = kvalification;

                    kvalifications.add(new Kvalification(id, kvalification));
                    Log.d(TAG, arrayKvalifications[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayKvalifications;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new KvalificationAdapter(kvalifications);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table kvalifications
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldKvalification = oldKvalificationEditText.getText().toString();
            String newKvalification = newKvalificationEditText.getText().toString();

            Log.d(TAG, "Old Kvalification " + oldKvalification);
            Log.d(TAG, "New Kvalification " + newKvalification);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateKvalificationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldKvalification", oldKvalification);
                json.put("newKvalification", newKvalification);
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
     * Delete record from table kvalifications
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionKvalification;

        public Deleter(int positionKvalification) {
            this.positionKvalification = positionKvalification;
        }

        @Override
        protected String doInBackground(String... params) {
            String kvalification = arrayKvalifications[positionKvalification];
            Log.d(TAG, kvalification);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteKvalificationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("kvalification", kvalification);
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
