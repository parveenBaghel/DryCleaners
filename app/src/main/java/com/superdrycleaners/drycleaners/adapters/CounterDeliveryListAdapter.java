package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.beans.CounterRequestBeans;

import java.util.List;

public class CounterDeliveryListAdapter extends RecyclerView.Adapter<CounterDeliveryListAdapter.ViewHolder> {
    private List<CounterRequestBeans> items;
    private Context context;

    public CounterDeliveryListAdapter(Context context, List<CounterRequestBeans> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counter_list_layout , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.delivery_date.setText(items.get(position).getDate());
        holder.delivery_location.setText(items.get(position).getLocation());
        holder.delivery_status.setText(items.get(position).getStatus());
        holder.delivery_time.setText(items.get(position).getTime());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView delivery_location , payment_option , delivery_date , delivery_status , delivery_id,delivery_time;
        public ViewHolder(View itemView) {
            super(itemView);
            delivery_location = itemView.findViewById(R.id.delivery_location);
            payment_option = itemView.findViewById(R.id.payment_option);
            delivery_date = itemView.findViewById(R.id.delivery_date);
            delivery_status = itemView.findViewById(R.id.delivery_status);
            delivery_time=itemView.findViewById(R.id.delivery_time);

        }
    }
}
