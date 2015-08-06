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
import com.example.gleb.adapters.RegistrationAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertRegistration;
import com.example.gleb.tables.Registration;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 13.07.15.
 */
public class RegistrationFragment extends Fragment {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private int[] arrayIdRegistrations = null;
    private String[] arrayDateRegistrations = null;
    private String[] arrayParticientKod = null;
    public EditText oldDateRegistrationEditText;
    public EditText newDateRegistrationEditText;
    public EditText oldParticientKodEditText;
    public Spinner particientKodSpinner;
    public int[] arrayIdParticientSpinner;
    public String[] arrayFIOParticientSpinner;
    public int[] arrayOldIdParticient;
    public RegistrationAdapter adapter;

    private List<Registration> registrations;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    public String profile;

    public RegistrationFragment(String profile) {
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.registrationactivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        registrations = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertRegistration.class);
                intent.putExtra(InsertRegistration.PROFILE, profile);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupParticient().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdateCity)
                                .customView(R.layout.update_registration, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        if (newDateRegistrationEditText.getText().toString().equals("")) {
                                            Toast.makeText(getActivity(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            new Updater(position).execute();
                                        }
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();
                        particientKodSpinner = (Spinner) v.findViewById(R.id.particientKodSpinner);

                        Log.d(TAG, "RegistrationActivity" + arrayParticientKod[position]);
                        oldParticientKodEditText = (EditText) v.findViewById(R.id.oldParticientKodEditText);
                        oldParticientKodEditText.append(arrayParticientKod[position]);

                        oldDateRegistrationEditText = (EditText) v.findViewById(R.id.oldDateRegistrationEditText);
                        oldDateRegistrationEditText.append(String.valueOf(arrayDateRegistrations[position]));
                        newDateRegistrationEditText = (EditText) v.findViewById(R.id.newDateRegistrationEditText);

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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectRegistrationLookup.php");
            //Fields of table RegistrationsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdRegistrations = new int[array.length()];
                arrayDateRegistrations = new String[array.length()];
                arrayParticientKod = new String[array.length()];
                arrayOldIdParticient = new int[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayOldIdParticient[i] = jObject.getInt("IdParticient");
                    arrayDateRegistrations[i] = jObject.getString("DateRegistration");
                    arrayParticientKod[i] = jObject.getString("FIO");

                    registrations.add(new Registration(jObject.getInt("IdParticient"), jObject.getString("DateRegistration"), jObject.getString("FIO")));

                    Log.d(TAG, arrayDateRegistrations[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDateRegistrations;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new RegistrationAdapter(registrations);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table PostsActivity
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldPositionParticient;

        public Updater(int oldPositionParticient) {
            this.oldPositionParticient = oldPositionParticient;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldDateRegistration = oldDateRegistrationEditText.getText().toString();
            String newDateRegistration = newDateRegistrationEditText.getText().toString();

            String oldParticientKod = oldParticientKodEditText.getText().toString();
            int positionSpinner = particientKodSpinner.getSelectedItemPosition();

            Log.d(TAG, "Old DateRegistration " + oldDateRegistration);
            Log.d(TAG, "New DateRegistration " + newDateRegistration);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateRegistrationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDateRegistration", oldDateRegistration);
                json.put("newDateRegistration", newDateRegistration);

                json.put("oldParticientKod", arrayOldIdParticient[oldPositionParticient]);
                json.put("newParticientKod", arrayIdParticientSpinner[positionSpinner]);

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
        private int positionidRegistration;

        public Deleter(int positionidRegistration) {
            this.positionidRegistration = positionidRegistration;
        }

        @Override
        protected String doInBackground(String... params) {
            int particientKod = arrayOldIdParticient[positionidRegistration];
            String dateRegistration = arrayDateRegistrations[positionidRegistration];

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteRegistrationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("ParticientKod", particientKod);
                json.put("DateRegistration", dateRegistration);
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

    public class LookupParticient extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ParticientScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayFIOParticientSpinner = new String[array.length()];
                arrayIdParticientSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String FIOParticient = jObject.getString("FIO");
                    int idParticient = jObject.getInt("IdParticient");

                    arrayIdParticientSpinner[i] = idParticient;
                    arrayFIOParticientSpinner[i] = FIOParticient;
                    Log.d(TAG, "ParticientKod in RegistrationsActivity " + arrayIdParticientSpinner[i] + " Particient " + arrayFIOParticientSpinner[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFIOParticientSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayFIOParticientSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            particientKodSpinner.setAdapter(adapterSpinner);

        }
    }
}
