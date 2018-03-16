package com.example.learntoprogram;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RedditUtils {
    final static String REDDIT_SEARCH_BASE_URL = "REDDIT API";   // TO DO
    final static String REDDIT_SEARCH_QUERY_PARAM = "q";         // TO DO
    final static String REDDIT_SEARCH_SORT_PARAM = "sort";       // TO DO
    final static String REDDIT_SEARCH_SORT_VALUE = "SORT VALUE"; // TO DO

    public static class SearchResult {
        public String threadName;
        public String description;
        public String htmlURL;
    }

    public static String buildRedditURL(String searchQuery) {
        return Uri.parse(REDDIT_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(REDDIT_SEARCH_QUERY_PARAM, searchQuery)
                .appendQueryParameter(REDDIT_SEARCH_SORT_PARAM, REDDIT_SEARCH_SORT_VALUE)
                .build()
                .toString();
    }

    public static ArrayList<SearchResult> parseSearchResultsJSON(String searchResultsJSON) {
        try {
            JSONObject searchResultsObj = new JSONObject(searchResultsJSON);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("items");

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
            for (int i = 0; i < searchResultsItems.length(); i++) {
                SearchResult result = new SearchResult();
                JSONObject resultItem = searchResultsItems.getJSONObject(i);
                result.threadName = resultItem.getString("");  // TO DO
                result.description = resultItem.getString(""); // TO DO
                result.htmlURL = resultItem.getString("");     // TO DO
                searchResultsList.add(result);
            }
            return searchResultsList;
        } catch (JSONException e) {
            return null;
        }
    }
}
