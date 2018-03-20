package com.example.learntoprogram;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class RedditUtils {
    final static String REDDIT_BASE_URL = "http://www.reddit.com";
    final static String REDDIT_SUBREDDIT_PARAM = "r";
    final static String REDDIT_SUBREDDIT_VALUE = "";
    final static String REDDIT_POST_TYPE_VALUE = "";
    final static String REDDIT_COUNT_PARAM = "limit";
    final static String REDDIT_COUNT_VALUE = "";
    final static String REDDIT_SORT_PARAM = "sort";
    final static String REDDIT_SORT_VALUE = "";

    public static class Post {
        public String title;
        public String user;
        public String subreddit;
        public String category;
        public String url;
        public String image;
        public Integer comments;
        public Integer upvotes;
        public Integer downvotes;
        public Integer timestamp;
    }

    public static String buildRedditURL(String subreddit, String postType, String postCount, String sortValue) {
        return Uri.parse( REDDIT_BASE_URL ).buildUpon()
                .appendPath( REDDIT_SUBREDDIT_PARAM )
                .appendPath( subreddit )
                .appendPath( postType )
                .appendQueryParameter(REDDIT_COUNT_PARAM, postCount)
                .appendQueryParameter(REDDIT_SORT_PARAM, sortValue)
                .build()
                .toString();
    }

    public static ArrayList<Post> parsePostsJSON(String PostsJSON) {

        try {
            JSONObject PostsObj = new JSONObject( PostsJSON );
            JSONArray items = PostsObj.getJSONObject("data").getJSONArray("children");

            ArrayList<Post> postsList = new ArrayList<Post>();

            for (int idx = 0; idx < items.length(); idx++) {
                Post post = new Post();
                JSONObject item = items.getJSONObject( idx ).getJSONObject("data");

                post.title = item.getString( "title" );
                post.user = item.getString( "author" );
                post.subreddit = item.getString( "subreddit" );
                post.category = "NA";
                post.url = item.getString( "url" );
                post.image = "NA";
                post.comments = item.getInt( "num_comments" );
                post.upvotes = item.getInt( "ups" );
                post.downvotes = item.getInt( "downs" );
                post.timestamp = item.getInt( "created_utc" );

                postsList.add( post );
            }

            return postsList;

        } catch (JSONException e) {
            return null;
        }

    }
}
