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
public class InsertTicket extends InsertPattern {
    private EditText doctorKodEditText;
    private EditText registrationKodEditText;
    private EditText dateReceptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_ticket);

        doctorKodEditText = (EditText) findViewById(R.id.doctorKodEditText);
        registrationKodEditText = (EditText) findViewById(R.id.registrationKodEditText);
        dateReceptionEditText = (EditText) findViewById(R.id.dateReceptionEditText);
        insertButton = (Button) findViewById(R.id.insertTicketButton);
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
            String doctorKod = doctorKodEditText.getText().toString();
            String registrationKod = registrationKodEditText.getText().toString();
            String dateReception = dateReceptionEditText.getText().toString();

            Log.d(TAG, doctorKod);
            Log.d(TAG, registrationKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertTicketScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("doctorKod", doctorKod);
                json.put("registrationKod", registrationKod);
                json.put("dateReception", dateReception);
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
