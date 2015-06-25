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

import com.example.gleb.adapters.DepartmentsDoctorsAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertCountry;
import com.example.gleb.insert.InsertDepartmentDoctor;

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
public class DepartmentsDoctors extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private DepartmentsDoctorsAdapter adapter;
    public EditText oldDepartmentDoctorEditText;
    public EditText newDepartmentDoctorEditText;
    public String[] arrayDepartments = null;
    public int[] arrayIdDepartments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departmentsdoctors);

        listView = (ListView) findViewById(R.id.departmentListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderdepartment, null);
        listView.addHeaderView(header);
        new Loader().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * View for delete record
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(DepartmentsDoctors.this);
                View dialogView = LayoutInflater.from(DepartmentsDoctors.this).inflate(R.layout.update_departmentdoctor, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                oldDepartmentDoctorEditText = (EditText) dialog.findViewById(R.id.oldDepartmentDoctorEditText);
                oldDepartmentDoctorEditText.append(arrayDepartments[position - 1]);
                newDepartmentDoctorEditText = (EditText) dialog.findViewById(R.id.newDepartmentDoctorEditText);

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

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoice(listView));
    }

    /**
     * Update record from table DepartmentDoctors
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldDepartmentDoctor = oldDepartmentDoctorEditText.getText().toString();
            String newDepartmentDoctor = newDepartmentDoctorEditText.getText().toString();

            Log.d(TAG, "Old Change " + oldDepartmentDoctor);
            Log.d(TAG, "New Change " + newDepartmentDoctor);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdateDepartmentDoctorsScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldDepartment", oldDepartmentDoctor);
                json.put("newDepartment", newDepartmentDoctor);
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
     * MultiChoice for delete record and add new record in table DepartmentsDoctors
     */
    public class MultiChoice implements AbsListView.MultiChoiceModeListener {
        private AbsListView list;
        public ArrayList<Integer> departments = new ArrayList<Integer>();

        public MultiChoice(AbsListView list) {
            this.list = list;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.d(TAG, "OnItemCheck");
            if (checked) {
                int rows = list.getCheckedItemCount();
                departments.add(position);
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
                    for (int i = 0; i < departments.size(); i++) {
                        Log.d(TAG, arrayDepartments[departments.get(i) - 1]);
                        new Deleter(departments.get(i) - 1).execute();

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

        /**
     * Delete record from table departments
     */
    public class Deleter extends AsyncTask<String, String, String>{
        private int positionDepartment;

        public Deleter(int positionDepartment) {
            this.positionDepartment = positionDepartment;
        }

        @Override
        protected String doInBackground(String... params) {
            String department = arrayDepartments[positionDepartment];
            Log.d(TAG, department);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeleteDepartmentDoctorScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("department", department);
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
            Intent intent = new Intent(this, InsertDepartmentDoctor.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DepartmentsDoctorsScript.php");
            //Fields of table DepartmentsDoctors

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayDepartments = new String[array.length()];
                arrayIdDepartments = new int[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    int id = jObject.getInt("IdDepartment");
                    String department = jObject.getString("Department");
                    arrayIdDepartments[i] = id;
                    arrayDepartments[i] = department;
                    Log.d(TAG, arrayDepartments[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayDepartments;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new DepartmentsDoctorsAdapter(getBaseContext(), value);
            listView.setAdapter(adapter);

        }
    }
}
