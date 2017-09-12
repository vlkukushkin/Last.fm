package com.example.vlkukushkin.lastfm;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by vl.kukushkin on 11.09.2017.
 */

public class SearchQuery extends RealmObject {
    @Required
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
