package com.example.gleb.dentistcard;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Gleb on 16.06.2015.
 */
public class JsonParser {
    public static final String TAG = "TAG";
    public static InputStream is = null;
    public static JSONObject jObj = null;
    public static String json = "";

    // constructor
    public JsonParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public void makeHttpRequest(String url, List<NameValuePair> params) {
        String result = "";
        // Making HTTP request
        try {
            // check for request method
            // request method is POST
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
            if (is != null){
                result = "Send";
            }
            else{
                result = "Dont send";
            }

            Log.d(TAG, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
