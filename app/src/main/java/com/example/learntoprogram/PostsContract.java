package com.example.learntoprogram;

import android.provider.BaseColumns;

/**
 * Created by vpiscitello on 3/17/18.
 */

public class PostsContract {
    private PostsContract() {}
    public static class LoadedPosts implements BaseColumns {
        public static final String TABLE_NAME = "LoadedPosts";
        public static final String COLUMN_POST_TITLE = "Title";
        public static final String COLUMN_POST_USER = "User";
        public static final String COLUMN_POST_SUBREDDIT = "Subreddit";
        public static final String COLUMN_POST_CATEGORY = "Category";
        public static final String COLUMN_POST_URL = "URL";
        public static final String COLUMN_POST_IMG = "Image";
        public static final String COLUMN_POST_COMMENT_COUNT = "Comments";
        public static final String COLUMN_POST_UPVOTES = "Upvotes";
        public static final String COLUMN_POST_DOWNVOTES = "Downvotes";
        public static final String COLUMN_POST_TIMESTAMP = "Timestamp";
    }
}
