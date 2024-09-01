package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.activities.AdapterCallback;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;

import java.util.List;

public class AddJobsListAdapter extends RecyclerView.Adapter<AddJobsListAdapter.ViewHolder> {

  private List<RateModel> list;
  private Context context;
  int counter = 1;
  private AdapterCallback adapterCallback;
  public AddJobsListAdapter(Context context, List<RateModel> list , AdapterCallback adapterCallback) {
    this.context = context;
    this.list = list;
    this.adapterCallback = adapterCallback;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_jobs_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    RateModel model = list.get(position);
    holder.job.setText(list.get(position).getJob());
    holder.minRate.setText(list.get(position).getJob_minRate());
    holder.maxRate.setText(list.get(position).getJob_maxRate());
    holder.remarks.setText(list.get(position).getJob_remark());
    holder.item_name.setText(list.get(position).getItemName());
    holder.item_count.setText(list.get(position).getJobQunty());
    holder.itemView.setTag(model);

  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  protected class ViewHolder extends RecyclerView.ViewHolder {
    public AppCompatTextView itemName, job, minRate, maxRate, remarks, date_text , item_name , item_count;

    public ViewHolder(final View itemView) {
      super(itemView);

      job = itemView.findViewById(R.id.job);
      item_name = itemView.findViewById(R.id.item_name);
      minRate = itemView.findViewById(R.id.min_rate);
      maxRate = itemView.findViewById(R.id.max_rate);
      remarks = itemView.findViewById(R.id.remark);
      item_count = itemView.findViewById(R.id.item_count);


      itemView.findViewById(R.id.add_cart_delete).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Context context = itemView.getContext();
          RateModel model = (RateModel) itemView.getTag();
          list.remove(getAdapterPosition());
          notifyItemRemoved(getAdapterPosition());
          sqLiteDB db = new sqLiteDB(context);
          String jobID = model.getJob_id();
          db.carDelete(jobID);
          adapterCallback.onMethodCallback();
        }
      });

      itemView.findViewById(R.id.add_jobQunty).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Context context = itemView.getContext();
          RateModel rateModel = (RateModel) itemView.getTag();
          sqLiteDB db = new sqLiteDB(context);
          double min_amount  , max_amount;
          if(counter > 0)
          {

            String str_min = rateModel.getJob_minRate();
            String str_max = rateModel.getJob_maxRate();

            min_amount = Double.parseDouble(str_min);
            max_amount = Double.parseDouble(str_max);

            int jobid = Integer.parseInt(rateModel.getJob_id());
            List<RateModel> jobs = db.getAddCart();
            for (RateModel model : jobs) {
              int mactJobID = Integer.parseInt(model.getJob_id());
              if(jobid == mactJobID) {
                String str_min1 = model.getJob_minRate();
                String str_max1 = model.getJob_maxRate();

                double min1 = Double.parseDouble(str_min1);
                double max1 = Double.parseDouble(str_max1);
                max_amount = max_amount+max1;
                min_amount = min_amount+min1;
                String count = model.getJobQunty();
                int mainCount = Integer.parseInt(count);
                counter = mainCount;
                counter++;
                item_count.setText(""+counter);
                minRate.setText(""+min_amount);
                maxRate.setText(""+max_amount);
                db.updateJobTable(rateModel.getJob_id() , counter , min_amount ,max_amount);
                adapterCallback.onMethodCallback();
              }

            }

          }

        }
      });
      itemView.findViewById(R.id.less_jobQunty).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


          Context context = itemView.getContext();
          RateModel rateModel = (RateModel) itemView.getTag();
          sqLiteDB db = new sqLiteDB(context);
          int update_count = 0;
          double min_amount  , max_amount;
          int jobid1 = Integer.parseInt(rateModel.getJob_id());
          List<RateModel> jobss = db.getAddCart();
          for (RateModel modell : jobss) {
            int les_jobId = Integer.parseInt(modell.getJob_id());
            if(jobid1 == les_jobId)
            {
              update_count = Integer.parseInt(modell.getJobQunty());
            }

          }
          counter = update_count;


          if(counter > 1)
          {

            String str_min = rateModel.getOldMin();
            String str_max = rateModel.getOldMax();

            min_amount = Double.parseDouble(str_min);
            max_amount = Double.parseDouble(str_max);

            int jobid = Integer.parseInt(rateModel.getJob_id());
            List<RateModel> jobs = db.getAddCart();
            for (RateModel model : jobs) {
              int mactJobID = Integer.parseInt(model.getJob_id());
              if(jobid == mactJobID) {
                String str_min1 = model.getJob_minRate();
                String str_max1 = model.getJob_maxRate();

                double min1 = Double.parseDouble(str_min1);
                double max1 = Double.parseDouble(str_max1);
                // max_amount = max_amount-max1;
                // min_amount = min_amount-min1;
                min1 = min1 - min_amount;
                max1 = max1 - max_amount;
                String count = model.getJobQunty();
                int mainCount = Integer.parseInt(count);
                counter = mainCount;
                counter--;
                item_count.setText(""+counter);
                minRate.setText(""+min1);
                maxRate.setText(""+max1);
                db.updateJobTable(rateModel.getJob_id() , counter , min1 ,max1);
                adapterCallback.onMethodCallback();
              }

            }

          }

        }
      });

    }
  }
}