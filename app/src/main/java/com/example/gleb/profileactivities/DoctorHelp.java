package com.example.gleb.profileactivities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.gleb.adapters.RecomendationAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Login;
import com.example.gleb.dentistcard.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by gleb on 22.07.15.
 */
public class DoctorHelp extends ActionBarActivity {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    public TextView fioTextView;
    public TextView postTextView;
    public TextView kvalificationTextView;
    public TextView departmentTextView;
    public TextView expirienceTextView;
    public String[] arrayFIO;
    public String[] arrayPost;
    public String[] arrayKvalification;
    public String[] arrayDepartment;
    public int[] arrayExpirience;
    public String fullName;

    protected HttpClient client;
    protected HttpPost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutdoctor);

        fullName = getIntent().getStringExtra(Login.FULLNAME);
        Log.d(TAG, "DoctorHelp " + fullName);

        fioTextView = (TextView) findViewById(R.id.fioTextView);
        postTextView = (TextView) findViewById(R.id.postTextView);
        kvalificationTextView = (TextView) findViewById(R.id.kvalificationTextView);
        departmentTextView = (TextView) findViewById(R.id.departmentTextView);
        expirienceTextView = (TextView) findViewById(R.id.expirienceTextView);

        new Loader().execute();

    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/SelectLookupQuery/SelectDoctorHelp.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("FIO", fullName);

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

                        try{
                            //create JSON array for parse it
                            arrayFIO = new String[array.length()];
                            arrayPost = new String[array.length()];
                            arrayKvalification = new String[array.length()];
                            arrayDepartment = new String[array.length()];
                            arrayExpirience = new int[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                //parse of array
                                JSONObject jObject = array.getJSONObject(i);
                                arrayFIO[i] = jObject.getString("FIO");
                                arrayPost[i] = jObject.getString("Post");
                                arrayKvalification[i] = jObject.getString("Kvalification");
                                arrayDepartment[i] = jObject.getString("Department");
                                arrayExpirience[i] = jObject.getInt("Expirience");

                                Log.d(TAG, arrayFIO[i]);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return arrayFIO;
        }

        @Override
        protected void onPostExecute(String[] value) {
            fioTextView.setText(arrayFIO[0]);
            postTextView.setText(arrayPost[0]);
            kvalificationTextView.setText(arrayKvalification[0]);
            departmentTextView.setText(arrayDepartment[0]);
            expirienceTextView.setText(String.valueOf(arrayExpirience[0]));

        }
    }
}
