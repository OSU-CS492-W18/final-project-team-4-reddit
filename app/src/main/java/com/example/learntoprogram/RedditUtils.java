package com.example.learntoprogram;

import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RedditUtils {

    public static final String EXTRA_POST = "RedditUtils.Post";

    final static String REDDIT_BASE_URL = "http://www.reddit.com";
    final static String REDDIT_SUBREDDIT_PARAM = "r";
    final static String REDDIT_SUBREDDIT_VALUE = "";
    final static String REDDIT_POST_TYPE_VALUE = "";
    final static String REDDIT_AFTER_PARAM = "after";
    final static String REDDIT_AFTER_VALUE = "";
    final static String REDDIT_COUNT_PARAM = "limit";
    final static String REDDIT_COUNT_VALUE = "";
    final static String REDDIT_SORT_PARAM = "sort";
    final static String REDDIT_SORT_VALUE = "";

    final static Pattern C_PATTERN = Pattern.compile("(( ?(\\[C\\]) ?)|(\\b(C(?!\\+|#))\\b))");
    final static Pattern CPP_PATTERN = Pattern.compile("(( ?(\\[CPP\\])|(\\[C\\+\\+\\]) ?)|(\\b(CPP)|(C\\+\\+) ?))");
    final static Pattern JAVA_PATTERN = Pattern.compile("(( ?(\\[JAVA\\]) ?)|(\\b(JAVA)\\b))");
    final static Pattern PYTHON_PATTERN = Pattern.compile("(( ?(\\[PYTHON\\]) ?)|(\\b(PYTHON) ?))");
    final static Pattern HTML_PATTERN = Pattern.compile("(( ?(\\[HTML\\]) ?)|(\\b(HTML) ?))");
    final static Pattern JAVASCRIPT_PATTERN = Pattern.compile("(( ?(\\[JAVASCRIPT\\])|(\\[JS\\])|(\\.JS) ?)|(\\b(JAVASCRIPT)|(JS)\\b))");
    final static Pattern PHP_PATTERN = Pattern.compile("(( ?(\\[PHP\\]) ?)|(\\b(PHP) ?))");
    final static Pattern GOLANG_PATTERN = Pattern.compile("(( ?(\\[GOLANG\\])|(\\[GO\\]) ?)|(\\b(GOLANG)|(GO)\\b))");
    final static Pattern SWIFT_PATTERN = Pattern.compile("(( ?(\\[SWIFT\\]) ?)|(\\b(SWIFT) ?)|(\\b(IOS) ?))");
    final static Pattern RUBY_PATTERN = Pattern.compile("(( ?(\\[RUBY\\]) ?)|(\\b(RUBY) ?))");
    final static Pattern CSHARP_PATTERN = Pattern.compile("(( ?(\\[C#\\]) ?)|(\\b(C#) ?))");


    final static Pattern[] patterns = {
            C_PATTERN,
            CPP_PATTERN,
            JAVA_PATTERN,
            PYTHON_PATTERN,
            HTML_PATTERN,
            JAVASCRIPT_PATTERN,
            PHP_PATTERN,
            GOLANG_PATTERN,
            SWIFT_PATTERN,
            RUBY_PATTERN,
            CSHARP_PATTERN
    };

    final static String[] categories = {
            "C",
            "CPP",
            "JAVA",
            "PYTHON",
            "HTML",
            "JS",
            "PHP",
            "GO",
            "SWIFT",
            "RUBY",
            "CSHARP"
    };

    public static class Post implements Serializable {
        public String title;
        public String user;
        public String subreddit;
        public String category;
        public String url;
        public String id36;
        public Integer comments;
        public Integer upvotes;
        public Integer downvotes;
        public Integer timestamp;
    }

    public static String buildRedditURL(String subreddit, String postType, String after, String postCount, String sortValue) {
        return Uri.parse( REDDIT_BASE_URL ).buildUpon()
                .appendPath( REDDIT_SUBREDDIT_PARAM )
                .appendPath( subreddit )
                .appendPath( postType )
                .appendQueryParameter(REDDIT_AFTER_PARAM, after)
                .appendQueryParameter(REDDIT_COUNT_PARAM, postCount)
                .appendQueryParameter(REDDIT_SORT_PARAM, sortValue)
                .build()
                .toString();
    }

    public static Post parseRowPost(Cursor cursor) {
        Post post = new Post();

        post.title = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_TITLE
                )
        );
        post.user = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_USER
                )
        );
        post.subreddit = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_SUBREDDIT
                )
        );
        post.url = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_URL
                )
        );
        post.id36 = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_ID36
                )
        );
        post.comments = (int)cursor.getLong(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_COMMENT_COUNT
                )
        );
        post.upvotes = (int)cursor.getLong(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_UPVOTES
                )
        );
        post.downvotes = (int)cursor.getLong(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_DOWNVOTES
                )
        );
        post.timestamp = (int)cursor.getLong(
                cursor.getColumnIndexOrThrow(
                        PostsContract.LoadedPosts.COLUMN_POST_TIMESTAMP
                )
        );

        return post;
    }

    public static String parseThreadCategory(String title) {
        String category = "";
        String temp = title.toUpperCase();

        for (int idx = 0; idx < patterns.length; idx++ ) {
            Matcher matcher = patterns[ idx ].matcher( temp );
            if ( matcher.find() ) {
                return categories[ idx ];
            }
        }

        return "NA";
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
                post.url = item.getString( "url" );
                post.id36 = item.getString( "name" );
                post.comments = item.getInt( "num_comments" );
                post.upvotes = item.getInt( "ups" );
                post.downvotes = item.getInt( "downs" );
                post.timestamp = item.getInt( "created_utc" );

                post.category = parseThreadCategory( post.title );

                System.out.println( post.category + "  ==  " + post.title );

                postsList.add( post );
            }

            return postsList;

        } catch (JSONException e) {
            return null;
        }

    }
}
