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
import com.superdrycleaners.drycleaners.activities.JObsActivity;
import com.superdrycleaners.drycleaners.activities.Product_Details;
import com.superdrycleaners.drycleaners.beans.SubCategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    private List<SubCategoryModel> list;
    private Context context;
    private ArrayList<SubCategoryModel> item_ListFiltered;

    public SubCategoryAdapter(Context context, List<SubCategoryModel> list) {
        this.context = context;
        this.list = list;
        this.item_ListFiltered = new ArrayList<SubCategoryModel>();
        this.item_ListFiltered.addAll(list);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_list_layout, parent, false);
        return new SubCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.ViewHolder holder, final int position) {
        String IMAGE_BASE = ConfigClass.GET_IMAGE_URL;
        final SubCategoryModel model = list.get(position);
        holder.category_name.setText(list.get(position).getItemName());

        Picasso.with(context).load(IMAGE_BASE + list.get(position).getImage()).placeholder(R.drawable.noimageavailable).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
                editor.putString("id", model.getId());
                editor.apply();

                Intent intent = new Intent(context, JObsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return (null != list ?
                list.size() : 0);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(item_ListFiltered);
        } else {
            for (SubCategoryModel wp : item_ListFiltered) {
                if (wp.getItemName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(wp);
                }
            }
            notifyDataSetChanged();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName, category_name, minRate, maxRate, remarks, date_text;
        CircleImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.itemName);
            imageView = itemView.findViewById(R.id.img_id);

        }

    }
}
