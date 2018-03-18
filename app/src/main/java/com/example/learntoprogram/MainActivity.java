package com.example.learntoprogram;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

public class MainActivity extends AppCompatActivity
        implements RedditAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<String> {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SEARCH_URL_KEY = "redditURL";
    private final static int POST_LOADER_ID = 0;

    private SQLiteDatabase mDB;

    // BEGIN DUMMY DATA
    private RecyclerView mRedditThreadsRV;
    private RedditAdapter mRedditAdapter;
    private Toast mToast;

    // can keep these
    private EditText mSearchBoxET;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;

    private static final String[] dummyRedditThreads = {
            "Dummy thread 1",
            "Dummy thread 2",
            "Dummy thread 3",
            "Dummy thread 4",
            "Dummy thread 5",
            "Dummy thread 6",
            "Dummy thread 7",
            "Dummy thread 8",
            "Dummy thread 9",
            "Dummy thread 10",
            "Dummy thread 11",
            "Dummy thread 12"
    };

    private static final String[] dummyDetailRedditThreads = {
            "Dummy detail thread 1",
            "Dummy detail thread 2",
            "Dummy detail thread 3",
            "Dummy detail thread 4",
            "Dummy detail thread 5",
            "Dummy detail thread 6",
            "Dummy detail thread 7",
            "Dummy detail thread 8",
            "Dummy detail thread 9",
            "Dummy detail thread 10",
            "Dummy detail thread 11",
            "Dummy detail thread 12"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostsDBHelper dbHelper = new PostsDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
        dbHelper.clearTable( mDB );

        mRedditThreadsRV = (RecyclerView) findViewById(R.id.rv_reddit_threads);

        mRedditThreadsRV.setLayoutManager(new LinearLayoutManager(this));
        mRedditThreadsRV.setHasFixedSize(true);

        mRedditAdapter = new RedditAdapter(this);
        mRedditThreadsRV.setAdapter(mRedditAdapter);

        mRedditAdapter.updateRedditData(
                new ArrayList<String>(Arrays.asList(dummyRedditThreads)),
                new ArrayList<String>(Arrays.asList(dummyDetailRedditThreads))
        );

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        mSearchBoxET = (EditText)findViewById(R.id.et_search_box);

        Button searchButton = (Button)findViewById(R.id.btn_search);

        doRedditSearch("learnprogramming", "new.json", "25", "new" );
    }

    private void doRedditSearch(String subreddit, String postType, String postCount, String sortValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String redditURL = RedditUtils.buildRedditURL( subreddit, postType, postCount, sortValue );
        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, redditURL);

        mLoadingProgressBar.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(POST_LOADER_ID, args, this);
    }

    @Override
    public void onItemClick(String detailedReddit) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, detailedReddit, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String url = null;
        if (args != null) {
            url = args.getString(SEARCH_URL_KEY);
        }
        return new PostsLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        PostsDBHelper dbHelper = new PostsDBHelper(this);

        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        Log.d( TAG, "got results from loader: " + data );
        if (data != null) {
            ArrayList<RedditUtils.Post> posts = RedditUtils.parsePostsJSON( data );
            System.out.println( "POSTS LOADED: " + posts.size() );
            dbHelper.storePosts( mDB, posts );
            mRedditAdapter.updatePosts( mDB );
            mLoadingErrorMessage.setVisibility(View.INVISIBLE);
            mRedditThreadsRV.setVisibility(View.VISIBLE);
        } else {
            mRedditThreadsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


}
// END DUMMY DATA

// BEGIN ACTUAL CONTENT... ONCE API IS HOOKED UP
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        mSearchBoxET = (EditText)findViewById(R.id.et_search_box);
        mRedditThreadsRV = (RecyclerView)findViewById(R.id.rv_reddit_threads);

        mRedditThreadsRV.setLayoutManager(new LinearLayoutManager(this));
        mRedditThreadsRV.setHasFixedSize(true);

        mRedditAdapter = new RedditAdapter();
        mRedditThreadsRV.setAdapter(mRedditAdapter);

        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doRedditSearch(searchQuery);
                }
            }
        });

   private void doRedditSearch(String searchQuery) {
        String redditURL = RedditUtils.buildRedditURL(searchQuery);
        new RedditSearchTask().execute(redditURL);
    }

    public class RedditSearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String redditSearchURL = urls[0];

            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(redditSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (s != null) {
                ArrayList<RedditUtils.SearchResult> searchResultsList = RedditUtils.parseSearchResultsJSON(s);
                mRedditAdapter.updateSearchResults(searchResultsList);
                mSearchBoxET.setText("");
                mLoadingErrorMessage.setVisibility(View.INVISIBLE);
                mRedditThreadsRV.setVisibility(View.VISIBLE);
            } else {
                mRedditThreadsRV.setVisibility(View.INVISIBLE);
                mLoadingErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

}*/