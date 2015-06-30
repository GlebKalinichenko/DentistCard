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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gleb.adapters.RegistrationAdapter;
import com.example.gleb.adapters.TicketAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertTicket;

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
public class Tickets extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdTickets = null;
    private String[] arrayDoctorKod = null;
    private String[] arrayRegistrationKod = null;
    private String[] arrayDateReception = null;
    public EditText oldDoctorKodEditText;
    public EditText newDoctorKodEditText;
    public EditText oldDateReceptionEditText;
    public EditText newDateReceptionEditText;
    public EditText oldRegistrationKodEditText;
    public EditText newRegistrationKodEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tickets);

        listView = (ListView) findViewById(R.id.ticketListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheadertickets, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(Tickets.this);
                View dialogView = LayoutInflater.from(Tickets.this).inflate(R.layout.update_ticket, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldDoctorKodEditText = (EditText) dialog.findViewById(R.id.oldDoctorKodEditText);
                oldDoctorKodEditText.append(String.valueOf(arrayDoctorKod[position - 1]));
                newDoctorKodEditText = (EditText) dialog.findViewById(R.id.newDoctorKodEditText);

                oldRegistrationKodEditText = (EditText) dialog.findViewById(R.id.oldRegistrationKodEditText);
                oldRegistrationKodEditText.append(String.valueOf(arrayRegistrationKod[position - 1]));
                newRegistrationKodEditText = (EditText) dialog.findViewById(R.id.newRegistrationKodEditText);

                oldDateReceptionEditText = (EditText) dialog.findViewById(R.id.oldDateReceptionEditText);
                oldDateReceptionEditText.append(arrayDateReception[position - 1]);
                newDateReceptionEditText = (EditText) dialog.findViewById(R.id.newDateReceptionEditText);

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

            String oldRegistrationKod = oldRegistrationKodEditText.getText().toString();
            String newRegistrationKod = newRegistrationKodEditText.getText().toString();

            String oldDateReception = oldDateReceptionEditText.getText().toString();
            String newDateReception = newDateReceptionEditText.getText().toString();

            Log.d(TAG, "Old DoctorKod " + oldDoctorKod);
            Log.d(TAG, "New DoctorKod " + newDoctorKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDoctorKod", oldDoctorKod);
                json.put("newDoctorKod", newDoctorKod);

                json.put("oldRegistrationKod", oldRegistrationKod);
                json.put("newRegistrationKod", newRegistrationKod);

                json.put("oldDateReception", oldDateReception);
                json.put("newDateReception", newDateReception);

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
        private int positionidRegistration;

        public Deleter(int positionidRegistration) {
            this.positionidRegistration = positionidRegistration;
        }

        @Override
        protected String doInBackground(String... params) {
            int idTicket = arrayIdTickets[positionidRegistration];
            Log.d(TAG, String.valueOf(idTicket));

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("IdTicket", idTicket);
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
     * MultiChoice for delete record and add new record in table tickets
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> tickets = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                tickets.add(position);
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
                    for (int i = 0; i < tickets.size(); i++) {
                        Log.d(TAG, String.valueOf(arrayIdTickets[tickets.get(i) - 1]));
                        new Deleter(tickets.get(i) - 1).execute();

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
            Intent intent = new Intent(this, InsertTicket.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectTicketLookup.php");
            //Fields of table tickets

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTickets = new int[array.length()];
                arrayDoctorKod = new String[array.length()];
                arrayRegistrationKod = new String[array.length()];
                arrayDateReception = new String[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    //arrayIdTickets[i] = jObject.getInt("IdTicket");
                    arrayDoctorKod[i] = jObject.getString("FIO");
                    arrayRegistrationKod[i] = jObject.getString("DateRegistration");
                    arrayDateReception[i] = jObject.getString("DateReception");

                    Log.d(TAG, arrayDateReception[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateReception;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new TicketAdapter(getBaseContext(), arrayDoctorKod, arrayRegistrationKod, arrayDateReception);
            listView.setAdapter(adapter);

        }
    }
}
