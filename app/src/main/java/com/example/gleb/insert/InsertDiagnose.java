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
 * Created by gleb on 20.06.15.
 */
public class InsertDiagnose extends InsertPattern {
    private EditText diagnoseEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_diagnose);

        diagnoseEditText = (EditText) findViewById(R.id.diagnoseEditText);
        insertButton = (Button) findViewById(R.id.insertDiagnoseButton);
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
            String diagnose = diagnoseEditText.getText().toString();
            Log.d(TAG, diagnose);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/InsertQuery/InsertDiagnoseScript.php");
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
            Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }
}
