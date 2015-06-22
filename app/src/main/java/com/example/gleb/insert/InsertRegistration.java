package com.example.gleb.insert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gleb.dentistcard.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by gleb on 21.06.15.
 */
public class InsertRegistration extends InsertPattern {
    private EditText dateRegistrationEditText;
    private EditText particientKodEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_registration);

        dateRegistrationEditText = (EditText) findViewById(R.id.dateRegistrationEditText);
        particientKodEditText = (EditText) findViewById(R.id.particientKodEditText);
        insertButton = (Button) findViewById(R.id.insertRegistrationButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Sender().execute();
            }
        });



    }

    public class Sender extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String dateRegistration = dateRegistrationEditText.getText().toString();
            String particientKod = particientKodEditText.getText().toString();

            Log.d(TAG, dateRegistration);
            Log.d(TAG, particientKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertRegistrationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("dateRegistration", dateRegistration);
                json.put("particientKod", particientKod);
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
}
