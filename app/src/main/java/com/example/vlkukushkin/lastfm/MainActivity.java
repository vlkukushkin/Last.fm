package com.example.vlkukushkin.lastfm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    ListView albumsView;

    final String LOG_TAG = "LOG_TAG";

    final String ALBUM_TITLE = "album_title";
    final String ALBUM_IMAGE = "album_image";
    final String GROUP_TITLE = "album_image";

    JSONObject response;

    SimpleAdapter adapter;

    private String[] from;
    private int[] to;
    private List<Map<String,Object>> data;
    private Map<String,Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.search);

        albumsView = (ListView) findViewById(R.id.albums);


        from = new String[] {ALBUM_TITLE, ALBUM_IMAGE, GROUP_TITLE};
        to = new int[]{R.id.albumTitle, R.id.albumImage, R.id.groupTitle};

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    getALbum(query);
//                    response.length()
//                    data = convertData
                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.item_list, from, to);
                    albumsView.setAdapter(adapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    void getALbum(String album){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="
                + album + "&api_key=d9ec088659404f058418c14bbcd9d461&format=json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        response = res;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });
    requestQueue.add(jsonObjectRequest);
    }
}
