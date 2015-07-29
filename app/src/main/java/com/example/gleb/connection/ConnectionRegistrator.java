package com.example.gleb.connection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gleb.adapters.ChangeAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.Mail;
import com.example.gleb.dentistcard.R;
import com.example.gleb.tables.Change;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gleb on 21.07.15.
 */
public class ConnectionRegistrator extends ActionBarActivity {
    public static final String TAG = "TAG";
    public static final String EMAIL = "Email";
    private DatabaseRequest request = new DatabaseRequest();
    public EditText themeEditText;
    public EditText textEditText;
    public Spinner emailSpinner;
    public Button sendButton;

    public String[] arrayFullName;
    public String[] arrayEmail;

    public String emailParticient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_doctor);

        emailParticient = getIntent().getStringExtra(ConnectionRegistrator.EMAIL);
        Log.d(TAG, "ConnectionRegistrator " + emailParticient);

        themeEditText = (EditText) findViewById(R.id.themeEditText);
        textEditText = (EditText) findViewById(R.id.textEditText);
        emailSpinner = (Spinner) findViewById(R.id.emailSpinner);
        sendButton = (Button) findViewById(R.id.sendButton);

        new Loader().execute();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                Log.d(TAG, "to " + arrayEmail[emailSpinner.getSelectedItemPosition()]);
                String[] toArr = {arrayEmail[emailSpinner.getSelectedItemPosition()]}; // This is an array, you can add more emails, just separate them with a coma
                m.setTo(toArr); // load array to setTo function
                Log.d(TAG, "from " + emailParticient);
                m.setFrom(emailParticient); // who is sending the email
                m.setSubject(themeEditText.getText().toString());
                m.setBody(textEditText.getText().toString());

                try {
                    if(m.send()) {
                        // success
                        Toast.makeText(ConnectionRegistrator.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        // failure
                        Toast.makeText(ConnectionRegistrator.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/ConnectionScript/SearchRegistrations.php");
            //Fields of table PostsActivity

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayFullName = new String[array.length()];
                arrayEmail = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    String fullName = jObject.getString("FullName");
                    String email = jObject.getString("Email");
                    arrayFullName[i] = fullName;
                    arrayEmail[i] = email;

                    Log.d(TAG, "FullName " + arrayFullName[i]);
                    Log.d(TAG, "Email " + arrayEmail[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayFullName;
        }

        @Override
        protected void onPostExecute(String[] value) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(ConnectionRegistrator.this, android.R.layout.simple_spinner_item, arrayFullName);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            emailSpinner.setAdapter(adapterSpinner);

        }
    }
}
