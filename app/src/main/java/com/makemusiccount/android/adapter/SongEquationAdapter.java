package com.makemusiccount.android.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
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
import com.makemusiccount.android.model.SongEquationList;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Welcome on 26-01-2018.
 */

public class SongEquationAdapter extends RecyclerView.Adapter<SongEquationAdapter.MyViewHolder> {

    private List<SongEquationList> songEquationLists;

    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
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

    public SongEquationAdapter(Context context, List<SongEquationList> songEquationLists) {
        this.context = context;
        this.songEquationLists = songEquationLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {
        //holder.tvLabel.setText(Html.fromHtml(songEquationLists.get(listPosition).getLabel()));
        //holder.tvLabel.setText(songEquationLists.get(listPosition).getLabel());
        if (songEquationLists.get(listPosition).getEqn_type().equalsIgnoreCase("text")) {
            holder.ivLabel.setVisibility(View.GONE);
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.GIF.setVisibility(View.GONE);
            holder.tvLabel.setText(songEquationLists.get(listPosition).getLabel());
        } else {

            holder.tvLabel.setVisibility(View.GONE);
            Glide.with(context)
                    .load(songEquationLists.get(listPosition).getEqn_image())
                    /*.listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.ivLabel.setImageDrawable(resource);
                            holder.GIF.setVisibility(View.GONE);
                            holder.ivLabel.setVisibility(View.VISIBLE);
                            return false;
                        }
                    });*/
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
        return songEquationLists.size();
    }
}
