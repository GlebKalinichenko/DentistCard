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
public class InsertRecommendation extends InsertPattern {
    private EditText ticketKodEditText;
    private EditText diagnoseKodEditText;
    private EditText therapyEditText;
    private EditText complaintsEditText;
    private EditText historyIllnessEditText;
    private EditText objectiveValuesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_recommendation);

        ticketKodEditText = (EditText) findViewById(R.id.ticketKodEditText);
        diagnoseKodEditText = (EditText) findViewById(R.id.diagnoseKodEditText);
        therapyEditText = (EditText) findViewById(R.id.therapyEditText);
        complaintsEditText = (EditText) findViewById(R.id.complaintsEditText);
        historyIllnessEditText = (EditText) findViewById(R.id.historyIllnessEditText);
        objectiveValuesEditText = (EditText) findViewById(R.id.objectiveValuesEditText);
        insertButton = (Button) findViewById(R.id.insertRecommendationButton);
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
            String ticketKod = ticketKodEditText.getText().toString();
            String diagnoseKod = diagnoseKodEditText.getText().toString();
            String therapy = therapyEditText.getText().toString();
            String complaints = complaintsEditText.getText().toString();
            String historyIllness = historyIllnessEditText.getText().toString();
            String objectiveValues = objectiveValuesEditText.getText().toString();

            Log.d(TAG, ticketKod);
            Log.d(TAG, diagnoseKod);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertRecommendationScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("ticketKod", ticketKod);
                json.put("diagnoseKod", diagnoseKod);
                json.put("therapy", therapy);
                json.put("complaints", complaints);
                json.put("historyIllness", historyIllness);
                json.put("objectiveValues", objectiveValues);
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
