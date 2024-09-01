package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.activities.SubCategoryList;
import com.superdrycleaners.drycleaners.beans.RateModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RateListAdapter extends RecyclerView.Adapter<RateListAdapter.ViewHolder> {
    private List<RateModel> list;
    private Context context;

    public RateListAdapter(Context context, List<RateModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String IMAGE_BASE = ConfigClass.GET_IMAGE_URL;
        RateModel model = list.get(position);

        holder.job.setText(list.get(position).getItemName());
        holder.itemView.setTag(model);
        Picasso.with(context).load(IMAGE_BASE + model.getImage()).resize(150, 150).placeholder(R.drawable.noimageavailable).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName, job, minRate, maxRate, remarks, date_text;
        CircleImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemID);
            job = itemView.findViewById(R.id.itemName);
            imageView = itemView.findViewById(R.id.img_id);

            itemView.findViewById(R.id.details_show).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RateModel model = (RateModel) itemView.getTag();
                    SharedPreferences.Editor editor = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
                    editor.putString("itemName", model.getItemName());
                    editor.putString("itemID", model.getId());
                    editor.apply();
                    Intent intent = new Intent(context, SubCategoryList.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);


                }
            });

        }
    }
}
