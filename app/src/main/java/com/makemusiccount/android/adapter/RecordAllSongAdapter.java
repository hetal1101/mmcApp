package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.AllSong;

import java.util.List;

/*
 * Created by Welcome on 25-01-2018.
 */

public class RecordAllSongAdapter extends RecyclerView.Adapter<RecordAllSongAdapter.MyViewHolder> {

    private List<AllSong> allSongs;

    private Context context;

    OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvName,tvStatus;
        ImageView ivImage;
        LinearLayout llMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivImage = itemView.findViewById(R.id.ivImage);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public RecordAllSongAdapter(Context context, List<AllSong> allSongs) {
        this.context = context;
        this.allSongs = allSongs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_record_song_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(allSongs.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);

       holder.tvName.setText(allSongs.get(listPosition).getName());

       holder.tvCategory.setText(allSongs.get(listPosition).getCategory());

       holder.tvStatus.setText(allSongs.get(listPosition).getStatus());

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(listPosition, view, 2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }
}
