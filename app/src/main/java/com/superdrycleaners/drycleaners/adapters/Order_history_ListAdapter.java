package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.beans.RequestBeans;

import java.util.List;

public class Order_history_ListAdapter extends RecyclerView.Adapter<Order_history_ListAdapter.ViewHolder> {
  private Context context;
  private List<RequestBeans> list;


  public Order_history_ListAdapter(Context context, List<RequestBeans> list) {
    this.context = context;
    this.list = list;

  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_list , parent , false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    RequestBeans bean = list.get(position);
    holder.amt_id.setText(list.get(position).getName());
    holder.req_id.setText(list.get(position).getAddress());
    holder.req_date.setText(list.get(position).getDate());
    holder.pickupstatus.setText(list.get(position).getStatus());
    holder.itemView.setTag(bean);


  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  protected class ViewHolder extends RecyclerView.ViewHolder {
    public AppCompatTextView req_name , req_address , req_mobile , amt_id , req_date ,req_id,pickupstatus;
    public LinearLayoutCompat details_show;
    public ViewHolder(final View itemView) {
      super(itemView);


      req_date = itemView.findViewById(R.id.pickup_dateTime);
      req_id = itemView.findViewById(R.id.pickup_id);
      details_show = itemView.findViewById(R.id.details_show);
      pickupstatus = itemView.findViewById(R.id.pickupstatus);
      amt_id=itemView.findViewById(R.id.amt_id);
    }



  }
}
