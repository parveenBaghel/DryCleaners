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
import com.superdrycleaners.drycleaners.activities.JObsActivity;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;

import java.util.List;

public class JobsRateListAdapter extends RecyclerView.Adapter<JobsRateListAdapter.ViewHolder> {
    private List<RateModel> list;
    private Context mcontext;
    private JObsActivity activity ;
    private AppCompatTextView addCunt;
    private AdapterCallback adapterCallback;

    public JobsRateListAdapter(Context context, List<RateModel> list, AdapterCallback adapterCallback) {
        this.mcontext = context;
        this.list = list;
        this.adapterCallback = adapterCallback;


    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RateModel model = list.get(position);
        holder.job.setText(list.get(position).getJob());
        holder.minRate.setText(list.get(position).getJob_minRate());
        holder.maxRate.setText(list.get(position).getJob_maxRate());
        holder.remarks.setText(list.get(position).getJob_remark());
        holder.date_text.setText(list.get(position).getJob_date());

        String jobID_check = list.get(position).getJob_id();
        sqLiteDB db = new sqLiteDB(mcontext);
        List<RateModel> jobs = db.getAddCart();

        Log.e("ssss==> " , String.valueOf(jobs.size()));
        for (RateModel bean : jobs) {
            String jobId = bean.getJob_id();
            if(jobId.equals(jobID_check))
            {
                Log.e("ddd==> " , String.valueOf(jobId));
                holder.add_cart.setVisibility(View.GONE);
            }/*else{
                Log.e("ddd=11=> " , String.valueOf(jobId));
                holder.add_cart.setVisibility(View.VISIBLE);
            }*/
        }

        holder.itemView.setTag(model);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName, job, minRate, maxRate, remarks, date_text , add_cart;

        public ViewHolder(final View itemView) {
            super(itemView);

            job = itemView.findViewById(R.id.job);
            minRate = itemView.findViewById(R.id.min_rate);
            maxRate = itemView.findViewById(R.id.max_rate);
            remarks = itemView.findViewById(R.id.remark);
            date_text = itemView.findViewById(R.id.date);
            add_cart = itemView.findViewById(R.id.add_cart);

       itemView.findViewById(R.id.add_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();
                    SharedPreferences sp = context.getSharedPreferences("MyPref" , Context.MODE_PRIVATE);
                    String itemName = sp.getString("itemName" , "name");
                    RateModel model = (RateModel) itemView.getTag();
                    RateModel cart = new RateModel();
                    sqLiteDB db = new sqLiteDB(context);
                    //Log.e("id====> " , model.getJob_id());
                    cart.setJob_id(model.getJob_id());
                    cart.setJob_itemID(model.getJob_itemID());
                    cart.setItemName(itemName);
                    cart.setJob(model.getJob());
                    cart.setJob_minRate(model.getJob_minRate());
                    cart.setJob_maxRate(model.getJob_maxRate());
                    cart.setOldMin(model.getJob_minRate());
                    cart.setOldMax(model.getJob_maxRate());
                    cart.setJob_date(model.getJob_date());
                    cart.setJobQunty("1");
                    db.insertCartData(cart);


                    List<RateModel> jobs = db.getAddCart();
                    for (RateModel bean : jobs) {
                        String log = "Name: " + bean.getItemName() + " ,JOb Name: " + bean.getJob();
                        int jobId = Integer.parseInt(bean.getJob_id());
                        if(jobId > 0)
                        {
                           add_cart.setVisibility(View.GONE);
                        }else{
                            add_cart.setVisibility(View.VISIBLE);
                        }
                    }

                 adapterCallback.onMethodCallback();


                }
            });


        }
    }


}
