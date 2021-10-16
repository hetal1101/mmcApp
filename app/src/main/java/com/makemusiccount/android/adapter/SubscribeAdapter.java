package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.PackageList;

import java.util.List;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder> {

    private List<PackageList> packageLists;

    private Context context;

    private int selectedPosition = 0;

    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;

        LinearLayout llMain,llData;

        RadioButton rbSelect;

        ViewHolder(View v) {
            super(v);

            tvPrice = v.findViewById(R.id.tvPrice);
            llMain = v.findViewById(R.id.llMain);
            llData = v.findViewById(R.id.llData);
            rbSelect = v.findViewById(R.id.rbSelect);
            tvName = v.findViewById(R.id.tvName);
        }
    }

    public SubscribeAdapter(Activity activity, List<PackageList> items) {
        context = activity;
        packageLists = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subscriber, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.setIsRecyclable(false);

        final PackageList packageList = packageLists.get(position);

        holder.tvName.setText(Html.fromHtml("<b>"+packageList.getName()+"</b>  ")+packageList.getPlan_price_info());

        if (position == selectedPosition) {
            holder.rbSelect.setChecked(true);
            holder.llData.setBackground(context.getResources().getDrawable(R.drawable.btn_border_package_select));
        } else {
            holder.rbSelect.setChecked(false);
            holder.llData.setBackground(context.getResources().getDrawable(R.drawable.btn_border_package_unselect));
        }
        holder.tvPrice.setText(packageList.getPackage_desc());
        holder.llData.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return packageLists.size();
    }
}