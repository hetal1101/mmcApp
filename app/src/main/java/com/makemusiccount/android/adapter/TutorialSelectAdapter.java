package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.TutorialSelectionActivity;
import com.makemusiccount.android.model.TutorialSelectList;
import com.makemusiccount.android.util.AppConstant;

import java.util.List;

/**
 * Created by Welcome on 14-02-2018.
 */

public class TutorialSelectAdapter extends RecyclerView.Adapter<TutorialSelectAdapter.MyViewHolder> {

    private List<TutorialSelectList> tutorialLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;


    int selectedItem = 1;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivImage;
        CardView cvMain;
        RelativeLayout rlMain;


        MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            cvMain = itemView.findViewById(R.id.cvMain);
            rlMain = itemView.findViewById(R.id.rlMain);
        }
    }

    public TutorialSelectAdapter(Context context, List<TutorialSelectList> data) {
        this.context = context;
        this.tutorialLists = data;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tutorial_select_list, parent, false);
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

        /*if (listPosition == 0) {
            TutorialSelectionActivity.ID = tutorialLists.get(listPosition).getID();
            TutorialSelectionActivity.Name = tutorialLists.get(listPosition).getName();
            selectedItem = listPosition;
        }*/

        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TutorialSelectionActivity.ID = tutorialLists.get(listPosition).getID();
                TutorialSelectionActivity.Name = tutorialLists.get(listPosition).getName();
                AppConstant.songName=tutorialLists.get(listPosition).getName();
                AppConstant.songId=tutorialLists.get(listPosition).getID();

                selectedItem = listPosition;
                notifyDataSetChanged();
            }
        });

        holder.tvTitle.setText(tutorialLists.get(listPosition).getName());

        if (selectedItem == listPosition) {
            holder.rlMain.setBackground(context.getDrawable(R.drawable.tutorial_select));
            holder.rlMain.setPadding(5,5,5,5);
        } else {
            holder.rlMain.setBackground(context.getDrawable(R.drawable.tutorial_unselect));
            holder.rlMain.setPadding(0,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return tutorialLists.size();
    }
}