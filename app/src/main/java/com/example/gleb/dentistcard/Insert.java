package com.example.gleb.dentistcard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gleb on 15.06.2015.
 */
public class Insert extends ActionBarActivity {
    public static final String TAG = "TAG";
    private EditText countryEditText;
    public HttpClient httpClient;
    public HttpPost httpPost;
    public Button insertButton;
    public JsonParser jsonParser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_value);

        httpClient = new DefaultHttpClient();

        countryEditText = (EditText) findViewById(R.id.countryEditText);
        insertButton = (Button) findViewById(R.id.insertCountryButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Sender().execute();
            }
        });
    }

    public class Sender extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String country = countryEditText.getText().toString();
            Log.d(TAG, country);
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("country", country));
                Log.d(TAG, param.get(0).toString());

                // getting product details by making HTTP request
                jsonParser.makeHttpRequest("http://dentists.16mb.com/InsertQuery/InsertCountry.php", param);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String json = "";
//
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.accumulate("country", country);
//                json = jsonObject.toString();
//                StringEntity se = new StringEntity(json);
//
//                httpPost.setEntity(se);
//                httpPost.setHeader("Accept", "application/json");
//                httpPost.setHeader("Content-type", "application/json");
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                InputStream inputStream = httpResponse.getEntity().getContent();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getBaseContext(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

}
