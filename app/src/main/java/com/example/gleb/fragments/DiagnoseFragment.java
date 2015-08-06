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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.DiagnoseAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertDiagnose;
import com.example.gleb.insert.InsertDoctor;
import com.example.gleb.insert.InsertParticient;
import com.example.gleb.tables.Diagnose;
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
public class DiagnoseFragment extends Fragment {
    public static final String TAG = "TAG";
    public int[] arrayIdDiagnoses = null;
    public String[] arrayDiagnoses = null;
    private DatabaseRequest request = new DatabaseRequest();
    private DiagnoseAdapter adapter;
    public EditText oldDiagnoseEditText;
    public EditText newDiagnoseEditText;

    private List<Diagnose> diagnoses;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    public String profile;

    public DiagnoseFragment(String profile) {
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diagnoseactivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        diagnoses = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        new Loader().execute();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertDiagnose.class);
                intent.putExtra(InsertDiagnose.PROFILE, profile);
                startActivity(intent);
            }
        });

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdateDiagnose)
                                .customView(R.layout.update_diagnose, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        if (newDiagnoseEditText.getText().toString().equals("")) {
                                            Toast.makeText(getActivity(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            new Updater().execute();
                                        }
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();

                        oldDiagnoseEditText = (EditText) v.findViewById(R.id.oldDiagnoseEditText);
                        oldDiagnoseEditText.append(arrayDiagnoses[position]);
                        newDiagnoseEditText = (EditText) v.findViewById(R.id.newDiagnoseEditText);
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
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DiagnoseScript.php");
            //Fields of table DoctorsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayIdDiagnoses = new int[array.length()];
                arrayDiagnoses = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    arrayIdDiagnoses[i] = jObject.getInt("IdDiagnose");
                    arrayDiagnoses[i] = jObject.getString("Diagnose");

                    diagnoses.add(new Diagnose(jObject.getInt("IdDiagnose"), jObject.getString("Diagnose")));

                    Log.d(TAG, arrayDiagnoses[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDiagnoses;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new DiagnoseAdapter(diagnoses);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table DiagnosesActivity
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldDiagnose = oldDiagnoseEditText.getText().toString();
            String newDiagnose = newDiagnoseEditText.getText().toString();

            Log.d(TAG, "Old Diagnose " + oldDiagnose);
            Log.d(TAG, "New Diagnose " + newDiagnose);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateDiagnoseScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDiagnose", oldDiagnose);
                json.put("newDiagnose", newDiagnose);
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
     * Delete record from table diagnoses
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionDiagnose;

        public Deleter(int positionDiagnose) {
            this.positionDiagnose = positionDiagnose;
        }

        @Override
        protected String doInBackground(String... params) {
            String diagnose = arrayDiagnoses[positionDiagnose];
            Log.d(TAG, diagnose);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteDiagnoseScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("diagnose", diagnose);
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

}
