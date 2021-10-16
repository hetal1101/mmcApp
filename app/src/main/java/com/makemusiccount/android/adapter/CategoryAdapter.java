package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.CategoryList;

import java.util.List;

/*
 * Created by Welcome on 25-01-2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryList> categoryLists;

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
        LinearLayout llMain;
        ProgressBar activeProgress;
        CardView cvback;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            activeProgress = itemView.findViewById(R.id.activeProgress);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            llMain = itemView.findViewById(R.id.llMain);


        }
    }

    public CategoryAdapter(Context context, List<CategoryList> categoryLists) {
        this.context = context;
        this.categoryLists = categoryLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        Glide.with(context)
                .load(categoryLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);

        holder.tvTitle.setText(categoryLists.get(listPosition).getName());

        holder.tvButton.setOnClickListener(view -> {
            if (categoryLists.get(listPosition).getSub_cats().equals("Yes")) {
                mOnItemClickListener.onItemClick(listPosition, view, 2);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });

        holder.llMain.setOnClickListener(view -> {
            if (categoryLists.get(listPosition).getSub_cats().equals("Yes")) {
                mOnItemClickListener.onItemClick(listPosition, view, 2);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }
        });

        holder.tvButton.setText(categoryLists.get(listPosition).getShort_desc());
        if(categoryLists.get(listPosition).getType().equalsIgnoreCase("tutorial"))
        {
            holder.activeProgress.setVisibility(View.GONE);
        }
        else
        {
            holder.activeProgress.setVisibility(View.VISIBLE);
            holder.activeProgress.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_br));
            holder.activeProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor(""+categoryLists.get(listPosition).getBar_color())));
            holder.activeProgress.setProgress((int)((Integer.parseInt(categoryLists.get(listPosition).getPercentage()))));
        }

        }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }
}