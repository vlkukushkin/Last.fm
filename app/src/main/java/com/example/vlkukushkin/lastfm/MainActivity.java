package com.example.vlkukushkin.lastfm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    final String LOG_VOLLY = "LOG_Volly_MainActivity";

    final static String ALBUM_NAME = "name";
    final static String ARTIST = "artist";
    final static String MBID = "mbid";
    public static final String MEDIUM_IMAGE_URL = "mediumImageURL";

    SearchView searchView;
    RecyclerView albumsView;
    ProgressDialog progress;

    String url;

    private Realm realm;
    private List<Map<String,Object>> data;
    private RVAdapter rvAdapter;

    public Context context;
    Intent intent;

    LinkedList <String>searchRequsts;
    List<SearchItem> suggestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();

        searchView = (SearchView) findViewById(R.id.searchView);

        RealmResults<SearchQuery> allSearchReqests = realm.where(SearchQuery.class).findAll();
        suggestionsList = new ArrayList<>();

        for (int i = 0; i < allSearchReqests.size(); i++) {
            String savedRequest = allSearchReqests.get(i).getQuery();
            suggestionsList.add(new SearchItem(savedRequest));
        }

        SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
        searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {;
                CharSequence queryText = suggestionsList.get(position).get_text();
                Log.d("Clicked #",String.valueOf(position-1));
                searchView.setQuery(queryText,true);
            }
        });
        searchView.setVoice(false);
        searchView.setAdapter(searchAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    if (suggestionsList.size() >= 10) {
                        suggestionsList.remove(0);
                        suggestionsList.add(new SearchItem(query));
                    } else
                        suggestionsList.add(new SearchItem(query));
                    realm.beginTransaction();
                    realm.delete(SearchQuery.class);
                    for (int i = 0; i < suggestionsList.size(); i++) {
                       SearchQuery searchQuery = realm.createObject(SearchQuery.class);
                       searchQuery.setQuery(suggestionsList.get(i).get_text().toString());
                    }
                    realm.commitTransaction();
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


    private void findAlbums(String query) {
        progress.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ApiURLConstructor.albumSearch(query);
        url = ApiURLConstructor.albumSearch(query);
        Log.d(this.getLocalClassName() + "/request",url);
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
                progress.hide();
                Toast.makeText(context, R.string.network_error,Toast.LENGTH_SHORT).show();
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
                String artist = jsonObject.getString(ARTIST);
                String mbid = jsonObject.getString(MBID);
                String mediumImageURL = jsonObject.getJSONArray("image").getJSONObject(2).getString("#text");
                map.put("ICON", R.drawable.ic_cd);
                map.put("name",name);
                map.put(ARTIST,artist);
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
