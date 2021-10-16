package com.makemusiccount.android.adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.ProgressModel;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {

    Activity context;
    private List<ProgressModel> listData;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProgressAdapter(Activity context, List<ProgressModel> bean) {
        this.listData = bean;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progress, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ProgressModel bean = listData.get(position);
        holder.tvTitle.setText(bean.getLabel() + ":-");

        if (holder.tvPer.getText().toString().equalsIgnoreCase(""))
        {
            //holder.tvPer.setText("" + bean.getPer() + " % ");
            ValueAnimator animator = ValueAnimator.ofInt(0, Integer.parseInt(bean.getPer()));
            animator.setDuration(1500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.tvPer.setText(animation.getAnimatedValue().toString() + " %");
                }
            });
            animator.start();

        } else {

        }

        holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        holder.Progress.setProgress(Integer.parseInt(bean.getPer()));
        if (bean.getColour().equalsIgnoreCase("DANGER")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.DANGER));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.DANGER));

        } else if (bean.getColour().equalsIgnoreCase("INFO")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.INFO);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.INFO));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.INFO));

        } else if (bean.getColour().equalsIgnoreCase("PRIMARY")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.PRIMARY));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.PRIMARY));

        } else if (bean.getColour().equalsIgnoreCase("SUCCESS")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.SUCCESS));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.SUCCESS));

        } else if (bean.getColour().equalsIgnoreCase("WARNING")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.WARNING));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.WARNING));

        } else if (bean.getColour().equalsIgnoreCase("SECONDARY")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.Tex));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.Tex));

        } else if (bean.getColour().equalsIgnoreCase("REGULAR")) {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.Tex));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.Tex));

        } else {
            holder.Progress.setBootstrapBrand(DefaultBootstrapBrand.INFO);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.INFO));
            holder.tvPer.setTextColor(context.getResources().getColor(R.color.INFO));
        }



    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPer;
        BootstrapProgressBar Progress;

        public ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvPer = v.findViewById(R.id.tvPer);
            Progress = v.findViewById(R.id.Progress);
        }
    }
}