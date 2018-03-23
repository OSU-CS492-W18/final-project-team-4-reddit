package com.example.learntoprogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

/**
 * Created by vpiscitello on 3/17/18.
 */

public class PostsDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Posts.db";
    private static int DATABASE_VERSION = 1;

    public PostsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void clearTable(SQLiteDatabase db) {
        final String SQL_DELETE_POSTS_TABLE =
                "DELETE FROM " + PostsContract.LoadedPosts.TABLE_NAME + " ";
        db.execSQL( SQL_DELETE_POSTS_TABLE );
    }

    public long storePosts(SQLiteDatabase db, ArrayList<RedditUtils.Post> posts) {
        long status = 0;

        for (int idx = 0; idx < posts.size(); idx ++) {
            status = addPostToDB( db, posts.get( idx ) );
        }

        return 0;
    }

    public long addPostToDB(SQLiteDatabase db, RedditUtils.Post post) {
        SQLiteStatement SQL_INSERT_THREAD = db.compileStatement(
            "INSERT INTO " + PostsContract.LoadedPosts.TABLE_NAME + "(" +
                    PostsContract.LoadedPosts.COLUMN_POST_TITLE + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_USER + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_SUBREDDIT + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_CATEGORY + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_URL + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_ID36 + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_COMMENT_COUNT + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_UPVOTES + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_DOWNVOTES + ", " +
                    PostsContract.LoadedPosts.COLUMN_POST_TIMESTAMP +
                    ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );

        SQL_INSERT_THREAD.bindString( 1, post.title );
        SQL_INSERT_THREAD.bindString( 2, post.user );
        SQL_INSERT_THREAD.bindString( 3, post.subreddit );
        SQL_INSERT_THREAD.bindString( 4, post.category );
        SQL_INSERT_THREAD.bindString( 5, post.url );
        SQL_INSERT_THREAD.bindString( 6, post.id36 );
        SQL_INSERT_THREAD.bindLong( 7, post.comments );
        SQL_INSERT_THREAD.bindLong( 8, post.upvotes );
        SQL_INSERT_THREAD.bindLong( 9, post.downvotes );
        SQL_INSERT_THREAD.bindLong( 10, post.timestamp );

        SQL_INSERT_THREAD.executeInsert();

        return 0;
    }

    public ArrayList<String> getAllPosts(SQLiteDatabase db) {
        Cursor cursor = db.query(
                PostsContract.LoadedPosts.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> postsList = new ArrayList<>();

        System.out.println("LOCATIONS: ");

        while (cursor.moveToNext()) {
            String post = cursor.getString( cursor.getColumnIndex(PostsContract.LoadedPosts.COLUMN_POST_TITLE) );
            postsList.add( post );
            System.out.println( post );
        }

        cursor.close();
        return postsList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_POSTS_TABLE =
                "CREATE TABLE " + PostsContract.LoadedPosts.TABLE_NAME + "(" +
                        PostsContract.LoadedPosts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PostsContract.LoadedPosts.COLUMN_POST_TITLE + " TEXT NOT NULL, " +
                        PostsContract.LoadedPosts.COLUMN_POST_USER + " TEXT NOT NULL, " +
                        PostsContract.LoadedPosts.COLUMN_POST_SUBREDDIT + " TEXT NOT NULL, " +
                        PostsContract.LoadedPosts.COLUMN_POST_CATEGORY + " TEXT NOT NULL, " +
                        PostsContract.LoadedPosts.COLUMN_POST_URL + " TEXT NOT NULL, " +
                        PostsContract.LoadedPosts.COLUMN_POST_ID36 + " TEXT, " +
                        PostsContract.LoadedPosts.COLUMN_POST_COMMENT_COUNT + " INTEGER, " +
                        PostsContract.LoadedPosts.COLUMN_POST_UPVOTES + " INTEGER, " +
                        PostsContract.LoadedPosts.COLUMN_POST_DOWNVOTES + " INTEGER, " +
                        PostsContract.LoadedPosts.COLUMN_POST_TIMESTAMP + " INTEGER " +
                        ");";
        db.execSQL( SQL_CREATE_POSTS_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PostsContract.LoadedPosts.TABLE_NAME + ";");
        onCreate(db);
    }

}
