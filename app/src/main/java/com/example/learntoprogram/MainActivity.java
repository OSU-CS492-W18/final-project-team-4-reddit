package com.example.learntoprogram;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplication().setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);

        PostsDBHelper dbHelper = new PostsDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
        dbHelper.clearTable( mDB );

        mRedditThreadsRV = (RecyclerView) findViewById(R.id.rv_reddit_threads);

        mRedditThreadsRV.setLayoutManager(new LinearLayoutManager(this));
        mRedditThreadsRV.setHasFixedSize(true);

        mRedditAdapter = new RedditAdapter(this);
        mRedditThreadsRV.setAdapter(mRedditAdapter);

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

//        doRedditSearch("cpp", "new.json", "50", "new" );
//        doRedditSearch("java", "new.json", "50", "new" );
//        doRedditSearch("Python", "new.json", "50", "new" );
//        doRedditSearch("golang", "new.json", "50", "new" );
//        doRedditSearch("javascript", "new.json", "50", "new" );
        doRedditSearch("learnprogramming", "new.json", "50", "new" );

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        // Nothing ...
    }



}