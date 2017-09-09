package com.example.vlkukushkin.lastfm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.HashMap;
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
    String albumText;
    String artistText;

    Context context;

    ProgressDialog progress;
    TextView description;
    TextView album;
    TextView group;
    TextView playcount;
    TextView listeners;
    ImageView albumImage;
    ListView songVIew;

    private Map<String,Object> data;


    @Override
    protected void onPause() {
        super.onPause();
        this.progress.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumImage = (ImageView) findViewById(R.id.activityAlbum_albumImage);
        album = (TextView) findViewById(R.id.activityAlbum_albumTitle);
        group = (TextView) findViewById(R.id.activityAlbum_groupTitle);
        playcount = (TextView) findViewById(R.id.playcountValue);
        listeners = (TextView) findViewById(R.id.listenersValue);
        description = (TextView) findViewById(R.id.activityAlbum_description    );
        songVIew = (ListView) findViewById(R.id.songsView);

        context = getApplicationContext();

        progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);

        intent = getIntent();

        mbid = intent.getStringExtra(MainActivity.MBID);
        albumText = intent.getStringExtra(MainActivity.ALBUM_NAME);
        artistText = intent.getStringExtra(MainActivity.ARTIST);

        album.setText(albumText);
        group.setText(artistText);

        SetAlbumData();
    }

    private void SetAlbumData() {
        progress.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "http://ws.audioscrobbler.com";
        if (mbid.isEmpty())
            url = url.concat("/2.0/?method=album.getinfo&api_key=&api_key=d9ec088659404f058418c14bbcd9d461&artist=Cher&album=Believe&format=json" +
                      "&artist=" + artistText + "&alb   um=" + albumText);
            else
            url = url.concat("/2.0/?method=album.getinfo&api_key=d9ec088659404f058418c14bbcd9d461&mbid="
                + mbid +"&format=json");
        Log.d("AlbumActivity/request",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        data = parseJSON(res);
                        Log.d("Logger",data.toString());
                        new DownloadImageTask(albumImage)
                                .execute(data.get(MEDIUM_IMAGE_URL).toString());
                        playcount.setText(data.get(PLAYCOUNT).toString());
                        listeners.setText(data.get(LISTENERS).toString());
                        description.setText(data.get(CONTENT).toString());
                        initSongView((List) data.get(TRACKS));
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

    private void initSongView(List listTracks){
        ArrayList tracks = new ArrayList();
        for (int i = 0; i < listTracks.size(); i++) {
            HashMap mapTrack = (HashMap) listTracks.get(i);
            tracks.add(mapTrack.get(NAME));
        }

        ArrayAdapter adapter = new ArrayAdapter(context,R.layout.song_list,R.id.songText, tracks);
        songVIew.setAdapter(adapter);
    }


    private Map<String,Object> parseJSON(JSONObject JSON) {

        ArrayMap<String, Object> map = new ArrayMap<String, Object>();

        try {
            JSONObject jsonObject =  JSON.getJSONObject(ALBUM);
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
            if (jsonObject.has("wiki")) {
            map.put(CONTENT,jsonObject.getJSONObject("wiki").getString(CONTENT).toString());
            } else map.put(CONTENT,"");
        } catch (JSONException e) {
            Log.d("JSON/parsing/error",e.toString());
        };
        return map;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}