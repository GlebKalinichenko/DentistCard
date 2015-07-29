package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gleb.connection.ConnectionRegistrator;
import com.example.gleb.profileactivities.AdminProfileActivity;
import com.example.gleb.profileactivities.DoctorProfileActivity;
import com.example.gleb.profileactivities.ParticientProfileActivity;
import com.example.gleb.profileactivities.RegistrationProfileActivity;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    public Spinner profileSpinner;

    public String responseValue;
    public int value;

    public String hashSha;
    public static int NO_OPTIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_registration);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        userNameEditText = (EditText) findViewById(R.id.fullNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        registrationUserButton = (Button) findViewById(R.id.registrationUserButton);
        profileSpinner = (Spinner) findViewById(R.id.profileSpinner);

        new LookupProfile().execute();

        registrationUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (userNameEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")
//                        || passwordEditText.getText().toString().equals("")){
//                    Toast.makeText(getBaseContext(), R.string.CorrectRegistration, Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    int profilePosition = profileSpinner.getSelectedItemPosition();
//                    String profile = arrayProfiles[profilePosition];
//
//                    if (profile.equals("Администратор")) {
//                        new Registrator().execute();
//                        if (value == 0) {
//                            Intent intent = new Intent(RegistrationUser.this, AdminProfileActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//                    else{
//                        if (profile.equals("Регистратор")) {
//                            new Registrator().execute();
//                            if (value == 0) {
//                                Intent intent = new Intent(RegistrationUser.this, RegistrationProfileActivity.class);
//                                startActivity(intent);
//                            }
//                        }
//                        else{
//                            if (profile.equals("Врач")) {
//                                new Registrator().execute();
//                                if (value == 0) {
//                                    Intent intent = new Intent(RegistrationUser.this, DoctorProfileActivity.class);
//                                    startActivity(intent);
//                                }
//                            }
//                            else{
//                                if (profile.equals("Пациент")) {
//                                    new Registrator().execute();
//                                    if (value == 0) {
//                                        Intent intent = new Intent(RegistrationUser.this, ParticientProfileActivity.class);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
                new Registrator().execute();
            }
        });

        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spinner " + arrayProfiles[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class Registrator extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String fullName = userNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            int profilePosition = profileSpinner.getSelectedItemPosition();

//            MessageDigest sha1 = null;
//
//            try {
//                sha1 = MessageDigest.getInstance("SHA-1");
//                sha1.update(password.getBytes("ASCII"));
//                byte[] data = sha1.digest();
//                hashSha = convertToHex(data);
//
//                Log.d(TAG, "SHA-1 " + hashSha);
//
//
//
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/RegistrationScript/UserRegistrationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("fullName", fullName);
                json.put("email", email);
//                json.put("password", hashSha);
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

                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
//                    JSONObject jsonObject = new JSONObject(sb.toString());
                    Log.d(TAG, "Response " + sb.toString());
                    if (!sb.toString().equals("")) {
                        JSONObject jsonValue = new JSONObject(sb.toString());
                        responseValue = jsonValue.getString("request");

                        value = 1;
                        Log.d(TAG, "Response value " + responseValue);

                    }
                    else{
                        value = 0;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (value == 1) {
                Toast.makeText(getBaseContext(), R.string.CorrectUser, Toast.LENGTH_SHORT).show();
            }
            else {
                if (value == 0) {
                    if (userNameEditText.getText().toString().equals("") || emailEditText.getText().toString().equals("")
                            || passwordEditText.getText().toString().equals("")) {
                        Toast.makeText(getBaseContext(), R.string.CorrectRegistration, Toast.LENGTH_SHORT).show();
                    } else {
                        int profilePosition = profileSpinner.getSelectedItemPosition();
                        String profile = arrayProfiles[profilePosition];

                        if (profile.equals("Администратор")) {
                            Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                            String[] toArr = {emailEditText.getText().toString()}; // This is an array, you can add more emails, just separate them with a coma
                            m.setTo(toArr); // load array to setTo function
                            m.setFrom("Glebjn@yandex.ua"); // who is sending the email
                            m.setSubject("Спасибо за регистрацию в системе DentistCard");
                            String newLine = System.getProperty("line.separator");
                            m.setBody("Здравствуйте, уважаемый " + userNameEditText.getText().toString() + "." + newLine +
                                "Спасибо, что зарегистировались в DentistCard" + newLine +
                                "Ваш логин: " + userNameEditText.getText().toString() + newLine +
                                "Ваш пароль: " + passwordEditText.getText().toString() + newLine +
                                "Ваш email: " + emailEditText.getText().toString() + newLine +
                                "Ваш профиль: " + profile);


                            try {
                                if(m.send()) {
                                    // success
                                    Toast.makeText(RegistrationUser.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                                } else {
                                    // failure
                                    Toast.makeText(RegistrationUser.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(RegistrationUser.this, AdminProfileActivity.class);
                            startActivity(intent);
                        }
                        else {
                            if (profile.equals("Регистратор")) {
                                Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                                String[] toArr = {emailEditText.getText().toString()}; // This is an array, you can add more emails, just separate them with a coma
                                m.setTo(toArr); // load array to setTo function
                                m.setFrom("Glebjn@yandex.ua"); // who is sending the email
                                m.setSubject("Спасибо за регистрацию в системе DentistCard");
                                String newLine = System.getProperty("line.separator");
                                m.setBody("Здравствуйте, уважаемый " + userNameEditText.getText().toString() + "." + newLine +
                                        "Спасибо, что зарегистировались в DentistCard" + newLine +
                                        "Ваш логин: " + userNameEditText.getText().toString() + newLine +
                                        "Ваш пароль: " + passwordEditText.getText().toString() + newLine +
                                        "Ваш email: " + emailEditText.getText().toString() + newLine +
                                        "Ваш профиль: " + profile);

                                try {
                                    if(m.send()) {
                                        // success
                                        Toast.makeText(RegistrationUser.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                                    } else {
                                        // failure
                                        Toast.makeText(RegistrationUser.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(RegistrationUser.this, RegistrationProfileActivity.class);
                                startActivity(intent);
                            }
                            else {
                                if (profile.equals("Врач")) {
                                    Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                                    String[] toArr = {emailEditText.getText().toString()}; // This is an array, you can add more emails, just separate them with a coma
                                    m.setTo(toArr); // load array to setTo function
                                    m.setFrom("Glebjn@yandex.ua"); // who is sending the email
                                    m.setSubject("Спасибо за регистрацию в системе DentistCard");
                                    String newLine = System.getProperty("line.separator");
                                    m.setBody("Здравствуйте, уважаемый " + userNameEditText.getText().toString() + "." + newLine +
                                            "Спасибо, что зарегистировались в DentistCard" + newLine +
                                            "Ваш логин: " + userNameEditText.getText().toString() + newLine +
                                            "Ваш пароль: " + passwordEditText.getText().toString() + newLine +
                                            "Ваш email: " + emailEditText.getText().toString() + newLine +
                                            "Ваш профиль: " + profile);

                                    try {
                                        if(m.send()) {
                                            // success
                                            Toast.makeText(RegistrationUser.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                                        } else {
                                            // failure
                                            Toast.makeText(RegistrationUser.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Intent intent = new Intent(RegistrationUser.this, DoctorProfileActivity.class);
                                    intent.putExtra(Login.FULLNAME, userNameEditText.getText().toString());
                                    startActivity(intent);
                                }
                                else {
                                    if (profile.equals("Пациент")) {
                                        Mail m = new Mail("Glebjn@yandex.ua", "Gleb80507078620");
                                        String[] toArr = {emailEditText.getText().toString()}; // This is an array, you can add more emails, just separate them with a coma
                                        m.setTo(toArr); // load array to setTo function
                                        m.setFrom("Glebjn@yandex.ua"); // who is sending the email
                                        m.setSubject("Спасибо за регистрацию в системе DentistCard");
                                        String newLine = System.getProperty("line.separator");
                                        m.setBody("Здравствуйте, уважаемый " + userNameEditText.getText().toString() + "." + newLine +
                                                "Спасибо, что зарегистировались в DentistCard" + newLine +
                                                "Ваш логин: " + userNameEditText.getText().toString() + newLine +
                                                "Ваш пароль: " + passwordEditText.getText().toString() + newLine +
                                                "Ваш email: " + emailEditText.getText().toString() + newLine +
                                                "Ваш профиль: " + profile);

                                        try {
                                            if(m.send()) {
                                                // success
                                                Toast.makeText(RegistrationUser.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                                            } else {
                                                // failure
                                                Toast.makeText(RegistrationUser.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(RegistrationUser.this, ParticientProfileActivity.class);
                                        Log.d(TAG, "RegistrationUser " + emailEditText.getText().toString());
                                        intent.putExtra(ParticientProfileActivity.EMAIL, emailEditText.getText().toString());
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    }
                }
                Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String convertToHex(byte[] data) throws java.io.IOException
    {
        StringBuffer sb = new StringBuffer();
        String hex=null;
        hex = Base64.encodeToString(data, 0, data.length, NO_OPTIONS);
        sb.append(hex);

        return sb.toString();
    }

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
             * Adapter for add content in profileSpinner from table Profiles
             */
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, arrayProfiles);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            profileSpinner.setAdapter(adapterSpinner);

        }
    }

}
