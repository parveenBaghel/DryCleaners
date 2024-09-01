package com.superdrycleaners.drycleaners.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.activities.AdapterCallback;
import com.superdrycleaners.drycleaners.activities.WalletActivity;
import com.superdrycleaners.drycleaners.beans.PaymentStatusModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PaymentStatusHistoryAdapter extends RecyclerView.Adapter<PaymentStatusHistoryAdapter.ViewHolder> {
    private Context context;
    private List<PaymentStatusModel> list;
    AdapterCallback adapterCallBack;
    public LinearLayout linearLayout;


    public PaymentStatusHistoryAdapter(Context context, List<PaymentStatusModel> list, AdapterCallback adapterCallBack) {
        this.context = context;
        this.list = list;
        this.adapterCallBack = adapterCallBack;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_layout, parent, false);
        return new PaymentStatusHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentStatusHistoryAdapter.ViewHolder holder, final int position) {
        PaymentStatusModel bean = list.get(position);
        holder.req_amt.setText(list.get(position).getAmount());
        holder.date_time.setText(list.get(position).getDate());
        holder.status.setText(list.get(position).getPay_status());
        holder.billNo.setText(list.get(position).getBill_no());

        String sts = holder.status.getText().toString();
        switch (sts) {
            case "1":
                holder.status.setText("Done");
                holder.done.setVisibility(View.GONE);
                break;
            case "0":
                holder.status.setText("Pending");
                holder.done.setVisibility(View.VISIBLE);
                break;

        }

        holder.itemView.setTag(bean);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView req_amt, date_time, status, billNo;
        Button done;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            req_amt = itemView.findViewById(R.id.job_id);
            date_time = itemView.findViewById(R.id.date_time_id);
            status = itemView.findViewById(R.id.status_id);
            done = itemView.findViewById(R.id.done_id);
            billNo = itemView.findViewById(R.id.billNoTV);
            linearLayout = itemView.findViewById(R.id.layoutBtn);

            itemView.findViewById(R.id.done_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentStatusModel model = (PaymentStatusModel) itemView.getTag();
                    SharedPreferences sp = context.getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor;
                    editor = sp.edit();
                    editor.putString("pay_statu", "1");
                    editor.putString("ide", model.getId());
                    editor.putString("BillNo",model.getBill_no());
                    editor.putString("PayableAmount", model.getAmount());
                    editor.apply();
                    context.startActivity(new Intent(context, WalletActivity.class));

                }
            });

        }
    }
}








