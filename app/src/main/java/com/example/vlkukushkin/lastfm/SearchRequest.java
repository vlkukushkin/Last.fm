package com.example.vlkukushkin.lastfm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by vl.kukushkin on 11.09.2017.
 */

public class SearchRequest extends RealmObject {
    @Required
    private String request;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
