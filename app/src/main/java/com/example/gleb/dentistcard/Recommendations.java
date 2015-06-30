package com.example.gleb.dentistcard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gleb.adapters.ChangeAdapter;
import com.example.gleb.adapters.RecommendationAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertRecommendation;

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

/**
 * Created by Gleb on 13.06.2015.
 */
public class Recommendations extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayTicketKod = null;
    private int[] arrayIdRecommendation = null;
    private int[] arrayDiagnoseKod = null;
    private String[] arrayTherapy = null;
    private String[] arrayComplaints = null;
    private String[] arrayHistoryIllness = null;
    private String[] arrayObjectiveValues = null;
    public EditText oldTicketKodEditText;
    public EditText newTicketKodEditText;
    public EditText oldDiagnoseKodEditText;
    public EditText newDiagnoseKodEditText;
    public EditText oldTherapyEditText;
    public EditText newTherapyEditText;
    public EditText oldComplaintsEditText;
    public EditText newComplaintsEditText;
    public EditText oldHistoryIllnessEditText;
    public EditText newHistoryIllnessEditText;
    public EditText oldObjectiveValuesEditText;
    public EditText newObjectiveValuesEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation);

        listView = (ListView) findViewById(R.id.recommendationListView);
        View header = (View) getLayoutInflater().inflate(R.layout.recommendation, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Recommendations.this);
                View dialogView = LayoutInflater.from(Recommendations.this).inflate(R.layout.update_recomendation, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldTicketKodEditText = (EditText) dialog.findViewById(R.id.oldTicketKodEditText);
                oldTicketKodEditText.append(String.valueOf(arrayTicketKod[position - 1]));
                newTicketKodEditText = (EditText) dialog.findViewById(R.id.newTicketKodEditText);

                oldDiagnoseKodEditText = (EditText) dialog.findViewById(R.id.oldDiagnoseKodEditText);
                oldDiagnoseKodEditText.append(String.valueOf(arrayDiagnoseKod[position - 1]));
                newDiagnoseKodEditText = (EditText) dialog.findViewById(R.id.newDiagnoseKodEditText);

                oldTherapyEditText = (EditText) dialog.findViewById(R.id.oldTherapyEditText);
                oldTherapyEditText.append(arrayTherapy[position - 1]);
                newTherapyEditText = (EditText) dialog.findViewById(R.id.newTherapyEditText);

                oldComplaintsEditText = (EditText) dialog.findViewById(R.id.oldComplaintsEditText);
                oldComplaintsEditText.append(arrayComplaints[position - 1]);
                newComplaintsEditText = (EditText) dialog.findViewById(R.id.newComplaintsEditText);

                oldHistoryIllnessEditText = (EditText) dialog.findViewById(R.id.oldHistoryIllnessEditText);
                oldHistoryIllnessEditText.append(arrayHistoryIllness[position - 1]);
                newHistoryIllnessEditText = (EditText) dialog.findViewById(R.id.newHistoryIllnessEditText);

                oldObjectiveValuesEditText = (EditText) dialog.findViewById(R.id.oldObjectiveValuesEditText);
                oldObjectiveValuesEditText.append(arrayObjectiveValues[position - 1]);
                newObjectiveValuesEditText = (EditText) dialog.findViewById(R.id.newObjectiveValuesEditText);

                imageButton = (ImageButton) dialog.findViewById(R.id.updateImageButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Click");
                        new Updater().execute();
                    }
                });
            }
        });

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoice(listView));
    }

    /**
     * Update record from table Posts
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldObjectiveValues = oldObjectiveValuesEditText.getText().toString();
            String newObjectiveValues = newObjectiveValuesEditText.getText().toString();

            String oldTicketKod = oldTicketKodEditText.getText().toString();
            String newTicketKod = newTicketKodEditText.getText().toString();

            String oldDiagnoseKod = oldDiagnoseKodEditText.getText().toString();
            String newDiagnoseKod = newDiagnoseKodEditText.getText().toString();

            String oldTherapy = oldTherapyEditText.getText().toString();
            String newTherapy = newTherapyEditText.getText().toString();

            String oldComplaints = oldComplaintsEditText.getText().toString();
            String newComplaints = newComplaintsEditText.getText().toString();

            String oldHistoryIllness = oldHistoryIllnessEditText.getText().toString();
            String newHistoryIllness = newHistoryIllnessEditText.getText().toString();

            Log.d(TAG, "Old TicketKod " + oldTicketKod);
            Log.d(TAG, "New TicketKod " + newTicketKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateRecomendationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldTicketKod", oldTicketKod);
                json.put("newTicketKod", newTicketKod);

                json.put("oldDiagnoseKod", oldDiagnoseKod);
                json.put("newDiagnoseKod", newDiagnoseKod);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dentist_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addRecord) {
            Intent intent = new Intent(this, InsertRecommendation.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/RecomendationScript.php");
            //Fields of table Recommendation

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdRecommendation = new int[array.length()];
                arrayTicketKod = new int[array.length()];
                arrayDiagnoseKod = new int[array.length()];
                arrayTherapy = new String[array.length()];
                arrayComplaints = new String[array.length()];
                arrayHistoryIllness = new String[array.length()];
                arrayObjectiveValues = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdRecommendation[i] = jObject.getInt("IdRecomendation");
                    arrayTicketKod[i] = jObject.getInt("TicketKod");
                    arrayDiagnoseKod[i] = jObject.getInt("DiagnoseKod");
                    arrayTherapy[i] = jObject.getString("Therapy");
                    arrayHistoryIllness[i] = jObject.getString("HistoryIllness");
                    arrayObjectiveValues[i] = jObject.getString("ObjectiveValues");
                    arrayComplaints[i] = jObject.getString("Complaints");

                    Log.d(TAG, arrayTherapy[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayTherapy;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new RecommendationAdapter(getBaseContext(), arrayTicketKod, arrayDiagnoseKod, arrayTherapy, arrayComplaints, arrayHistoryIllness, arrayObjectiveValues);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }

    /**
     * MultiChoice for delete record and add new record in table therapies
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> therapies = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                therapies.add(position);
                setSubtitle(mode, rows);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.item_add) {
                Log.d(TAG, "Item add");
                Intent intent = new Intent(getBaseContext(), InsertCountry.class);
                startActivity(intent);
            } else {
                if (item.getItemId() == R.id.item_delete) {
                    for (int i = 0; i < therapies.size(); i++) {
                        Log.d(TAG, arrayTherapy[therapies.get(i) - 1]);
                        new Deleter(therapies.get(i) - 1).execute();

                    }
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        private void setSubtitle(ActionMode mode, int selectedCount) {
            switch (selectedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                default:
                    mode.setTitle(String.valueOf(selectedCount));
                    break;
            }
        }
    }

    
}
