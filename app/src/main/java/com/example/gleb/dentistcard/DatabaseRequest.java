package com.example.gleb.dentistcard;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Gleb on 05.06.2015.
 */
public class DatabaseRequest {
    private String jsonContent;
    private InputStream input;
    private JSONObject jObject;

    public DatabaseRequest(){

    }

    /**
     * Function get URL of server with JSON content and write it into string jsonContent
     * @param url   URL of server
     * @return jsonContent
     */
    public String makeRequest(String url){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            input = entity.getContent();
            jsonContent = parseContentJSON(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonContent;


    }

    public String parseContentJSON(InputStream in){
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();

        try {
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();


    }


}
