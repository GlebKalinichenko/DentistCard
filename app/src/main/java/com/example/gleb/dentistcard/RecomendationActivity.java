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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.RecomendationAdapter;
import com.example.gleb.insert.InsertRecommendation;
import com.example.gleb.tables.Recomendation;
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
 * Created by gleb on 12.07.15.
 */
public class RecomendationActivity extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private RecomendationAdapter adapter;
    private String[] arrayTicketKod = null;
    private int[] arrayIdRecommendation = null;
    private String[] arrayDiagnoseKod = null;
    private String[] arrayTherapy = null;
    private String[] arrayComplaints = null;
    private String[] arrayHistoryIllness = null;
    private String[] arrayObjectiveValues = null;
    public EditText oldTicketKodEditText;
    public EditText oldDiagnoseKodEditText;
    public EditText oldTherapyEditText;
    public EditText newTherapyEditText;
    public EditText oldComplaintsEditText;
    public EditText newComplaintsEditText;
    public EditText oldHistoryIllnessEditText;
    public EditText newHistoryIllnessEditText;
    public EditText oldObjectiveValuesEditText;
    public EditText newObjectiveValuesEditText;
    public Spinner ticketKodSpinner;
    public Spinner diagnoseKodSpinner;

    public String[] arrayDateReceptionSpinner;
    public int[] arrayIdTicketSpinner;

    public String[] arrayDiagnoseSpinner;
    public int[] arrayIdDiagnoseSpinner;

    public int[] arrayOldIdTicket;
    public int[] arrayOldIdDiagnose;

    private List<Recomendation> recomendations;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recomendationacitivity);

        rv = (RecyclerView) findViewById(R.id.rv);
        recomendations = new ArrayList<>();
        addImageButton = (ImageButton) findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecomendationActivity.this, InsertRecommendation.class);
                startActivity(intent);
            }
        });

        //Initialise ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.RecommendationFull);
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
                InputMethodManager inputMethodManager = (InputMethodManager) RecomendationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(RecomendationActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                    Toast.makeText(RecomendationActivity.this, RecomendationActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RecomendationActivity.this, RecomendationActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupTicket().execute();
                        new LookupDiagnose().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(RecomendationActivity.this)
                                .title(R.string.UpdateRecomendation)
                                .customView(R.layout.update_recomendation, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        new Updater(position).execute();
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();
                        ticketKodSpinner = (Spinner) v.findViewById(R.id.ticketKodSpinner);
                        diagnoseKodSpinner = (Spinner) v.findViewById(R.id.diagnoseKodSpinner);
                        oldTicketKodEditText = (EditText) v.findViewById(R.id.oldTicketKodEditText);
                        oldTicketKodEditText.append(arrayTicketKod[position]);
                        oldDiagnoseKodEditText = (EditText) v.findViewById(R.id.oldDiagnoseKodEditText);
                        oldDiagnoseKodEditText.append(arrayDiagnoseKod[position]);

                        oldTherapyEditText = (EditText) v.findViewById(R.id.oldTherapyEditText);
                        oldTherapyEditText.append(arrayTherapy[position]);
                        newTherapyEditText = (EditText) v.findViewById(R.id.newTherapyEditText);

                        oldComplaintsEditText = (EditText) v.findViewById(R.id.oldComplaintsEditText);
                        oldComplaintsEditText.append(arrayComplaints[position]);
                        newComplaintsEditText = (EditText) v.findViewById(R.id.newComplaintsEditText);

                        oldHistoryIllnessEditText = (EditText) v.findViewById(R.id.oldHistoryIllnessEditText);
                        oldHistoryIllnessEditText.append(arrayHistoryIllness[position]);
                        newHistoryIllnessEditText = (EditText) v.findViewById(R.id.newHistoryIllnessEditText);

                        oldObjectiveValuesEditText = (EditText) v.findViewById(R.id.oldObjectiveValuesEditText);
                        oldObjectiveValuesEditText.append(arrayObjectiveValues[position]);
                        newObjectiveValuesEditText = (EditText) v.findViewById(R.id.newObjectiveValuesEditText);

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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectRecomendationLookup.php");
            //Fields of table Recommendation

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdRecommendation = new int[array.length()];
                arrayTicketKod = new String[array.length()];
                arrayDiagnoseKod = new String[array.length()];
                arrayTherapy = new String[array.length()];
                arrayComplaints = new String[array.length()];
                arrayHistoryIllness = new String[array.length()];
                arrayObjectiveValues = new String[array.length()];
                arrayOldIdTicket = new int[array.length()];
                arrayOldIdDiagnose = new int[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayOldIdTicket[i] = jObject.getInt("IdTicket");
                    arrayOldIdDiagnose[i] = jObject.getInt("IdDiagnose");
                    arrayTicketKod[i] = jObject.getString("DateReception");
                    arrayDiagnoseKod[i] = jObject.getString("Diagnose");
                    arrayTherapy[i] = jObject.getString("Therapy");
                    arrayHistoryIllness[i] = jObject.getString("HistoryIllness");
                    arrayObjectiveValues[i] = jObject.getString("ObjectiveValues");
                    arrayComplaints[i] = jObject.getString("Complaints");

                    recomendations.add(new Recomendation(jObject.getString("DateReception"), jObject.getString("Diagnose"),
                            jObject.getString("Therapy"), jObject.getString("HistoryIllness"), jObject.getString("ObjectiveValues"),
                            jObject.getString("Complaints")));

                    Log.d(TAG, arrayTherapy[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayTherapy;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new RecomendationAdapter(recomendations);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table PostsActivity
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldPositionTicket;
        public int oldPositionDiagnose;

        public Updater(int oldPosition) {
            this.oldPositionTicket = oldPosition;
            this.oldPositionDiagnose = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldObjectiveValues = oldObjectiveValuesEditText.getText().toString();
            String newObjectiveValues = newObjectiveValuesEditText.getText().toString();

            String oldTicketKod = oldTicketKodEditText.getText().toString();

            String oldDiagnoseKod = oldDiagnoseKodEditText.getText().toString();

            String oldTherapy = oldTherapyEditText.getText().toString();
            String newTherapy = newTherapyEditText.getText().toString();

            String oldComplaints = oldComplaintsEditText.getText().toString();
            String newComplaints = newComplaintsEditText.getText().toString();

            String oldHistoryIllness = oldHistoryIllnessEditText.getText().toString();
            String newHistoryIllness = newHistoryIllnessEditText.getText().toString();
            int positionTicketSpinner = ticketKodSpinner.getSelectedItemPosition();
            int positionDiagnoseSpinner = diagnoseKodSpinner.getSelectedItemPosition();

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateRecomendationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldTicketKod", arrayOldIdTicket[oldPositionTicket]);
                json.put("newTicketKod", arrayIdTicketSpinner[positionTicketSpinner]);

                json.put("oldDiagnoseKod", arrayOldIdDiagnose[oldPositionDiagnose]);
                json.put("newDiagnoseKod", arrayIdDiagnoseSpinner[positionDiagnoseSpinner]);

                json.put("oldTherapy", oldTherapy);
                json.put("newTherapy", newTherapy);

                json.put("oldComplaints", oldComplaints);
                json.put("newComplaints", newComplaints);

                json.put("oldHistoryIllness", oldHistoryIllness);
                json.put("newHistoryIllness", newHistoryIllness);

                json.put("oldObjectiveValues", oldObjectiveValues);
                json.put("newObjectiveValues", newObjectiveValues);

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

    public class Deleter extends AsyncTask<String, String, String>{
        private int positionTherapy;

        public Deleter(int positionTherapy) {
            this.positionTherapy = positionTherapy;
        }

        @Override
        protected String doInBackground(String... params) {
            String therapy = arrayTherapy[positionTherapy];
            Log.d(TAG, therapy);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteRecomendationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("Therapy", therapy);
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

    public class LookupTicket extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/TicketScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDateReceptionSpinner = new String[array.length()];
                arrayIdTicketSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String dateReception = jObject.getString("DateReception");
                    int idTicket = jObject.getInt("IdTicket");

                    arrayDateReceptionSpinner[i] = dateReception;
                    arrayIdTicketSpinner[i] = idTicket;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateReceptionSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayDateReceptionSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ticketKodSpinner.setAdapter(adapterSpinner);

        }
    }

    public class LookupDiagnose extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DiagnoseScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDiagnoseSpinner = new String[array.length()];
                arrayIdDiagnoseSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String diagnose = jObject.getString("Diagnose");
                    int idDiagnose = jObject.getInt("IdDiagnose");

                    arrayDiagnoseSpinner[i] = diagnose;
                    arrayIdDiagnoseSpinner[i] = idDiagnose;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDiagnoseSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayDiagnoseSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            diagnoseKodSpinner.setAdapter(adapterSpinner);

        }
    }

}
