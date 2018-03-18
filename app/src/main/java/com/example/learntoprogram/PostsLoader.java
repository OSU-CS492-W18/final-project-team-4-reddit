package com.example.learntoprogram;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.learntoprogram.NetworkUtils;

import java.io.IOException;
/**
 * Created by vpiscitello on 3/17/18.
 */

public class PostsLoader extends AsyncTaskLoader<String> {
    private final static String TAG = PostsLoader.class.getSimpleName();

    private String mPostsJSON;
    private String mRedditURL;

    PostsLoader(Context context, String url) {
        super(context);
        mRedditURL = url;
    }

    @Override
    protected void onStartLoading() {
        if ( mRedditURL != null ) {
            if ( mPostsJSON != null ) {
                Log.d(TAG, "loader returning cached results");
                deliverResult( mPostsJSON );
            } else {
                forceLoad();
            }
        }
    }

    @Override
    public String loadInBackground() {
        if (mRedditURL != null) {
            Log.d(TAG, "loading results from Reddit with URL: " + mRedditURL);
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet( mRedditURL );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        } else {
            return null;
        }
    }

    @Override
    public void deliverResult(String data) {
        mPostsJSON = data;
        super.deliverResult(data);
    }

}
