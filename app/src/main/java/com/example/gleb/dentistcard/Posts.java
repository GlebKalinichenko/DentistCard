package com.example.gleb.dentistcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gleb.adapters.PostAdapter;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gleb on 06.06.2015.
 */
public class Posts extends Pattern {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    public String[] arrayPosts = null;
    public int[] arrayIdPosts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts);

        listView = (ListView) findViewById(R.id.postsListView);
        View header = (View) getLayoutInflater().inflate(R.layout.viewheaderpost, null);
        listView.addHeaderView(header);
        new Loader().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dentist_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addRecord) {
            Intent intent = new Intent(this, InsertPost.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/PostScript.php");
            //Fields of table Posts

            Log.d(TAG, jsonContent);
            try {
                //create JSON array for parse it
                JSONArray array = new JSONArray(jsonContent);
                arrayPosts = new String[array.length()];
                arrayIdPosts = new int[array.length()];

                for (int i = 0; i < array.length(); i++){
                    //parse of array
                    JSONObject jObject = array.getJSONObject(i);
                    int id = jObject.getInt("IdPost");
                    String post = jObject.getString("Post");
                    arrayIdPosts[i] = id;
                    arrayPosts[i] = post;
                    Log.d(TAG, arrayPosts[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPosts;
        }

        @Override
        protected void onPostExecute(String[] value) {
            //adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, value);
            adapter = new PostAdapter(getBaseContext(), arrayPosts);
            listView.setAdapter(adapter);

        }
    }
}
