package com.example.learntoprogram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

// BEGIN DUMMY
public class RedditAdapter extends RecyclerView.Adapter<RedditAdapter.RedditThreadViewHolder> {
    private ArrayList<String> mRedditThreads;
    private ArrayList<String> mDetailRedditThreads;
    private OnItemClickListener mOnItemClickListener;

    public RedditAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void updateRedditData(ArrayList<String> redditThreads, ArrayList<String> detailRedditThreads) {
        mRedditThreads = redditThreads;
        mDetailRedditThreads = detailRedditThreads;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRedditThreads != null && mDetailRedditThreads !=null) {
            return Math.min(mRedditThreads.size(), mDetailRedditThreads.size());
        } else {
            return 0;
        }
    }

    @Override
    public RedditThreadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result_item,parent,false);
        return new RedditThreadViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RedditThreadViewHolder holder, int position) {
        holder.bind(mRedditThreads.get(position));
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
                    String detailRedditThreads = mDetailRedditThreads.get(getAdapterPosition());
                    mOnItemClickListener.onItemClick(detailRedditThreads);
                }
            });
        }

        public void bind(String dummy) {
            mRedditThreadTV.setText(dummy);
        }
    }

}
// END DUMMY

// BEGIN ACTUAL CONTENT... ONCE API IS HOOKED UP
/*     public class RedditAdapter extends RecyclerView.Adapter<RedditAdapter.SearchResultViewHolder> {
    private ArrayList<RedditUtils.SearchResult> mSearchResultsList;

   public void updateSearchResults(ArrayList<RedditUtils.SearchResult> searchResultsList) {
        mSearchResultsList = searchResultsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSearchResultsList != null) {
            return mSearchResultsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        holder.bind(mSearchResultsList.get(position));
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView mSearchResultTV;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mSearchResultTV = (TextView)itemView.findViewById(R.id.tv_search_result);
        }

        public void bind(RedditUtils.SearchResult searchResult) {
            mSearchResultTV.setText(searchResult.threadName);
        }
    }*/
