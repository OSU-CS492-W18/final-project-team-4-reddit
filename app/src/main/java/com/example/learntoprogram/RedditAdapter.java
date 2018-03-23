package com.example.learntoprogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.database.Cursor;

import static android.provider.Settings.Global.getString;


public class RedditAdapter extends RecyclerView.Adapter<RedditAdapter.RedditThreadViewHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ArrayList<RedditUtils.Post> mPosts;


    private OnItemClickListener mOnItemClickListener;

    Cursor mThreadsCursor;

    private Context mContext;

    public RedditAdapter(OnItemClickListener onItemClickListener, Context context) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    public void updatePosts(Cursor cursor) {
        mThreadsCursor = cursor;
        notifyDataSetChanged();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
                    mThreadsCursor.moveToPosition( getAdapterPosition() );
                    RedditUtils.Post detailThread = RedditUtils.parseRowPost( mThreadsCursor );
                    mOnItemClickListener.onItemClick( detailThread );
                }
            });

        }

        @SuppressLint("ResourceAsColor")
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

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String textColor = sharedPreferences.getString(
                        mContext.getString(R.string.pref_text_color_key),
                        mContext.getString(R.string.pref_text_color_default_value)
                );

                if (textColor.equals("#000000")) {
                    mThreadTitleTV.setTextColor(ContextCompat.getColor(mContext, R.color.defaultTextColor));
                }if (textColor.equals("#00FF00")){
                    mThreadTitleTV.setTextColor(ContextCompat.getColor(mContext, R.color.Lime));
                }if (textColor.equals("#000080")) {
                    mThreadTitleTV.setTextColor(ContextCompat.getColor(mContext, R.color.Navy));
                }
            }

        }
    }

}

