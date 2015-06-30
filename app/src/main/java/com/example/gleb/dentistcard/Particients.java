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
import com.example.gleb.adapters.ParticientAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertParticient;

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
public class Particients extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdParticients = null;
    private String[] arrayFIO = null;
    private String[] arrayAddreses = null;
    private int[] arrayCityKod = null;
    private String[] arrayPhoneNumber = null;
    private String[] arrayFIOParent = null;
    private String[] arrayDateBorn = null;
    private ParticientAdapter adapter;
    public EditText oldFIOEditText;
    public EditText newFIOEditText;
    public EditText oldAddressEditText;
    public EditText newAddressEditText;
    public EditText oldDateBornEditText;
    public EditText newDateBornEditText;
    public EditText oldCityKodEditText;
    public EditText newCityKodEditText;
    public EditText oldPhoneEditText;
    public EditText newPhoneEditText;
    public EditText oldFIOParentEditText;
    public EditText newFIOParentEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particients);

        listView = (ListView) findViewById(R.id.particientListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderparticients, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Particients.this);
                View dialogView = LayoutInflater.from(Particients.this).inflate(R.layout.update_particients, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldFIOEditText = (EditText) dialog.findViewById(R.id.oldFIOParticientEditText);
                oldFIOEditText.append(arrayFIO[position - 1]);
                newFIOEditText = (EditText) dialog.findViewById(R.id.newFIOParticientEditText);

                oldAddressEditText = (EditText) dialog.findViewById(R.id.oldAddressEditText);
                oldAddressEditText.append(arrayAddreses[position - 1]);
                newAddressEditText = (EditText) dialog.findViewById(R.id.newAddressEditText);

                oldCityKodEditText = (EditText) dialog.findViewById(R.id.oldCityKodEditText);
                oldCityKodEditText.append(String.valueOf(arrayCityKod[position - 1]));
                newCityKodEditText = (EditText) dialog.findViewById(R.id.newCityKodEditText);

                oldPhoneEditText = (EditText) dialog.findViewById(R.id.oldPhoneEditText);
                oldPhoneEditText.append(arrayPhoneNumber[position - 1]);
                newPhoneEditText = (EditText) dialog.findViewById(R.id.newPhoneEditText);

                oldDateBornEditText = (EditText) dialog.findViewById(R.id.oldDateBornEditText);
                oldDateBornEditText.append(arrayDateBorn[position - 1]);
                newDateBornEditText = (EditText) dialog.findViewById(R.id.newDateBornEditText);

                oldFIOParentEditText = (EditText) dialog.findViewById(R.id.oldFIOParentEditText);
                oldFIOParentEditText.append(arrayFIOParent[position - 1]);
                newFIOParentEditText = (EditText) dialog.findViewById(R.id.newFIOParentEditText);

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
     * Update record from table particients
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldFIO = oldFIOEditText.getText().toString();
            String newFIO = newFIOEditText.getText().toString();

            String oldAddress = oldAddressEditText.getText().toString();
            String newAddress = newAddressEditText.getText().toString();

            String oldCityKod = oldCityKodEditText.getText().toString();
            String newCityKod = newCityKodEditText.getText().toString();

            String oldPhone = oldPhoneEditText.getText().toString();
            String newPhone = newPhoneEditText.getText().toString();

            String oldFIOParent = oldFIOParentEditText.getText().toString();
            String newFIOParent = newFIOParentEditText.getText().toString();

            String oldDateBorn = oldDateBornEditText.getText().toString();
            String newDateBorn = newDateBornEditText.getText().toString();

            Log.d(TAG, "Old Kvalification " + oldFIO);
            Log.d(TAG, "New Kvalification " + newFIO);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateParticientScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldFio", oldFIO);
                json.put("newFio", newFIO);

                json.put("oldAddress", oldAddress);
                json.put("newAddress", newAddress);

                json.put("oldCityKod", oldCityKod);
                json.put("newCityKod", newCityKod);

                json.put("oldPhoneNumber", oldPhone);
                json.put("newPhoneNumber", newPhone);

                json.put("oldDateBorn", oldDateBorn);
                json.put("newDateBorn", newDateBorn);

                json.put("oldFIOParent", oldFIOParent);
                json.put("newFIOParent", newFIOParent);

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
     * Delete record from table Particients
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionPartcient;

        public Deleter(int positionPartcient) {
            this.positionPartcient = positionPartcient;
        }

        @Override
        protected String doInBackground(String... params) {
            String fio = arrayFIO[positionPartcient];
            Log.d(TAG, fio);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteParticientScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fio);
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
     * MultiChoice for delete record and add new record in table particients
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> particients = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                particients.add(position);
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
                    for (int i = 0; i < particients.size(); i++) {
                        Log.d(TAG, arrayFIO[particients.get(i) - 1]);
                        new Deleter(particients.get(i) - 1).execute();

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
            Intent intent = new Intent(this, InsertParticient.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ParticientScript.php");
            //Fields of table Doctors

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdParticients = new int[array.length()];
                arrayFIO = new String[array.length()];
                arrayAddreses = new String[array.length()];
                arrayCityKod = new int[array.length()];
                arrayDateBorn = new String[array.length()];
                arrayFIOParent = new String[array.length()];
                arrayPhoneNumber = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdParticients[i] = jObject.getInt("IdParticient");
                    arrayFIO[i] = jObject.getString("FIO");
                    arrayAddreses[i] = jObject.getString("Address");
                    arrayDateBorn[i] = jObject.getString("DateBorn");
                    arrayPhoneNumber[i] = jObject.getString("PhoneNumber");
                    arrayFIOParent[i] = jObject.getString("FIOParent");
                    arrayCityKod[i] = jObject.getInt("CityKod");

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
            adapter = new ParticientAdapter(getBaseContext(), arrayFIO, arrayAddreses, arrayCityKod, arrayPhoneNumber, arrayFIOParent, arrayDateBorn);
            //adapter = new CountryAdapter(getApplicationContext(), R.layout.countries_item_row, value);
            listView.setAdapter(adapter);

        }
    }
}
