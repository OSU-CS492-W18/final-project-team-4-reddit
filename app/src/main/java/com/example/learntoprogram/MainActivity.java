package com.example.learntoprogram;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
        LoaderManager.LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener{

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

    public String url;

    private static final String THREADS_LOADED_KEY = "THREADS_LOADED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets color scheme
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        setUpTheme(pref);

        boolean threadsLoaded = false;

        if ( savedInstanceState != null && savedInstanceState.containsKey( THREADS_LOADED_KEY ) ) {
            threadsLoaded = (boolean)savedInstanceState.getSerializable( THREADS_LOADED_KEY );
        }

        setContentView(R.layout.activity_main);

        PostsDBHelper dbHelper = new PostsDBHelper(this);
        mDB = dbHelper.getWritableDatabase();

        if ( !threadsLoaded ) {
            dbHelper.clearTable( mDB );
        }

        mRedditThreadsRV = findViewById(R.id.rv_reddit_threads);

        mRedditThreadsRV.setLayoutManager(new LinearLayoutManager(this));
        mRedditThreadsRV.setHasFixedSize(true);

        mRedditAdapter = new RedditAdapter(this,this);
        mRedditThreadsRV.setAdapter(mRedditAdapter);

        mLoadingProgressBar = findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = findViewById(R.id.tv_loading_error);

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

        ArrayAdapter<String> mACTVAdapter = new ArrayAdapter<String>( this, android.R.layout.select_dialog_item, RedditUtils.filterCats );
        final AutoCompleteTextView mFilterACTV = (AutoCompleteTextView) findViewById(R.id.actv_filter_box);
        mFilterACTV.setThreshold( 0 );
        mFilterACTV.setAdapter( mACTVAdapter );
        mFilterACTV.setHint( R.string.hint );
        mFilterACTV.setOnDismissListener( new AutoCompleteTextView.OnDismissListener() {
            public void onDismiss() {
                String filterText = mFilterACTV.getText().toString().toUpperCase();

                if ( TextUtils.isEmpty( filterText ) ) {
                    Cursor cursor = mDB.rawQuery(
                            "SELECT * FROM " + PostsContract.LoadedPosts.TABLE_NAME,
                            null
                    );

                    mRedditAdapter.updatePosts( cursor );

                }

            }
        });

        Button filterButton = (Button)findViewById(R.id.btn_search);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText = mFilterACTV.getText().toString().toUpperCase();
                String filterQuery = RedditUtils.parseThreadCategory( filterText );

                if ( !TextUtils.isEmpty( filterQuery ) ) {
                    Cursor cursor = mDB.rawQuery(
                            "SELECT * FROM " +
                                    PostsContract.LoadedPosts.TABLE_NAME +
                                    " WHERE " + PostsContract.LoadedPosts.COLUMN_POST_CATEGORY +
                                    "='" + filterQuery + "'",
                            null
                    );

                    mRedditAdapter.updatePosts( cursor );

                }

            }
        });

        pref.registerOnSharedPreferenceChangeListener(this);

        if ( !threadsLoaded ) {
            loadPosts(pref, true);
            //doRedditSearch( "learnprogramming+cpp+Python+javascript+golang", "new.json", null, "25", "new" );
        }

        getSupportLoaderManager().initLoader(POST_LOADER_ID, null, this);
    }

    public void loadPosts(SharedPreferences pref, boolean initialLoad) {
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


    private void doRedditSearch(String subreddit, String postType, String after, String postCount, String sortValue) {
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

    public void setUpTheme(SharedPreferences sharedPreferences) {
        SharedPreferences pref = sharedPreferences;
        String themeName = pref.getString(getString(R.string.pref_theme_key), getString(R.string.pref_theme_default_value));
        switch (themeName) {
            case "Blue":
                this.setTheme(R.style.Blue);
                break;
            case "Dark":
                setTheme(R.style.Dark);
                break;
            case "AppTheme":
                this.setTheme(R.style.AppTheme);
                break;
            default:
                break;
        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        onCreate(new Bundle());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mDB != null ) {
            outState.putSerializable( THREADS_LOADED_KEY, true );
        } else {
            outState.putSerializable( THREADS_LOADED_KEY, false );
        }
    }
}