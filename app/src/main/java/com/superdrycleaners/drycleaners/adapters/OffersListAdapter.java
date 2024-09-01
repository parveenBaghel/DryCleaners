package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.activities.StartPaymentActivity;
import com.superdrycleaners.drycleaners.beans.OfferBannerModel;

import java.util.List;

public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.ViewHolder> {
    private List<OfferBannerModel> list;
    private Context context;

    public OffersListAdapter(Context context, List<OfferBannerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OffersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_layout, parent, false);
        return new OffersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OffersListAdapter.ViewHolder holder, final int position) {
        String IMAGE_BASE = "https://siguientesoftwares.in/super/backend/";

        // http://siguientesoftwares.in/super/backend/uploads/offer/super-dry-cleaner-logo.png
        final OfferBannerModel model = list.get(position);

        holder.itemName.setText(model.getDescription());
        holder.itemView.setTag(model);

        Picasso.with(context).load(IMAGE_BASE + model.getOfferImage()).into(holder.imageView);


        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetReddemedAmount();


            }

            private void GetReddemedAmount() {
                OfferBannerModel bn = (OfferBannerModel) holder.itemView.getTag();
                //Toast.makeText(context , "jkhthriot"+model.getOfferAmount() , Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
                editor.putString("offer_amount", bn.getOfferAmount());
                editor.putString("id", bn.getOfferId());
                editor.putString("offer_per", bn.getOfferPercentage());
                editor.apply();
                Intent intent = new Intent(context, StartPaymentActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName, job, buy, maxRate, remarks, date_text;
        AppCompatImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_id);
            job = itemView.findViewById(R.id.itemName);
            imageView = itemView.findViewById(R.id.img_id);
            buy = itemView.findViewById(R.id.buy);
        }
    }
}
