package com.example.learntoprogram;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learntoprogram.RedditUtils;

/**
 * Created by Dilon_000 on 3/21/2018.
 */

public class RedditDetailActivity extends AppCompatActivity {

    private TextView mTVtitle;
    private TextView mTVuser;
    private TextView mTVsubreddit;
    private TextView mTVurl;
    private TextView mTVcomments;
    private TextView mTVupvotes;
    private TextView mTVdownvotes;

    private SQLiteDatabase mDB;

    private RedditUtils.Post mPost;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_item_detail);

        mContext = getApplicationContext();
        mTVtitle = findViewById(R.id.tv_detail_title);
        mTVuser = findViewById(R.id.tv_detail_user);
        mTVsubreddit = findViewById(R.id.tv_detail_subreddit);
        mTVurl = findViewById(R.id.tv_detail_url);
        mTVcomments = findViewById(R.id.tv_detail_comments);
        mTVupvotes = findViewById(R.id.tv_detail_upvotes);
        mTVdownvotes = findViewById(R.id.tv_detail_downvotes);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RedditUtils.EXTRA_POST)) {
            mPost = (RedditUtils.Post) intent.getSerializableExtra(RedditUtils.EXTRA_POST);
            mTVtitle.setText(mPost.title);
            mTVuser.setText(mPost.user);
            mTVsubreddit.setText(mPost.subreddit);
            mTVurl.setText(mPost.url);
            mTVcomments.setText(String.valueOf(mPost.comments));
            mTVupvotes.setText(String.valueOf(mPost.upvotes));
            mTVdownvotes.setText(String.valueOf(mPost.downvotes));
        }
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext);
        String textColor = sharedPreferences.getString(
                mContext.getString(R.string.pref_text_color_key),
                mContext.getString(R.string.pref_text_color_default_value)
        );

        if (textColor.equals("#000000")) {
            mTVtitle.setTextColor(ContextCompat.getColor(mContext, R.color.defaultTextColor));
            mTVsubreddit.setTextColor(ContextCompat.getColor(mContext, R.color.defaultTextColor));

        }if (textColor.equals("#00FF00")){
            mTVtitle.setTextColor(ContextCompat.getColor(mContext, R.color.Lime));
            mTVsubreddit.setTextColor(ContextCompat.getColor(mContext, R.color.Lime));

        }if (textColor.equals("#000080")) {
            mTVtitle.setTextColor(ContextCompat.getColor(mContext, R.color.Navy));
            mTVsubreddit.setTextColor(ContextCompat.getColor(mContext, R.color.Navy));
        }

    }

    public void openURLinBrowser(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.url));
        startActivity(browserIntent);
    }

}
