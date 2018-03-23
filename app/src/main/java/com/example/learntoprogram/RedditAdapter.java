package com.example.learntoprogram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.database.Cursor;


public class RedditAdapter extends RecyclerView.Adapter<RedditAdapter.RedditThreadViewHolder> {

    private ArrayList<RedditUtils.Post> mPosts;

    private OnItemClickListener mOnItemClickListener;

    Cursor mThreadsCursor;

    public RedditAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void updatePosts(Cursor cursor) {
        mThreadsCursor = cursor;
        notifyDataSetChanged();
    }

    public void updatePostsList(ArrayList<RedditUtils.Post> posts){
        mPosts = posts;
    }

    public Cursor getLastPost( int position ) {
        mThreadsCursor.moveToPosition( position );
        return mThreadsCursor;
    }

    @Override
    public int getItemCount() {
        if ( mThreadsCursor != null ) {
            return Math.max( mThreadsCursor.getCount(), 0 );
        } else {
            return 0;
        }
    }

    @Override
    public RedditThreadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
        View view = inflater.inflate(R.layout.post_item,parent,false);
        return new RedditThreadViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RedditThreadViewHolder holder, int position) {
        mThreadsCursor.moveToPosition( position );
        holder.bind( mThreadsCursor );
    }

    public interface OnItemClickListener {
        void onItemClick(RedditUtils.Post detailedReddit);
    }

    class RedditThreadViewHolder extends RecyclerView.ViewHolder {
        private TextView mThreadTitleTV;
        private TextView mThreadAuthorTV;
        private TextView mThreadUpvotesTV;

        public RedditThreadViewHolder(View itemView) {
            super(itemView);
            mThreadTitleTV = (TextView)itemView.findViewById(R.id.tv_thread_title);
            mThreadAuthorTV = (TextView)itemView.findViewById(R.id.tv_thread_author);
            mThreadUpvotesTV = (TextView)itemView.findViewById(R.id.tv_thread_upvotes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RedditUtils.Post detailPost = mPosts.get(getAdapterPosition());
                    mOnItemClickListener.onItemClick(detailPost);
                }
            });

        }

        public void bind(Cursor cursor) {

            if ( cursor != null ) {
                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PostsContract.LoadedPosts.COLUMN_POST_TITLE
                        )
                );

                String author = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PostsContract.LoadedPosts.COLUMN_POST_USER
                        )
                );

                Long upvotes = cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                PostsContract.LoadedPosts.COLUMN_POST_UPVOTES
                        )
                );

                mThreadTitleTV.setText( Html.fromHtml( title, Html.FROM_HTML_MODE_COMPACT ) );
                mThreadAuthorTV.setText( Html.fromHtml( author, Html.FROM_HTML_MODE_COMPACT ) );
                mThreadUpvotesTV.setText( upvotes.toString() );

            }

        }
    }

}

