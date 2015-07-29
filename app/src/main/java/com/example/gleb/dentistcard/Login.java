package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gleb.profileactivities.AdminProfileActivity;
import com.example.gleb.profileactivities.DoctorProfileActivity;
import com.example.gleb.profileactivities.ParticientProfileActivity;
import com.example.gleb.profileactivities.RegistrationProfileActivity;
import com.example.gleb.tables.UserRegistration;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 28.06.15.
 */
public class Login extends Pattern{
    public static final String TAG = "TAG";
    public static final String FULLNAME = "fullName";
    public EditText emailEditText;
    public EditText passwordEditText;
    public Button loginButton;
    public TextView registrationTextView;

    public String fullName;
    public String email;
    public String password;
    public String profile;

    public List<UserRegistration> userregistrations;
    public int value;

    public static int NO_OPTIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userregistrations = new ArrayList<>();
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registrationTextView = (TextView) findViewById(R.id.registrationTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emailEditText.getText().toString().equals("") &&
                        !passwordEditText.getText().toString(). equals("")) {
                    new SignUp().execute();
                }
                else{
                    Toast.makeText(Login.this, R.string.InputEmailPassword, Toast.LENGTH_SHORT).show();
                }

            }
        });

        registrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegistrationUser.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Update record from table doctors
     */
    public class SignUp extends AsyncTask<String, String, String> {
        public String emailValue;
        public String passwordValue;
        public String hashSha1;

        @Override
        protected String doInBackground(String... params) {
            emailValue = emailEditText.getText().toString();
            passwordValue = passwordEditText.getText().toString();

//            MessageDigest sha1 = null;
//
//            try {
//                sha1 = MessageDigest.getInstance("SHA-1");
//                sha1.update(passwordValue.getBytes("ASCII"));
//                byte[] data = sha1.digest();
//                hashSha1 = convertToHex(data);
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
            post = new HttpPost("http://dentists.16mb.com/RegistrationScript/SignUpLookup.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("Email", emailValue);
                json.put("Password", passwordValue);

//                Log.d(TAG, hashSha1);

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

                        for (int i = 0; i < array.length(); i++) {
                            //parse of array
                            JSONObject jObject = array.getJSONObject(i);
                            fullName = jObject.getString("FullName");
                            email = jObject.getString("Email");
                            password = jObject.getString("Password");
                            profile = jObject.getString("Profile");

                            userregistrations.add(new UserRegistration(fullName, email, password, profile));
                            value = 1;
                            Log.d(TAG, "FullName " + fullName);
                            Log.d(TAG, email);
                            Log.d(TAG, password);
                            Log.d(TAG, profile);
                        }
                    }
                    else{
                        Log.d(TAG, "Sb == 0");
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
                if (profile.equals("Администратор")){
                    Intent intent = new Intent(Login.this, AdminProfileActivity.class);
                    startActivity(intent);
                }
                else{
                    if (profile.equals("Регистратор")){
                        Intent intent = new Intent(Login.this, RegistrationProfileActivity.class);
                        startActivity(intent);
                    }
                    else{
                        if (profile.equals("Врач")){
                            Intent intent = new Intent(Login.this, DoctorProfileActivity.class);
                            intent.putExtra(FULLNAME, fullName);
                            startActivity(intent);
                        }
                        else{
                            if (profile.equals("Пациент")){
//                                Intent intent = new Intent(Login.this, ParticientProfileActivity.class);
//                                startActivity(intent);
                                Intent intent = new Intent(Login.this, ParticientProfileActivity.class);
                                Log.d(TAG, "RegistrationUser " + emailEditText.getText().toString());
                                intent.putExtra(ParticientProfileActivity.EMAIL, emailEditText.getText().toString());
                                startActivity(intent);
                            }
                        }
                    }
                }
                Toast.makeText(Login.this, R.string.Send, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Login.this, R.string.IncorrectUser, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String convertToHex(byte[] data) throws java.io.IOException
    {
        StringBuffer sb = new StringBuffer();
        String hex = null;
        hex = Base64.encodeToString(data, 0, data.length, NO_OPTIONS);
        sb.append(hex);

        return sb.toString();
    }
}