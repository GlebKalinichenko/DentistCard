package com.example.gleb.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gleb.adapters.PostAdapter;
import com.example.gleb.dentistcard.DatabaseRequest;
import com.example.gleb.dentistcard.Pattern;
import com.example.gleb.dentistcard.R;
import com.example.gleb.insert.InsertChange;
import com.example.gleb.insert.InsertPost;
import com.example.gleb.tables.Post;
import com.mikepenz.materialdrawer.Drawer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gleb on 13.07.15.
 */
public class PostFragment extends Fragment {
    public static final String TAG = "TAG";
    private DatabaseRequest request = new DatabaseRequest();
    private PostAdapter adapter;
    public String[] arrayPosts = null;
    public int[] arrayIdPosts = null;
    public EditText oldPostEditText;
    public EditText newPostEditText;

    private List<Post> posts;
    private RecyclerView rv;
    public ImageButton addImageButton;
    public ActionMode actionMode;
    public ArrayList<Integer> positions;

    private Drawer.Result drawerResult = null;

    protected HttpClient client;
    protected HttpPost post;

    public String profile;

    public PostFragment(String profile) {
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.particientactivity,container,false);

        rv = (RecyclerView) v.findViewById(R.id.rv);
        posts = new ArrayList<>();
        addImageButton = (ImageButton) v.findViewById(R.id.addFloatingButton);
        positions = new ArrayList<Integer>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertPost.class);
                intent.putExtra(InsertPost.PROFILE, profile);
                startActivity(intent);
            }
        });

        new Loader().execute();

        rv.addOnItemTouchListener(
                new Pattern.RecyclerItemClickListener(getActivity(), new Pattern.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        boolean wrapInScrollView = true;
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.UpdatePost)
                                .customView(R.layout.update_post, wrapInScrollView)
                                .positiveText("Подтвердить")
                                .negativeText("Отмена")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }

                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        if (newPostEditText.getText().toString().equals("")) {
                                            Toast.makeText(getActivity(), R.string.AddContent, Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            new Updater().execute();
                                        }
                                    }
                                })
                                .show();

                        View v = dialog.getCustomView();

                        oldPostEditText = (EditText) v.findViewById(R.id.oldPostEditText);
                        oldPostEditText.append(arrayPosts[position]);
                        newPostEditText = (EditText) v.findViewById(R.id.newPostEditText);
                    }

                    @Override
                    public void onItemLongPress(View childView, int position) {
                        positions.add(position);
                        actionMode = getActivity().startActionMode(callback);
                    }
                })
        );

        return v;


    }

    private ActionMode.Callback callback = new ActionMode.Callback() {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_header, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    for (int i = 0; i < positions.size(); i++){
                        new Deleter(positions.get(i)).execute();

                    }
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "destroy");
            actionMode = null;
        }

    };

    public class Loader extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            //String with JSON
            String jsonContent = request.makeRequest("http://dentists.16mb.com/SelectQuery/PostScript.php");
            //Fields of table PostsActivity

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

                    posts.add(new Post(id, post));

                    Log.d(TAG, arrayPosts[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrayPosts;
        }

        @Override
        protected void onPostExecute(String[] value) {
            adapter = new PostAdapter(posts);
            rv.setAdapter(adapter);

        }
    }

    /**
     * Update record from table PostsActivity
     */
    public class Updater extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String oldPost = oldPostEditText.getText().toString();
            String newPost = newPostEditText.getText().toString();

            Log.d(TAG, "Old Kvalification " + oldPost);
            Log.d(TAG, "New Kvalification " + newPost);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/UpdateScript/UpdatePostScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("oldPost", oldPost);
                json.put("newPost", newPost);

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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }

    public class Deleter extends AsyncTask<String, String, String>{
        private int positionPartcient;

        public Deleter(int positionPartcient) {
            this.positionPartcient = positionPartcient;
        }

        @Override
        protected String doInBackground(String... params) {
            String posts = arrayPosts[positionPartcient];
            Log.d(TAG, posts);

            client = new DefaultHttpClient();
            post = new HttpPost("http://dentists.16mb.com/DeleteScript/DeletePostScript.php");
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("Post", posts);
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
            Toast.makeText(getActivity(), R.string.Send, Toast.LENGTH_SHORT).show();
        }
    }
}
