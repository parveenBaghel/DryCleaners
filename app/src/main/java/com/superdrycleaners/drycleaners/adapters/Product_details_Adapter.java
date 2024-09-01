package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.activities.AdapterCallback;
import com.superdrycleaners.drycleaners.activities.Product_Details;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;

import java.util.List;

public class Product_details_Adapter extends RecyclerView.Adapter<Product_details_Adapter.ViewHolder> {
    private List<RateModel> list;
    private Context mcontext;
    private Product_Details activity;
    private AppCompatTextView addCunt;
    private AdapterCallback adapterCallback;

    public Product_details_Adapter(Context context, List<RateModel> list, AdapterCallback adapterCallback) {
        this.mcontext = context;
        this.list = list;
        this.adapterCallback = adapterCallback;

    }

    @NonNull
    @Override
    public Product_details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Product_details_Adapter.ViewHolder holder, int position) {

        RateModel model = list.get(position);
        holder.job.setText(list.get(position).getJob());
        holder.minRate.setText(list.get(position).getJob_minRate());
        holder.maxRate.setText(list.get(position).getJob_maxRate());
        holder.remarks.setText(list.get(position).getJob_remark());
        holder.date_text.setText(list.get(position).getJob_date());
        holder.itemView.setTag(model);
        holder.add_cart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName, job, minRate, maxRate, remarks, date_text, add_cart;

        public ViewHolder(final View itemView) {
            super(itemView);

            job = itemView.findViewById(R.id.job);
            minRate = itemView.findViewById(R.id.min_rate);
            maxRate = itemView.findViewById(R.id.max_rate);
            remarks = itemView.findViewById(R.id.remark);
            date_text = itemView.findViewById(R.id.date);
            add_cart = itemView.findViewById(R.id.add_cart);

        }
    }
}
