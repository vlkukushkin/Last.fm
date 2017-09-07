package com.example.vlkukushkin.lastfm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    ListView albumsView;

    ProgressBar progress;

    final String LOG_TAG = "LOG_TAG";

    final static String ALBUM_NAME = "name";
    final static String ALBUM_IMAGE = "album_image";
    final static String ARTIST = "artist";
    final static String MBID = "mbid";

    SimpleAdapter adapter;

    private String[] from;
    private int[] to;
    private List<Map<String,Object>> data;

    public Context context;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        context = getApplicationContext();

        searchView = (SearchView) findViewById(R.id.search);

        albumsView = (ListView) findViewById(R.id.albums);

        from = new String[] {ALBUM_NAME, ARTIST};
        to = new int[]{R.id.itemList_albumTitle, R.id.itemList_groupTitle};

        albumsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mbidAlbum = data.get(position).get(MBID).toString();
                intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra(MBID,mbidAlbum);
                MainActivity.this.startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    //@TODO edit method
                    progress.setVisibility(View.VISIBLE);
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    String url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="
                            + query + "&api_key=d9ec088659404f058418c14bbcd9d461&format=json";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject res) {
                                    progress.setVisibility(View.INVISIBLE);
                                    data = parseJSON(res);
                                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.album_list, from, to);
                                    albumsView.setAdapter(adapter);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
//                    data = convertData

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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

                map.put("name",name);
                map.put("artist",artist);
                map.put(MBID, mbid);
                map.put("mediumImageURL",mediumImageURL);
                list.add(map);
            }
        } catch (JSONException e) {
            Log.d("JSON/parsing/error:",e.toString());
        };

        return list;
    }

}
