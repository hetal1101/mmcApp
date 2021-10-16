package com.makemusiccount.android.adapter;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.model.Nodes;

import java.util.ArrayList;
import java.util.List;

public class NodesAdapter extends RecyclerView.Adapter<NodesAdapter.ViewHolder> {

List<Nodes> nodesList=new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nodes;

        ViewHolder(View v) {
            super(v);
            nodes=v.findViewById(R.id.nodes);
        }
    }

    public NodesAdapter(Activity activity, List<Nodes> items) {
        nodesList=items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_nodes, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.nodes.setText(nodesList.get(position).getValue()+"");
    }

    @Override
    public int getItemCount() {
        return nodesList.size();
    }
}