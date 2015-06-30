package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gleb.adapters.CityAdapter;

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

/**
 * Created by gleb on 28.06.15.
 */
public class RegistrationUser extends Pattern{
    private DatabaseRequest request = new DatabaseRequest();
    public static final String TAG = "TAG";
    public EditText emailEditText;
    public EditText userNameEditText;
    public EditText passwordEditText;
    public Button registrationUserButton;

    public int[] arrayIdUserRegistration = null;
    public String[] arrayEmail = null;
    public String[] arrayFullName = null;
    public String[] arrayPassword = null;
    public String[] arrayProfiles = null;
    public int[] arrayProfileKod = null;
    public Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_registration);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        userNameEditText = (EditText) findViewById(R.id.fullNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        registrationUserButton = (Button) findViewById(R.id.registrationUserButton);
        spinner = (Spinner) findViewById(R.id.profileSpinner);

        new LookupProfile().execute();

        registrationUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")
                        || passwordEditText.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), R.string.CorrectRegistration, Toast.LENGTH_SHORT).show();
                }
                else{
                    new Registator().execute();
                    Intent intent = new Intent(RegistrationUser.this, Navigator.class);
                    startActivity(intent);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spinner " + arrayProfiles[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class Registator extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String fullName = userNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            int profilePosition = spinner.getSelectedItemPosition();

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/RegistrationScript/UserRegistrationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("fullName", fullName);
                json.put("email", email);
                json.put("password", password);
                json.put("profileKod", arrayProfileKod[profilePosition]);

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

//    public class Loader extends AsyncTask<String, String, String[]>{
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectLookupQuery/SelectUserRegistrationLookup.php");
//
//            Log.d(TAG, jsonContent);
//            try {
//                JSONArray array = new JSONArray(jsonContent);
//                arrayIdUserRegistration = new int[array.length()];
//                arrayFullName = new String[array.length()];
//                arrayEmail = new String[array.length()];
//                arrayPassword = new String[array.length()];
//                arrayProfiles = new String[array.length()];
//                arrayProfileKod = new int[array.length()];
//
//                new LookupProfile().execute();
//
//
//                for (int i = 0; i < array.length(); i++){
//                    JSONObject jObject = array.getJSONObject(i);
//                    String fullName = jObject.getString("FullName");
//                    String email = jObject.getString("Email");
//                    String password = jObject.getString("Password");
//                    //String profile = jObject.getString("Profile");
//                    //int profileKod = jObject.getInt("ProfileKod");
//
//                    arrayFullName[i] = fullName;
//                    arrayEmail[i] = email;
//                    arrayPassword[i] = password;
//                    //arrayProfiles[i] = profile;
//                    //arrayProfileKod[i] = profileKod;
//
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return arrayFullName;
//        }
//
//        @Override
//        protected void onPostExecute(String[] value) {
//
//        }
//    }

    public class LookupProfile extends AsyncTask<String, String, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/ProfileScript.php");

            Log.d(TAG, jsonContent);
            try {
                JSONArray array = new JSONArray(jsonContent);
                arrayProfiles = new String[array.length()];
                arrayProfileKod = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    JSONObject jObject = array.getJSONObject(i);
                    String profile = jObject.getString("Profile");
                    int profileKod = jObject.getInt("IdProfile");

                    arrayProfiles[i] = profile;
                    arrayProfileKod[i] = profileKod;

                    Log.d(TAG, arrayProfiles[i]);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFullName;
        }

        @Override
        protected void onPostExecute(String[] value) {
            /**
             * Adapter for add content in spinner from table Profiles
             */
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayProfiles);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);

        }
    }
}
