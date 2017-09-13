package com.example.vlkukushkin.lastfm;

import android.net.Uri;
import android.util.Log;

/**
 * Created by vl.kukushkin on 12.09.2017.
 */

public class ApiURLConstructor {
    //methods
    public static final String ALBUM_SEARCH = "album.search";
    public static final String ALBUM_GETINFO = "album.getinfo";


    public static final String SCHEME = "http";
    public static final String  AUTHORITY = "ws.audioscrobbler.com";
    public static final String  PATH_VERSION = "2.0";

    public static final String  METHOD = "method";

    public static final String  API_KEY = "api_key";
    public static final String  API_KEY_VALUE = "d9ec088659404f058418c14bbcd9d461";

    public static final String  FORMAT = "format";
    public static final String  JSON = "json";

    public static final String  ARTIST = "artist";
    public static final String  ALBUM = "album";
    public static final String  MBID= "mbid";


/*
        api Request Example:
        http://ws.audioscrobbler.com/2.0?method=album.search&api_key=d9ec088659404f058418c14bbcd9d461&format=json&album=smoke
*/

    public static String albumSearch(String album) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ApiURLConstructor.SCHEME)
                .authority(ApiURLConstructor.AUTHORITY)
                .appendPath(ApiURLConstructor.PATH_VERSION)
                .appendQueryParameter(METHOD,ALBUM_SEARCH)
                .appendQueryParameter(API_KEY,API_KEY_VALUE)
                .appendQueryParameter(FORMAT,JSON)
                .appendQueryParameter(ALBUM,album);

        String url = builder.build().toString();

        return url;
    }

    /*
        api Request Example:


*/
    public static String albumGetInfo(String mbid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ApiURLConstructor.SCHEME)
                .authority(ApiURLConstructor.AUTHORITY)
                .appendPath(ApiURLConstructor.PATH_VERSION)
                .appendQueryParameter(METHOD,ALBUM_GETINFO)
                .appendQueryParameter(API_KEY,API_KEY_VALUE)
                .appendQueryParameter(FORMAT,JSON)
                .appendQueryParameter(MBID,mbid);

        String url = builder.build().toString();

        return url;
    }

    public static String albumGetInfo(String artist, String album) {
        Log.d("artist",artist);
        Log.d("album",album);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(ApiURLConstructor.SCHEME)
                .authority(ApiURLConstructor.AUTHORITY)
                .appendPath(ApiURLConstructor.PATH_VERSION)
                .appendQueryParameter(METHOD,ALBUM_GETINFO)
                .appendQueryParameter(API_KEY,API_KEY_VALUE)
                .appendQueryParameter(FORMAT,JSON)
                .appendQueryParameter(ARTIST,artist)
                .appendQueryParameter(ALBUM,album);

        String url = builder.build().toString();

        return url;
    }
}
