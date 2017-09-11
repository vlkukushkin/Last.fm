package com.example.vlkukushkin.lastfm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final String MEDIUM_IMAGE_URL = "mediumImageURL";
    final String LOG_VOLLY = "LOG_Volly_MainActivity";

    final static String ALBUM_NAME = "name";
    final static String ALBUM_IMAGE = "album_image";
    final static String ARTIST = "artist";
    final static String MBID = "mbid";

    SearchView searchView;
    RecyclerView albumsView;

    ProgressDialog progress;


    private Realm realm;

    private List<Map<String,Object>> data;

    private RVAdapter rvAdapter;

    public Context context;

    Intent intent;

    LinkedList <String>searchRequsts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);

        setContentView(R.layout.activity_main);


        realm = Realm.getDefaultInstance();

//        realm.beginTransaction();
//        SearchRequest searchRequst = realm.createObject(SearchRequest.class);
//        searchRequst.setRequest("Test_test");
//        realm.commitTransaction();

        RealmResults<SearchRequest> allSearchRequsts = realm.where(SearchRequest.class).findAll();
        Log.d("tagger",allSearchRequsts.toString());

        searchRequsts = new <String>LinkedList();

        progress = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);

        context = getApplicationContext();

        albumsView = (RecyclerView) findViewById(R.id.albums);


        LinearLayoutManager llm = new LinearLayoutManager(context);
        albumsView.setLayoutManager(llm);

        albumsView.addOnItemTouchListener(
                new RecyclerItemClickListener(context,albumsView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String mbidAlbum = data.get(position).get(MBID).toString();
                        String album = data.get(position).get(ALBUM_NAME).toString();
                        String artist = data.get(position).get(ARTIST).toString();
                        intent = new Intent(MainActivity.this, AlbumActivity.class);
                        intent.putExtra(MBID,mbidAlbum);
                        intent.putExtra(ARTIST, artist);
                        intent.putExtra(ALBUM_NAME, album);
                        MainActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    if (searchRequsts.size() >= 10) searchRequsts.remove();
                    searchRequsts.add(query);
                    //  Load and set albums data
                    findAlbums(query);
                    searchView.clearFocus();
//
                }
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }

    private void findAlbums(String query) {
        progress.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="
                + query + "&api_key=d9ec088659404f058418c14bbcd9d461&format=json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        data = parseJSON(res);
                        rvAdapter = new RVAdapter(data);
                        albumsView.setAdapter(rvAdapter);

                        progress.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.network_error,Toast.LENGTH_LONG).show();
                VolleyLog.d(LOG_VOLLY, "Error: " + error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private List<Map<String,Object>> parseJSON(JSONObject JSON) {

        List <Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;

        try {
            JSONArray jsonArray =  JSON.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");


            for (int i = 0; i < jsonArray.length(); i++) {
                map = new ArrayMap<>();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String artist = jsonObject.getString("artist");
                String mbid = jsonObject.getString(MBID);
                String mediumImageURL = jsonObject.getJSONArray("image").getJSONObject(1).getString("#text");
                map.put("ICON", R.drawable.ic_cd);
                map.put("name",name);
                map.put("artist",artist);
                map.put(MBID, mbid);
                map.put(MEDIUM_IMAGE_URL,mediumImageURL);
                list.add(map);
            }
        } catch (JSONException e) {
            Log.d("JSON/parsing/error",e.toString());
        };

        return list;
    }
}
