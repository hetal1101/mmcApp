package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.PianoActivity;
import com.makemusiccount.android.model.SongList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/*
 * Created by welcome on 24-01-2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private List<SongList> songLists;

    private Context context;

    private OnItemClickListener mOnItemClickListener;

    private MaterialShowcaseSequence sequence;

    public void startAnimation() {
        sequence.start();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private boolean isFirst = true;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvButton, tvTitle,tvButton1,tvDolesson,tvPlayed,tvArtist,tvQuiz;
        ImageView ivImage, ivPlayed;
        LinearLayout llPlaySong;
        MaterialRippleLayout a1;

        CardView cvMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvQuiz = itemView.findViewById(R.id.tvQuiz);
            tvButton1 = itemView.findViewById(R.id.tvButton1);
            llPlaySong = itemView.findViewById(R.id.llPlaySong);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDolesson = itemView.findViewById(R.id.tvDolesson);
            ivPlayed = itemView.findViewById(R.id.ivPlayed);
            tvPlayed = itemView.findViewById(R.id.tvPlayed);
            cvMain = itemView.findViewById(R.id.cvMain);
            a1 = itemView.findViewById(R.id.a1);
        }
    }

    public SongAdapter(Context context, List<SongList> data, MaterialShowcaseSequence sequence) {
        this.context = context;
        this.songLists = data;
        this.sequence = sequence;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_list, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        if (holder.ivImage.getDrawable() == null) {
            Glide.with(context)
                    .load(songLists.get(listPosition).getImage())
                    .into(holder.ivImage);
        }


        holder.tvPlayed.setText(songLists.get(listPosition).getSong_level()+"");
        holder.tvArtist.setText(songLists.get(listPosition).getArtist()+"");
        holder.tvQuiz.setText(songLists.get(listPosition).getSong_quiz()+"");
        if(songLists.get(listPosition).getSong_quiz().equalsIgnoreCase(""))
        {
            holder.tvQuiz.setVisibility(View.GONE);
        }
        else
        {
            holder.tvQuiz.setVisibility(View.VISIBLE);
        }

        // holder.tvButton.setText("Play Now");

        if (songLists.get(listPosition).getStatus().equals("Inactive")) {
            holder.tvButton.setTextColor(Color.GRAY);
            holder.tvButton.setText("Locked");
        }


        if(songLists.get(listPosition).getPlay_songs().equalsIgnoreCase("NO"))
        {
            holder.tvDolesson.setText("Complete Lesson");
            holder.llPlaySong.setVisibility(View.VISIBLE);
            holder.tvButton.setVisibility(View.GONE);
            holder.tvButton1.setVisibility(View.GONE);
        }
        else if(songLists.get(listPosition).getPlay_songs().equalsIgnoreCase("Yes")&&songLists.get(listPosition).getPlay_autoplay().equalsIgnoreCase("Yes")) {
            holder.llPlaySong.setVisibility(View.GONE);
            holder.tvButton.setVisibility(View.VISIBLE);
            holder.tvButton1.setVisibility(View.VISIBLE);

        }
        else if(songLists.get(listPosition).getPlay_songs().equalsIgnoreCase("Yes")&&songLists.get(listPosition).getPlay_autoplay().equalsIgnoreCase("NO")) {

            holder.llPlaySong.setVisibility(View.VISIBLE);

            holder.tvDolesson.setText("Do Lesson Again");
            holder.tvButton.setVisibility(View.GONE);
            holder.tvButton1.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDolesson.setText("Complete Lesson");
            holder.llPlaySong.setVisibility(View.VISIBLE);
            holder.tvButton.setVisibility(View.GONE);
            holder.tvButton1.setVisibility(View.GONE);
        }


        if (isFirst) {
            switch (listPosition) {
                case 0:
                    sequence.addSequenceItem(
                            new MaterialShowcaseView.Builder((Activity) context)
                                    .setTarget(holder.tvTitle)
                                    .withRectangleShape()
                                    .setShapePadding(50)
                                    .setContentText("Click on this song to start and learn song. For each right ans of question you will get 10 point and for wrong, -1 point.")
                                    .setDismissText("Exit")
                                    .withRectangleShape()
                                    .setShapePadding(10)
                                    .setDismissOnTouch(true)
                                    .build()
                    );

                    /*sequence.addSequenceItem(holder.tvTitle,
                            "“Click on this song to start and learn song. For each right ans of question you will get 10 point and for wrong, -1 point.”", "Exit");
*/
                    isFirst = false;
                    break;
            }
        }

        holder.tvButton.setOnClickListener(view -> {
              Util.getKeyTheme(context);
            Intent intent = new Intent(context, PianoActivity.class);
            intent.putExtra("screen", "playsong");
            intent.putExtra("song_name", songLists.get(listPosition).getName());
            intent.putExtra("song_id", songLists.get(listPosition).getID());
            //   intent.putExtra("tag","playsong");
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(0, 0);

            /*if (songLists.get(listPosition).getStatus().equals("Inactive")) {
                mOnItemClickListener.onItemClick(listPosition, view, 4);
            } else {
                mOnItemClickListener.onItemClick(listPosition, view, 3);
            }*/
        });

        holder.cvMain.setOnClickListener(view -> {
            if(songLists.get(listPosition).getSong_level().equalsIgnoreCase("free"))
            {
                if (songLists.get(listPosition).getStatus().equalsIgnoreCase("Inactive")) {
                    mOnItemClickListener.onItemClick(listPosition, view, 4);
                } else {
                    mOnItemClickListener.onItemClick(listPosition, view, 3);
                }}
            else
            {
                mOnItemClickListener.onItemClick(listPosition, view, 5);
            }
        });
        holder.a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cvMain.performClick();
            }
        });

        holder.tvTitle.setText(songLists.get(listPosition).getName());
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }
}