package com.example.learntoprogram;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.PreferenceChangeEvent;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

public class MainActivity extends AppCompatActivity
        implements RedditAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SEARCH_URL_KEY = "redditURL";
    private final static int POST_LOADER_ID = 0;

    private SQLiteDatabase mDB;

    private RecyclerView mRedditThreadsRV;
    private RedditAdapter mRedditAdapter;

    private EditText mSearchBoxET;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;

    public String url;

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

        mRedditThreadsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if ( !recyclerView.canScrollVertically( 1 ) ) {

                    Cursor cursor = mRedditAdapter.getLastPost( mRedditAdapter.getItemCount() - 1 );

                    String id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    PostsContract.LoadedPosts.COLUMN_POST_ID36
                            )
                    );

                    doRedditSearch( "learnprogramming+cpp+Python+javascript+golang", "new.json", id, "25", "new" );

                }

            }
        });

        Cursor cursor = mDB.rawQuery( "SELECT * FROM " + PostsContract.LoadedPosts.TABLE_NAME, null );
        mRedditAdapter.updatePosts( cursor );

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        mSearchBoxET = (EditText)findViewById(R.id.et_search_box);

        Button filterButton = (Button)findViewById(R.id.btn_search);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText = mSearchBoxET.getText().toString().toUpperCase();
                String filterQuery = RedditUtils.parseThreadCategory( filterText );

                Cursor cursor;

                if ( !TextUtils.isEmpty( filterQuery ) ) {
                    cursor = mDB.rawQuery(
                            "SELECT * FROM " +
                                    PostsContract.LoadedPosts.TABLE_NAME +
                                    " WHERE " + PostsContract.LoadedPosts.COLUMN_POST_CATEGORY +
                                    "='" + filterQuery + "'",
                            null
                    );
                } else {
                    cursor = mDB.rawQuery(
                            "SELECT * FROM " + PostsContract.LoadedPosts.TABLE_NAME,
                            null
                    );
                }

                mRedditAdapter.updatePosts( cursor );
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        loadPosts(sharedPreferences, true);
        //doRedditSearch( "learnprogramming+cpp+Python+javascript+golang", "new.json", null, "25", "new" );

        getSupportLoaderManager().initLoader(POST_LOADER_ID, null, this);

    }

    private void doRedditSearch(String subreddit, String postType, String after, String postCount, String sortValue) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String redditURL = RedditUtils.buildRedditURL( subreddit, postType, after, postCount, sortValue );
        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, redditURL);

        mLoadingProgressBar.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(POST_LOADER_ID, args, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemClick(RedditUtils.Post post) {
        Intent detailedPostIntent = new Intent(this, RedditDetailActivity.class);
        detailedPostIntent.putExtra(RedditUtils.EXTRA_POST, post);
        startActivity(detailedPostIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        mDB.close();
        super.onDestroy();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String url = null;
        if (args != null) {
            url = args.getString(SEARCH_URL_KEY);
        }
        return new PostsLoader(this, url);
    }

    public void loadPosts(SharedPreferences sharedPreferences, boolean initialLoad) {
        String textColor = sharedPreferences.getString(
                getString(R.string.pref_text_color_key),
                getString(R.string.pref_text_color_default_value)
        );

        String theme = sharedPreferences.getString(
                getString(R.string.pref_theme_key),
                getString(R.string.pref_theme_default_value)
        );

        mLoadingProgressBar.setVisibility(View.VISIBLE);

        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(SEARCH_URL_KEY, url);
        LoaderManager loaderManager = getSupportLoaderManager();
        if (initialLoad) {
            loaderManager.initLoader(POST_LOADER_ID, loaderArgs, this);
            Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ INITIAL LOAD");
            doRedditSearch( "learnprogramming+cpp+Python+javascript+golang", "new.json", null, "25", "new" );
        } else {
            loaderManager.restartLoader(POST_LOADER_ID, loaderArgs, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        PostsDBHelper dbHelper = new PostsDBHelper(this);

        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {

            ArrayList<RedditUtils.Post> posts = RedditUtils.parsePostsJSON( data );
            dbHelper.storePosts( mDB, posts );

            Cursor cursor = mDB.rawQuery(
                    "SELECT * FROM " + PostsContract.LoadedPosts.TABLE_NAME,
                    null
            );

            mRedditAdapter.updatePosts( cursor );

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadPosts(sharedPreferences, false);

        String forecastLocation = sharedPreferences.getString(
                getString(R.string.pref_text_color_key),
                getString(R.string.pref_text_color_default_value)
        );

    }

}