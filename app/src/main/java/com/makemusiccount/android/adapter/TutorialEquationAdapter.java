package com.makemusiccount.android.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.TutorialEquationList;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class TutorialEquationAdapter extends RecyclerView.Adapter<TutorialEquationAdapter.MyViewHolder> {

    private List<TutorialEquationList> tutorialEquationLists;

    private Context context;

    OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel;
        ImageView ivLabel;
        GifImageView GIF;

        MyViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivLabel = itemView.findViewById(R.id.ivLabel);
            GIF = itemView.findViewById(R.id.GIF);
        }
    }

    public TutorialEquationAdapter(Context context, List<TutorialEquationList> tutorialEquationLists) {
        this.context = context;
        this.tutorialEquationLists = tutorialEquationLists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        //holder.tvLabel.setText(tutorialEquationLists.get(listPosition).getLabel());
        if (tutorialEquationLists.get(listPosition).getEqn_type().equalsIgnoreCase("text")){
            holder.ivLabel.setVisibility(View.GONE);
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.GIF.setVisibility(View.GONE);
            holder.tvLabel.setText(tutorialEquationLists.get(listPosition).getLabel());
        } else {
            holder.tvLabel.setVisibility(View.GONE);
            Glide.with(context)
                    .load(tutorialEquationLists.get(listPosition).getEqn_image())
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivLabel.setImageDrawable(resource);
                            holder.GIF.setVisibility(View.GONE);
                            holder.ivLabel.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return tutorialEquationLists.size();
    }
}