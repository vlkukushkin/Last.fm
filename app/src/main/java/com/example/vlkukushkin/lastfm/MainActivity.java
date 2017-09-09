package com.example.vlkukushkin.lastfm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String MEDIUM_IMAGE_URL = "mediumImageURL";
    final String LOG_VOLLY = "LOG_Volly_MainActivity";

    final static String ALBUM_NAME = "name";
    final static String ALBUM_IMAGE = "album_image";
    final static String ARTIST = "artist";
    final static String MBID = "mbid";

    SearchView searchView;
    ListView albumsView;

    ProgressDialog progress;

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

        progress = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);

        context = getApplicationContext();

        searchView = (SearchView) findViewById(R.id.search);

        albumsView = (ListView) findViewById(R.id.albums);

        from = new String[] {ALBUM_NAME, ARTIST, "ICON"};
        to = new int[]{R.id.itemList_albumTitle, R.id.itemList_groupTitle,R.id.itemList_albumImage};

        albumsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mbidAlbum = data.get(position).get(MBID).toString();
                String album = data.get(position).get(ALBUM_NAME).toString();
                String artist = data.get(position).get(ARTIST).toString();
                intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra(MBID,mbidAlbum);
                intent.putExtra(ARTIST, artist);
                intent.putExtra(ALBUM_NAME, album);
                MainActivity.this.startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    //  Load and set albums data
                    findAlbums(query);
                    searchView.clearFocus();

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                        adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.album_list, from, to);
                        albumsView.setAdapter(adapter);
                        albumsView.getAdapter();
                        progress.hide();
                        for (int i = 0; i < 5; i++) {
                            View v = albumsView.getAdapter().getView(1,null,albumsView);
                            TextView textView =(TextView) v.findViewById(R.id.itemList_groupTitle);
                            textView.setText("1233");
                            albumsView.getChildAt(1).setBackgroundColor(Color.RED);
                            albumsView.invalidateDrawable();
                            //ImageView albumImage = (ImageView) albumViewItem.findViewById(R.id.itemList_albumImage);
                            // new DownloadImageTask(albumImage)
                                    //.execute(data.get(i).get(MEDIUM_IMAGE_URL).toString());
                        }
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Log.d("Loading image IRL:", urls[0]);
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
