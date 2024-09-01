package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.beans.RequestBeans;

import java.util.List;

public class DeliveryListAdapter extends RecyclerView.Adapter<DeliveryListAdapter.ViewHolder> {
  private List<RequestBeans> items;
  private Context context;

  public DeliveryListAdapter(Context context, List<RequestBeans> items) {
    this.context = context;
    this.items = items;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_list , parent , false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    holder.delivery_id.setText(items.get(position).getId());
    holder.delivery_location.setText(items.get(position).getAddress());
    holder.payment_option.setText(items.get(position).getPayment_option());
    holder.delivery_date.setText(items.get(position).getDate());
    holder.delivery_status.setText(items.get(position).getStatus());

  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  protected class ViewHolder extends RecyclerView.ViewHolder {
    public AppCompatTextView delivery_location , payment_option , delivery_date , delivery_status , delivery_id;
    public ViewHolder(View itemView) {
      super(itemView);
      delivery_location = itemView.findViewById(R.id.delivery_location);
      payment_option = itemView.findViewById(R.id.payment_option);
      delivery_date = itemView.findViewById(R.id.delivery_date);
      delivery_status = itemView.findViewById(R.id.delivery_status);
      delivery_id = itemView.findViewById(R.id.delivery_id);
    }
  }
}
