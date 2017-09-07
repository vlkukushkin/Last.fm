package com.example.vlkukushkin.lastfm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

public class AlbumActivity extends AppCompatActivity {

    Intent intent;

    String mbid;
    String album;
    String artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mbid = intent.getStringExtra(MainActivity.MBID);

        JSONObject albumJSON = new RadioAPIRequest(getApplicationContext())
                .albumGetInfo(mbid);
        Log.d("Logger",albumJSON.toString());
    }
}
