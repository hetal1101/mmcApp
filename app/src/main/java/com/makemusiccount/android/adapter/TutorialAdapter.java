package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.TutorialList;

import java.util.List;

/**
 * Created by Welcome on 14-02-2018.
 */

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.MyViewHolder> {

    private List<TutorialList> tutorialLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvButton, tvTitle;
        ImageView ivImage;
        CardView cvMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            cvMain = itemView.findViewById(R.id.cvMain);
        }
    }

    public TutorialAdapter(Context context, List<TutorialList> data) {
        this.context = context;
        this.tutorialLists = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(tutorialLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);

        holder.tvButton.setText("Play");

        if (tutorialLists.get(listPosition).getStatus().equals("Inactive")) {
            holder.tvButton.setTextColor(Color.GRAY);
            holder.tvButton.setText("Locked");
        }

        holder.tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorialLists.get(listPosition).getStatus().equals("Inactive")) {
                    mOnItemClickListener.onItemClick(listPosition, view, 4);
                } else {
                    mOnItemClickListener.onItemClick(listPosition, view, 3);
                }
            }
        });

        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorialLists.get(listPosition).getStatus().equals("Inactive")) {
                    mOnItemClickListener.onItemClick(listPosition, view, 4);
                } else {
                    mOnItemClickListener.onItemClick(listPosition, view, 3);
                }
            }
        });

        holder.tvTitle.setText(tutorialLists.get(listPosition).getName());
    }

    @Override
    public int getItemCount() {
        return tutorialLists.size();
    }
}