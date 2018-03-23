package com.example.learntoprogram;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_item_detail);

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

    }

    public void openURLinBrowser(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.url));
        startActivity(browserIntent);
    }

}
