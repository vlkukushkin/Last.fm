package com.example.vlkukushkin.lastfm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlbumActivity extends AppCompatActivity {

    public static final String ALBUM = "album";
    public static final String NAME = "name";
    public static final String ARTIST = "artist";
    public static final String URL = "url";
    public static final String PLAYCOUNT = "playcount";
    public static final String LISTENERS = "listeners";
    public static final String MEDIUM_IMAGE_URL = "medium_image_url";
    public static final String TRACKS = "tracks";
    public static final String TRACK = "track";
    public static final String CONTENT = "content";

    final String LOG_VOLLY = "LOG_Volly_AlbumActivity";

    Intent intent;

    String mbid;

    Context context;

    ProgressDialog progress;

    private Map<String,Object> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        context = getApplicationContext();

        progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);

        intent = getIntent();

        mbid = intent.getStringExtra(MainActivity.MBID);

        setAlbumsData();
    }

    private void setAlbumsData() {
        progress.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=d9ec088659404f058418c14bbcd9d461&mbid="
                + mbid +"&format=json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        data = parseJSON(res);
                        Log.d("Logger",data.toString());
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

    private Map<String,Object> parseJSON(JSONObject JSON) {

        Map<String, Object> map = new ArrayMap<String, Object>();

        try {
            JSONObject jsonObject =  JSON.getJSONObject("results").getJSONObject(ALBUM);
            map.put(NAME,jsonObject.getString(NAME));
            map.put(ARTIST,jsonObject.getString(ARTIST));
            map.put(URL,jsonObject.getString(URL));
            map.put(PLAYCOUNT,jsonObject.getInt(PLAYCOUNT));
            map.put(LISTENERS,jsonObject.getInt(LISTENERS));
            String mediumImageURL = jsonObject.getJSONArray("image").getJSONObject(3).getString("#text");
            map.put(MEDIUM_IMAGE_URL, mediumImageURL);

            JSONArray jsonTracks = jsonObject.getJSONObject(TRACKS).getJSONArray(TRACK);
            List tracks = new ArrayList();

            for (int i = 0; i < jsonTracks.length(); i++) {
                Map track = new HashMap();

                JSONObject jsonTrackObject = jsonTracks.getJSONObject(i);
                String trackName = jsonTrackObject.getString("name");
                String trackURL = jsonTrackObject.getString("url");
                track.put(NAME, trackName);
                track.put(URL, trackURL);

                tracks.add(track);

            }
            map.put(TRACKS,tracks);

            map.put(CONTENT,jsonObject.getJSONObject("wiki").getString(CONTENT));

        } catch (JSONException e) {
            Log.d("JSON/parsing/error",e.toString());
        };

        return map;
    }

}