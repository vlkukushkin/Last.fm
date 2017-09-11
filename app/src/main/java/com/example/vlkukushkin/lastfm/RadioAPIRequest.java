package com.example.vlkukushkin.lastfm;

import android.content.Context;
import android.icu.util.TimeUnit;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by vl.kukushkin on 07.09.2017.
 */

public class RadioAPIRequest {

// API_KEY = "d9ec088659404f058418c14bbcd9d461";
// URL = "http://ws.audioscrobbler.com";
// API_VERSION = "2.0";

// FORMAT_JSON = "json";

    private Context context;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    String LOG_TAG = "Volly/log";

    private JSONObject result;
    private String url;

    RadioAPIRequest(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }


    public JSONObject albumSearch(String album) {
        result = null;

        url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="
            + album + "&api_key=d9ec088659404f058418c14bbcd9d461&format=json";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        JSONObject result = res;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.network_error,Toast.LENGTH_LONG);
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });
        return result;
    }

    public JSONObject albumGetInfo(String mbid) {
        result = null;

        url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=d9ec088659404f058418c14bbcd9d461&mbid="
                + mbid +"&format=json";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        result = res;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.network_error,Toast.LENGTH_LONG);
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());
            }
        });
//        try {
//            requestQueue.add(jsonObjectRequest);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        java.util.concurrent.TimeUnit.SECONDS.sleep(2);
        return result;
    }
}

//    String url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="
//            + query + "&api_key=d9ec088659404f058418c14bbcd9d461&format=json";
//    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject res) {
//                    progress.setVisibility(View.INVISIBLE);
//                    data = parseJSON(res);
//                    adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.album_list_item, from, to);
//                    albumsView.setAdapter(adapter);
//                }
//            }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            VolleyLog.d(LOG_VOLLY, "Error: " + error.getMessage());
//        }
//    });
//                    requestQueue.add(jsonObjectRequest);
//}
