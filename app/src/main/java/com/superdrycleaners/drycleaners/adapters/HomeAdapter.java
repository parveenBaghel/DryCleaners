package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.beans.ModelClass;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<ModelClass> items;
    private Context context;

    public HomeAdapter(Context context, List<ModelClass> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(items.get(position).getName());
        Glide.with(context).load(items.get(position).getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public AppCompatTextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
