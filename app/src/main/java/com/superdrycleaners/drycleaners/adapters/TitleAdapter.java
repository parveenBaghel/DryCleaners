package com.superdrycleaners.drycleaners.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.activities.DeliveryActivity;
import com.superdrycleaners.drycleaners.activities.LoginActivity;
import com.superdrycleaners.drycleaners.activities.Offers;
import com.superdrycleaners.drycleaners.activities.Payment_status;
import com.superdrycleaners.drycleaners.activities.PickuRequestList_Activity;
import com.superdrycleaners.drycleaners.activities.Pickup_Request_Activity;
import com.superdrycleaners.drycleaners.activities.RateChartCategoryList;
import com.superdrycleaners.drycleaners.beans.TitleModel;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.MyViewHolder> {
  private final Context context;
  private final List<TitleModel> titleList;
  private boolean loginStatus;


  public TitleAdapter(Context context, List<TitleModel> titleList) {
    this.context = context;
    this.titleList = titleList;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    LayoutInflater inflater= LayoutInflater.from(context);
    view=inflater.inflate(R.layout.grid_item_layout,parent,false);
    return new MyViewHolder(view);


  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

    SharedPreferences sp = context.getSharedPreferences(ConfigClass.MYPREF , MODE_PRIVATE);
    loginStatus = sp.getBoolean(ConfigClass.LOGINSTATUS , false);

    holder.tv_title.setText(titleList.get(position).getTittle());
    holder.imageView.setImageResource(titleList.get(position).getIcon());
    holder.cardView.setOnClickListener(new View.OnClickListener() {


      @Override
      public void onClick(View v) {
        switch (holder.getAdapterPosition()) {
          case 0:
            if (loginStatus) {
              context.startActivity(new Intent(context, Pickup_Request_Activity.class));


            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
              Toast toast = Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT);

            }
            break;

          case 1:
            if (loginStatus) {
              context.startActivity(new Intent(context, PickuRequestList_Activity.class).putExtra("pickup_id" , 0));
            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
              Toast toast = Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT);
            }
            break;
          /*case 2:
            if (loginStatus) {
              context.startActivity(new Intent(context, Offers.class));
            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
              Toast toast = Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT);
            }
            break;*/
          case 2:
            if (loginStatus) {
              context.startActivity(new Intent(context, RateChartCategoryList.class));
            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
              Toast toast = Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT);
            }
            break;


        }
      }


    });

  }

  @Override
  public int getItemCount() {
    return titleList.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder{
    TextView tv_title;
    ImageView imageView;
    CardView cardView;


    public MyViewHolder(View itemView) {
      super(itemView);
      tv_title=(TextView)itemView.findViewById(R.id.title_id);
      imageView=(ImageView)itemView.findViewById(R.id.icon_id);
      cardView=(CardView)itemView.findViewById(R.id.cardViewItem);







    }
  }
}
