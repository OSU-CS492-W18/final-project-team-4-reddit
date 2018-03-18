package com.example.learntoprogram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
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
    private OnItemClickListener mOnItemClickListener;

    Cursor mThreadsCursor;

    public RedditAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void updatePosts(Cursor cursor) {
        mThreadsCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if ( mThreadsCursor != null ) {
            return Math.max(mThreadsCursor.getCount(), 0);
        } else {
            return 0;
        }
    }

    @Override
    public RedditThreadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
        View view = inflater.inflate(R.layout.search_result_item,parent,false);
        return new RedditThreadViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RedditThreadViewHolder holder, int position) {
        mThreadsCursor.moveToPosition( position );
        holder.bind( mThreadsCursor );
    }

    public interface OnItemClickListener {
        void onItemClick(String detailedReddit);
    }

    class RedditThreadViewHolder extends RecyclerView.ViewHolder {
        private TextView mRedditThreadTV;

        public RedditThreadViewHolder(View itemView) {
            super(itemView);
            mRedditThreadTV = (TextView)itemView.findViewById(R.id.tv_reddit_thread_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String detailRedditThreads = mDetailRedditThreads.get(getAdapterPosition());
//                    mOnItemClickListener.onItemClick(detailRedditThreads);
                }
            });
        }

        public void bind(Cursor cursor) {

            if ( cursor != null ) {
                mRedditThreadTV.setText( cursor.getString(
                        cursor.getColumnIndexOrThrow( PostsContract.LoadedPosts.COLUMN_POST_TITLE )
                ));
            }

        }
    }

}

