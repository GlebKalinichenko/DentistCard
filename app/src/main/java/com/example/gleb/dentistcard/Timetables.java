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

import com.example.gleb.adapters.TimetableAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertTimetable;

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
 * Created by Gleb on 07.06.2015.
 */
public class Timetables extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private TimetableAdapter adapter;
    private int[] arrayIdTimetables = null;
    private String[] arrayDateTimetables = null;
    private int[] arrayDoctorKod = null;
    private int[] arrayChangeKod = null;
    public EditText oldDateWorkEditText;
    public EditText newDateWorkEditText;
    public EditText oldChangeKodEditText;
    public EditText newChangeKodEditText;
    public EditText oldDoctorKodEditText;
    public EditText newDoctorKodEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetables);

        listView = (ListView) findViewById(R.id.timetableListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadertimetables, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Timetables.this);
                View dialogView = LayoutInflater.from(Timetables.this).inflate(R.layout.update_timetable, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldDateWorkEditText = (EditText) dialog.findViewById(R.id.oldDateWorkEditText);
                oldDateWorkEditText.append(arrayDateTimetables[position - 1]);
                newDateWorkEditText = (EditText) dialog.findViewById(R.id.newDateWorkEditText);

                oldDoctorKodEditText = (EditText) dialog.findViewById(R.id.oldDoctorKodEditText);
                oldDoctorKodEditText.append(String.valueOf(arrayDoctorKod[position - 1]));
                newDoctorKodEditText = (EditText) dialog.findViewById(R.id.newDoctorKodEditText);

                oldChangeKodEditText = (EditText) dialog.findViewById(R.id.oldChangeKodEditText);
                oldChangeKodEditText.append(String.valueOf(arrayChangeKod[position - 1]));
                newChangeKodEditText = (EditText) dialog.findViewById(R.id.newChangeKodEditText);

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
            String oldDoctorKod = oldDoctorKodEditText.getText().toString();
            String newDoctorKod = newDoctorKodEditText.getText().toString();

            String oldChangeKod = oldChangeKodEditText.getText().toString();
            String newChangeKod = newChangeKodEditText.getText().toString();

            String oldDateWork = oldDateWorkEditText.getText().toString();
            String newDateWork = newDateWorkEditText.getText().toString();

            Log.d(TAG, "Old DoctorKod " + oldDoctorKod);
            Log.d(TAG, "New DoctorKod " + newDoctorKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateTimetableScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDoctorKod", oldDoctorKod);
                json.put("newDoctorKod", newDoctorKod);

                json.put("oldChangeKod", oldChangeKod);
                json.put("newChangeKod", newChangeKod);

                json.put("oldDateWork", oldDateWork);
                json.put("newDateWork", newDateWork);

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
        private int positionIdTimetable;

        public Deleter(int positionIdTimetable) {
            this.positionIdTimetable = positionIdTimetable;
        }

        @Override
        protected String doInBackground(String... params) {
            int idTimetable = arrayIdTimetables[positionIdTimetable];
            Log.d(TAG, String.valueOf(idTimetable));

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteTimetableScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("IdTimetable", idTimetable);
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
     * MultiChoice for delete record and add new record in table timetables
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> timetables = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                timetables.add(position);
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
                    for (int i = 0; i < timetables.size(); i++) {
                        Log.d(TAG, String.valueOf(arrayIdTimetables[timetables.get(i) - 1]));
                        new Deleter(timetables.get(i) - 1).execute();

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
            Intent intent = new Intent(this, InsertTimetable.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/TimetableScript.php");
            //Fields of table Timetables

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTimetables = new int[array.length()];
                arrayDoctorKod = new int[array.length()];
                arrayChangeKod = new int[array.length()];
                arrayDateTimetables = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdTimetables[i] = jObject.getInt("IdTimetable");
                    arrayDoctorKod[i] = jObject.getInt("DoctorKod");
                    arrayChangeKod[i] = jObject.getInt("ChangeKod");
                    arrayDateTimetables[i] = jObject.getString("DateWork");


                    Log.d(TAG, arrayDateTimetables[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateTimetables;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new TimetableAdapter(getBaseContext(), arrayDateTimetables, arrayDoctorKod, arrayChangeKod);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}
