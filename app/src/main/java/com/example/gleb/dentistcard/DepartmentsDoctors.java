package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.DepartmentsDoctorsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 06.06.2015.
 */
public class DepartmentsDoctors extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private DepartmentsDoctorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departmentsdoctors);

        listView = (ListView) findViewById(R.id.departmentListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderdepartment, null);
        listView.addHeaderView(header);
        new Loader().execute();
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/DepartmentsDoctorsScript.php");
            //Fields of table DepartmentsDoctors
            String[] arrayDepartments = null;
            int[] arrayIdDepartments = null;

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
