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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gleb.adapters.DoctorAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertDiagnose;
import com.example.gleb.insert.InsertDoctor;

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
 * Created by Gleb on 06.06.2015.
 */
public class Doctors extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    public String[] arrayFIO = null;
    public int[] arrayIdDoctors = null;
    public int[] arrayPostKod = null;
    public int[] arrayDepartmentKod = null;
    public int[] arrayKvalificationKod = null;
    public int[] arrayExpiriences = null;
    private DoctorAdapter adapter;
    public EditText oldFIOEditText;
    public EditText newFIOEditText;
    public EditText oldPostKodEditText;
    public EditText newPostKodEditText;
    public EditText oldKvalificationKodEditText;
    public EditText newKvalificationKodEditText;
    public EditText oldDepartmentKodEditText;
    public EditText newDepartmentKodEditText;
    public EditText oldExpirienceEditText;
    public EditText newExpirienceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors);

        listView = (ListView) findViewById(R.id.doctorListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderdoctors, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Doctors.this);
                View dialogView = LayoutInflater.from(Doctors.this).inflate(R.layout.update_doctors, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldFIOEditText = (EditText) dialog.findViewById(R.id.oldFIOEditText);
                oldFIOEditText.append(arrayFIO[position - 1]);
                newFIOEditText = (EditText) dialog.findViewById(R.id.newFIOEditText);

                oldPostKodEditText = (EditText) dialog.findViewById(R.id.oldPostKodEditText);
                oldPostKodEditText.append(String.valueOf(arrayPostKod[position - 1]));
                newPostKodEditText = (EditText) dialog.findViewById(R.id.newPostKodEditText);

                oldKvalificationKodEditText = (EditText) dialog.findViewById(R.id.oldKvalificationKodEditText);
                oldKvalificationKodEditText.append(String.valueOf(arrayKvalificationKod[position - 1]));
                newKvalificationKodEditText = (EditText) dialog.findViewById(R.id.newKvalificationKodEditText);

                oldDepartmentKodEditText = (EditText) dialog.findViewById(R.id.oldDepartmentKodEditText);
                oldDepartmentKodEditText.append(String.valueOf(arrayDepartmentKod[position - 1]));
                newDepartmentKodEditText = (EditText) dialog.findViewById(R.id.newDepartmentKodEditText);

                oldExpirienceEditText = (EditText) dialog.findViewById(R.id.oldExpirienceEditText);
                oldExpirienceEditText.append(String.valueOf(arrayExpiriences[position - 1]));
                newExpirienceEditText = (EditText) dialog.findViewById(R.id.newExpirienceEditText);

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
            Intent intent = new Intent(this, InsertDoctor.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update record from table doctors
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldFIO = oldFIOEditText.getText().toString();
            String newFIO = newFIOEditText.getText().toString();

            String oldPostKod = oldPostKodEditText.getText().toString();
            String newPostKod = newPostKodEditText.getText().toString();

            String oldKvalificationKod = oldKvalificationKodEditText.getText().toString();
            String newKvalificationKod = newKvalificationKodEditText.getText().toString();

            String oldDepartmentKod = oldDepartmentKodEditText.getText().toString();
            String newDepartmentKod = newDepartmentKodEditText.getText().toString();

            String oldExpirience = oldExpirienceEditText.getText().toString();
            String newExpirience = newExpirienceEditText.getText().toString();

            Log.d(TAG, "Old Diagnose " + oldFIO);
            Log.d(TAG, "New Diagnose " + newFIO);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateDoctorScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldFIO", oldFIO);
                json.put("newFIO", newFIO);
                json.put("oldPostKod", oldPostKod);
                json.put("newPostKod", newPostKod);
                json.put("oldKvalificationKod", oldKvalificationKod);
                json.put("newKvalificationKod", newKvalificationKod);
                json.put("oldDepartmentKod", oldDepartmentKod);
                json.put("newDepartmentKod", newDepartmentKod);
                json.put("oldExpirience", oldExpirience);
                json.put("newExpirience", newExpirience);
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
     * Delete record from table doctors
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionFIO;

        public Deleter(int positionFIO) {
            this.positionFIO = positionFIO;
        }

        @Override
        protected String doInBackground(String... params) {
            String fio = arrayFIO[positionFIO];
            Log.d(TAG, fio);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteDoctorScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("fio", fio);
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
     * MultiChoice for delete record and add new record in table doctors
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> doctors = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                doctors.add(position);
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
                    for (int i = 0; i < doctors.size(); i++) {
                        Log.d(TAG, arrayFIO[doctors.get(i) - 1]);
                        new Deleter(doctors.get(i) - 1).execute();

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

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DoctorScript.php");
            //Fields of table Doctors


            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayFIO = new String[array.length()];
                arrayIdDoctors = new int[array.length()];
                arrayDepartmentKod = new int[array.length()];
                arrayKvalificationKod = new int[array.length()];
                arrayExpiriences = new int[array.length()];
                arrayPostKod = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdDoctors[i] = jObject.getInt("IdDoctor");
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayDepartmentKod[i] = jObject.getInt("DepartmentKod");
                    arrayKvalificationKod[i] = jObject.getInt("KvalificationKod");
                    arrayPostKod[i] = jObject.getInt("PostKod");
                    arrayExpiriences[i] = jObject.getInt("Expirience");


                    Log.d(TAG, arrayFIO[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFIO;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            Log.d(TAG, String.valueOf(arrayPostKod[0]));
            adapter = new DoctorAdapter(getBaseContext(), arrayFIO, arrayPostKod, arrayKvalificationKod, arrayDepartmentKod, arrayExpiriences);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }


}
