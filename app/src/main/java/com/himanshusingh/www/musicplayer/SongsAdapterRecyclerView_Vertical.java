package com.himanshusingh.www.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by himanshu on 26/2/19.
 */

public class SongsAdapterRecyclerView_Vertical extends RecyclerView.Adapter<SongsAdapterRecyclerView_Vertical.SongViewHolder> {

    OnSatsangClickListenerMore mOnSatsangClickListenerMore;
    private ArrayList<String> data;
    private String cover_image_url;
    public SongsAdapterRecyclerView_Vertical(ArrayList<String> data, String cover_image_url, OnSatsangClickListenerMore onSatsangClickListenerMore)
    {
        this.data = data;
        this.cover_image_url = cover_image_url;
        this.mOnSatsangClickListenerMore = onSatsangClickListenerMore;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.songs_item_recycler_view_vertical, parent, false);
        return new SongViewHolder(view, mOnSatsangClickListenerMore);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        String title = data.get(position);
        holder.textView.setText(title);
        Picasso.get().load(cover_image_url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        OnSatsangClickListenerMore onSatsangClickListenerMore;
        public SongViewHolder(View itemView, OnSatsangClickListenerMore onSatsangClickListenerMore) {
            super(itemView);
            imageView = itemView.findViewById(R.id.idImgSongRV_Vertical);
            textView = itemView.findViewById(R.id.idDescSongRV_Vertical);
            this.onSatsangClickListenerMore = onSatsangClickListenerMore;

            imageView.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v==imageView)
                onSatsangClickListenerMore.onSatsangImageClickMore(getAdapterPosition());
            else if(v==textView)
                onSatsangClickListenerMore.onSatsangTitleClickMore(getAdapterPosition());
        }
    }

    public interface OnSatsangClickListenerMore
    {
        void onSatsangTitleClickMore(int position);
        void onSatsangImageClickMore(int position);
    }
}
