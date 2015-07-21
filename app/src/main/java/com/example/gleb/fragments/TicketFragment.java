package com.example.gleb.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.TicketAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertTicket;
import com.example.gleb.tables.Ticket;
import com.mikepenz.materialdrawer.Drawer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 13.07.15.
 */
public class TicketFragment extends Fragment {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdTickets = null;
    private String[] arrayDoctorKod = null;
    private String[] arrayParticientKod = null;
    private String[] arrayDateReception = null;
    public EditText oldDoctorKodEditText;
    public EditText oldDateReceptionEditText;
    public EditText newDateReceptionEditText;
    public EditText oldRegistrationKodEditText;
    public TicketAdapter adapter;

    private int[] arrayOldIdDoctor = null;
    private int[] arrayOldIdRegistration = null;

    public String[] arrayDoctorSpinner;
    public int[] arrayIdDoctorSpinner;
    public String[] arrayRegistrationSpinner;
    public int[] arrayIdRegistrationSpinner;
    public Spinner doctorKodSpinner;
    public Spinner registrationKodSpinner;

    private List<Ticket> tickets;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    private String fullName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ticketactivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        tickets = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertTicket.class);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupDoctor().execute();
                        new LookupRegistration().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdateTicket)
                                .customView(R.layout.update_ticket, wrapInScrollView)
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
                        doctorKodSpinner = (Spinner) v.findViewById(R.id.doctorKodSpinner);
                        registrationKodSpinner = (Spinner) v.findViewById(R.id.registrationKodSpinner);

                        oldDoctorKodEditText = (EditText) v.findViewById(R.id.oldDoctorKodEditText);
                        oldDoctorKodEditText.append(arrayDoctorKod[position]);

                        oldRegistrationKodEditText = (EditText) v.findViewById(R.id.oldRegistrationKodEditText);
                        oldRegistrationKodEditText.append(arrayParticientKod[position]);

                        oldDateReceptionEditText = (EditText) v.findViewById(R.id.oldDateReceptionEditText);
                        oldDateReceptionEditText.append(String.valueOf(arrayDateReception[position]));
                        newDateReceptionEditText = (EditText) v.findViewById(R.id.newDateReceptionEditText);

                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {
                        positions.add(position);
                        actionMode = getActivity().startActionMode(callback);
                    }
                })
        );


        return v;
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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectTicketLookup.php");
            //Fields of table tickets

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdTickets = new int[array.length()];
                arrayDoctorKod = new String[array.length()];
                arrayParticientKod = new String[array.length()];
                arrayDateReception = new String[array.length()];
                arrayOldIdDoctor = new int[array.length()];
                arrayOldIdRegistration = new int[array.length()];


                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayOldIdDoctor[i] = jObject.getInt("IdDoctor");
                    arrayOldIdRegistration[i] = jObject.getInt("IdRegistration");
                    arrayDoctorKod[i] = jObject.getString("Doctor");
                    arrayParticientKod[i] = jObject.getString("Particient");
                    arrayDateReception[i] = jObject.getString("DateReception");

                    tickets.add(new Ticket(jObject.getString("Doctor"), jObject.getString("Particient"),
                            jObject.getString("DateReception")));

                    Log.d(TAG, arrayDateReception[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateReception;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new TicketAdapter(tickets);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table PostsActivity
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldDoctorPosition;
        public int oldRegistrationPosition;

        public Updater(int oldPosition) {
            this.oldDoctorPosition = oldPosition;
            this.oldRegistrationPosition = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldDoctorKod = oldDoctorKodEditText.getText().toString();
            String oldRegistrationKod = oldRegistrationKodEditText.getText().toString();

            String oldDateReception = oldDateReceptionEditText.getText().toString();
            String newDateReception = newDateReceptionEditText.getText().toString();

            int doctorPosition = doctorKodSpinner.getSelectedItemPosition();
            int registrationPosition = registrationKodSpinner.getSelectedItemPosition();

            Log.d(TAG, "Old DoctorKod " + oldDoctorKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDoctorKod", arrayOldIdDoctor[oldDoctorPosition]);
                json.put("newDoctorKod", arrayIdDoctorSpinner[doctorPosition]);

                json.put("oldRegistrationKod", arrayOldIdRegistration[oldRegistrationPosition]);
                json.put("newRegistrationKod", arrayIdRegistrationSpinner[registrationPosition]);

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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    public class Deleter extends AsyncTask<String, String, String>{
        private int position;

        public Deleter(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {
            int doctorKod = arrayOldIdDoctor[position];
            int registrationKod = arrayOldIdRegistration[position];
            String dateReception = arrayDateReception[position];

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("DoctorKod", doctorKod);
                json.put("RegistrationKod", registrationKod);
                json.put("DateReception", dateReception);
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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    public class LookupDoctor extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DoctorScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDoctorSpinner = new String[array.length()];
                arrayIdDoctorSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String doctor = jObject.getString("FIO");
                    int idDoctor = jObject.getInt("IdDoctor");

                    arrayDoctorSpinner[i] = doctor;
                    arrayIdDoctorSpinner[i] = idDoctor;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDoctorSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, "LookupDoctor " + arrayDoctorSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayDoctorSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            doctorKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupRegistration extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/RegistrationScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayRegistrationSpinner = new String[array.length()];
                arrayIdRegistrationSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String registration = jObject.getString("DateRegistration");
                    int idRegistration = jObject.getInt("IdRegistration");

                    arrayRegistrationSpinner[i] = registration;
                    arrayIdRegistrationSpinner[i] = idRegistration;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayRegistrationSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayRegistrationSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            registrationKodSpinner.setAdapter(adapterSpinner);

        }
    }


}
