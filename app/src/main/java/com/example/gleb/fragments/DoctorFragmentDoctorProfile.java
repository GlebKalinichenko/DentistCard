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
import com.example.gleb.adapters.DoctorAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertCity;
import com.example.gleb.insert.InsertDoctor;
import com.example.gleb.insert.InsertRecommendation;
import com.example.gleb.profileactivities.DoctorHelp;
import com.example.gleb.tables.Doctor;
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
 * Created by gleb on 30.07.15.
 */
public class DoctorFragmentDoctorProfile extends Fragment {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    public String[] arrayFIO = null;
    public int[] arrayIdDoctors = null;
    public String[] arrayPostKod = null;
    public String[] arrayDepartmentKod = null;
    public String[] arrayKvalificationKod = null;
    public int[] arrayExpiriences = null;
    private DoctorAdapter adapter;
    public EditText oldFIOEditText;
    public EditText newFIOEditText;
    public EditText oldPostKodEditText;
    public EditText oldKvalificationKodEditText;
    public EditText oldDepartmentKodEditText;
    public EditText oldExpirienceEditText;
    public EditText newExpirienceEditText;

    public Spinner postKodSpinner;
    public Spinner kvalificationKodSpinner;
    public Spinner departmentKodSpinner;

    public String[] arrayPostSpinner;
    public int[] arrayIdPostSpinner;

    public String[] arrayKvalificationSpinner;
    public int[] arrayIdKvalificationSpinner;

    public String[] arrayDepartmentSpinner;
    public int[] arrayIdDepartmentSpinner;

    public int[] arrayOldIdPost;
    public int[] arrayOldIdKvalification;
    public int[] arrayOldIdDepartment;

    private List<Doctor> doctors;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;
    public String fullName;
    public int freshTicket;
    public int allTicket;
    public String profile;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    public DoctorFragmentDoctorProfile(String fullName, int freshTicket, int allTicket, String profile) {
        this.fullName = fullName;
        this.freshTicket = freshTicket;
        this.allTicket = allTicket;
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.doctoractivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        doctors = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertDoctor.class);
                intent.putExtra(InsertDoctor.PROFILE, profile);
                intent.putExtra(InsertDoctor.ALLTICKET, allTicket);
                intent.putExtra(InsertDoctor.FRESHTICKET, freshTicket);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        new LookupPost().execute();
                        new LookupKvalification().execute();
                        new LookupDepartment().execute();

                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdateDoctors)
                                .customView(R.layout.update_doctors, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        if (newFIOEditText.getText().toString().equals("") || newExpirienceEditText.getText().toString().equals("")) {
                                            Toast.makeText(getActivity(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            new Updater(position).execute();
                                        }
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();
                        postKodSpinner = (Spinner) v.findViewById(R.id.postKodSpinner);
                        kvalificationKodSpinner = (Spinner) v.findViewById(R.id.kvalificationKodSpinner);
                        departmentKodSpinner = (Spinner) v.findViewById(R.id.departmentKodSpinner);

                        oldKvalificationKodEditText = (EditText) v.findViewById(R.id.oldKvalificationKodEditText);
                        oldKvalificationKodEditText.append(String.valueOf(arrayKvalificationKod[position]));

                        oldPostKodEditText = (EditText) v.findViewById(R.id.oldPostKodEditText);
                        oldPostKodEditText.append(String.valueOf(arrayPostKod[position]));

                        oldDepartmentKodEditText = (EditText) v.findViewById(R.id.oldDepartmentKodEditText);
                        oldDepartmentKodEditText.append(String.valueOf(arrayDepartmentKod[position]));

                        oldExpirienceEditText = (EditText) v.findViewById(R.id.oldExpirienceEditText);
                        oldExpirienceEditText.append(String.valueOf(arrayExpiriences[position]));
                        newExpirienceEditText = (EditText) v.findViewById(R.id.newExpirienceEditText);

                        oldFIOEditText = (EditText) v.findViewById(R.id.oldFIOEditText);
                        oldFIOEditText.append(String.valueOf(arrayFIO[position]));
                        newFIOEditText = (EditText) v.findViewById(R.id.newFIOEditText);

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
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectDoctorDoctorProfile.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);

                post.setHeader("json", json.toString());
                StringEntity se = new StringEntity(json.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                if (response != null) {
                    InputStream in = response.getEntity().getContent(); // Get the
                    Log.i("Read from Server", in.toString());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "Sb " + sb.toString());
                    if (!sb.toString().equals("")) {
                        JSONArray array = new JSONArray(sb.toString());

                        try{
                            //parse of array
                            arrayFIO = new String[array.length()];
                            arrayOldIdPost = new int[array.length()];
                            arrayOldIdKvalification = new int[array.length()];
                            arrayOldIdDepartment = new int[array.length()];
                            arrayDepartmentKod = new String[array.length()];
                            arrayKvalificationKod = new String[array.length()];
                            arrayExpiriences = new int[array.length()];
                            arrayPostKod = new String[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                arrayOldIdPost[i] = jObject.getInt("IdPost");
                                arrayOldIdKvalification[i] = jObject.getInt("IdKvalification");
                                arrayOldIdDepartment[i] = jObject.getInt("IdDepartment");
                                arrayFIO[i] = jObject.getString("FIO");
                                arrayDepartmentKod[i] = jObject.getString("Department");
                                arrayKvalificationKod[i] = jObject.getString("Kvalification");
                                arrayPostKod[i] = jObject.getString("Post");
                                arrayExpiriences[i] = jObject.getInt("Expirience");

                                doctors.add(new Doctor(jObject.getString("FIO"), jObject.getString("Post"), jObject.getString("Kvalification"),
                                        jObject.getString("Department"), jObject.getInt("Expirience")));

                                Log.d(TAG, arrayFIO[i]);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new DoctorAdapter(doctors);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table doctors
     */
    public class Updater extends AsyncTask<String, String, String>{
        public int oldPostPosition;
        public int oldKvalificationPosition;
        public int oldDepartmentPosition;

        public Updater(int oldPosition) {
            this.oldPostPosition = oldPosition;
            this.oldKvalificationPosition = oldPosition;
            this.oldDepartmentPosition = oldPosition;
        }

        @Override
        protected String doInBackground(String... params) {
            String oldFIO = oldFIOEditText.getText().toString();
            String newFIO = newFIOEditText.getText().toString();

            String oldPostKod = oldPostKodEditText.getText().toString();
            String oldKvalificationKod = oldKvalificationKodEditText.getText().toString();
            String oldDepartmentKod = oldDepartmentKodEditText.getText().toString();

            String oldExpirience = oldExpirienceEditText.getText().toString();
            String newExpirience = newExpirienceEditText.getText().toString();

            int positionPost = postKodSpinner.getSelectedItemPosition();
            int positionKvalification = kvalificationKodSpinner.getSelectedItemPosition();
            int positionDepartment = departmentKodSpinner.getSelectedItemPosition();

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

                json.put("oldPostKod", arrayOldIdPost[oldPostPosition]);
                json.put("newPostKod", arrayIdPostSpinner[positionPost]);

                json.put("oldKvalificationKod", arrayOldIdKvalification[oldKvalificationPosition]);
                json.put("newKvalificationKod", arrayIdKvalificationSpinner[positionKvalification]);

                json.put("oldDepartmentKod", arrayOldIdDepartment[oldDepartmentPosition]);
                json.put("newDepartmentKod", arrayIdDepartmentSpinner[positionDepartment]);

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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    public class LookupPost extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/PostScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayPostSpinner = new String[array.length()];
                arrayIdPostSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String post = jObject.getString("Post");
                    int idPost = jObject.getInt("IdPost");

                    arrayPostSpinner[i] = post;
                    arrayIdPostSpinner[i] = idPost;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPostSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayPostSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            postKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupKvalification extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/KvalificationScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayKvalificationSpinner = new String[array.length()];
                arrayIdKvalificationSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String kvalification = jObject.getString("Kvalification");
                    int idKvalification = jObject.getInt("IdKvalification");

                    arrayKvalificationSpinner[i] = kvalification;
                    arrayIdKvalificationSpinner[i] = idKvalification;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPostSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayKvalificationSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kvalificationKodSpinner.setAdapter(adapterSpinner);
        }
    }

    public class LookupDepartment extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DepartmentsDoctorsScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayDepartmentSpinner = new String[array.length()];
                arrayIdDepartmentSpinner = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String department = jObject.getString("Department");
                    int idDepartment = jObject.getInt("IdDepartment");

                    arrayDepartmentSpinner[i] = department;
                    arrayIdDepartmentSpinner[i] = idDepartment;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDepartmentSpinner;
        }

        @Override
        protected void onPostExecute(String[] value) {
            Log.d(TAG, arrayPostSpinner[0]);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayDepartmentSpinner);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            departmentKodSpinner.setAdapter(adapterSpinner);
        }
    }
}
