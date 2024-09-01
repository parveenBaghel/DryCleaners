package com.superdrycleaners.drycleaners.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.activities.CounterRequest;
import com.superdrycleaners.drycleaners.activities.Pickup_Details;
import com.superdrycleaners.drycleaners.beans.RequestBeans;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    private Context context;
    private List<RequestBeans> list;
    String type;

    public RequestListAdapter(Context context, List<RequestBeans> list,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pikup_request_newlist , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        RequestBeans bean = list.get(position);
        holder.req_id.setText(list.get(position).getPickup_id());
        holder.req_date.setText(list.get(position).getDate());
        holder.pickupstatus.setText(list.get(position).getStatus());
        Log.e("Pickup-Status-",""+list.get(position).getStatus());

        switch (bean.getStatus()){
            case "Picked":
                if (list.get(position).getD_req().equals("0")) {
                    holder.pickupstatus.setText("Picked");
                }else{
                    holder.pickupstatus.setText("Your delivery request is accepted");
                }

                holder.cancelid.setVisibility(View.GONE);
                holder.details_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("Delivery")) {
                            if (list.get(position).getD_req().equals("0")) {
                                Intent intent = new Intent(context, Pickup_Details.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("id", list.get(position).getPickup_id());
                                intent.putExtra("name", list.get(position).getName());
                                intent.putExtra("address", list.get(position).getAddress());
                                intent.putExtra("mobile", list.get(position).getMobile());
                                intent.putExtra("remark", list.get(position).getRemarks());
                                intent.putExtra("date", list.get(position).getDate());
                                intent.putExtra("status", list.get(position).getStatus());
                                context.startActivity(intent);
                            }
                        }else{
                            Intent intent = new Intent(context, CounterRequest.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("PickupID", ""+list.get(position).getPickup_id());
                            context.startActivity(intent);
                        }


                    }
                });

                break;
            case "Pending":
                holder.cancelid.setVisibility(View.VISIBLE);
                break;
            case "Cancelled":
                holder.cancelid.setVisibility(View.GONE);
                break;
            case "Delivered":
                holder.cancelid.setVisibility(View.GONE);
                break;
            case "Processing":
                holder.cancelid.setVisibility(View.GONE);
                break;
            case "Ready to Deliver":
                holder.cancelid.setVisibility(View.GONE);
                break;

        }


        holder.itemView.setTag(bean);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView req_name , req_address , req_mobile , req_remark , req_date ,req_id,cancelid,pickupstatus;
        public LinearLayoutCompat details_show;
        public ViewHolder(final View itemView) {
            super(itemView);

           // req_name = itemView.findViewById(R.id.req_name);
            //req_address = itemView.findViewById(R.id.req_address);
            //req_mobile = itemView.findViewById(R.id.req_mobile);
            //req_remark = itemView.findViewById(R.id.req_remark);
            req_date = itemView.findViewById(R.id.pickup_dateTime);
            req_id = itemView.findViewById(R.id.pickup_id);
            details_show = itemView.findViewById(R.id.details_show);
            pickupstatus=itemView.findViewById(R.id.pickupstatus);

            cancelid=itemView.findViewById(R.id.cancel_id);


            cancelid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successMessage();


                }

                private void successMessage() {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                            context);

                    alertDialogBuilder.setTitle("Confirm");
                    alertDialogBuilder.setMessage("Are you sure want to Cancel?");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sp = context.getSharedPreferences(ConfigClass.MYPREF , MODE_PRIVATE);
                                    String userID = sp.getString(ConfigClass.USERID , "");

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        RequestBeans beans = (RequestBeans) itemView.getTag();

                                        jsonObject.put("cancelledid" ,beans.getPickup_id());
                                        requestServiceCancel(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }





                                }
                            }
                    ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();



                }

                private void requestServiceCancel(final JSONObject data) {
                    final ProgressDialog pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setMessage("Please Wait...!");
                    pd.show();

                    JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, ConfigClass.CANCEL_REQUEST, data, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("response===> " , String.valueOf(response));
                            pd.dismiss();

                            try {
                                boolean status=response.getBoolean("error");

                                if (!status){
                                    RequestBeans model = (RequestBeans) itemView.getTag();
                                    Context context = itemView.getContext();
                                    list.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    sqLiteDB db = new sqLiteDB(context);
                                    String jobID = model.getStatus();
                                    db.carDelete(jobID);

                                }else{
                                    Toast.makeText(context,""+response.getString("message"),Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }
                    };
                    VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
                }
            });


           /* details_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   RequestBeans beans = (RequestBeans) itemView.getTag();
                    Intent intent = new Intent(context , Pickup_Details.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id" , beans.getPickup_id());
                    intent.putExtra("name" , beans.getName());
                    intent.putExtra("address" , beans.getAddress());
                    intent.putExtra("mobile" , beans.getMobile());
                    intent.putExtra("remark" , beans.getRemarks());
                    intent.putExtra("date" , beans.getDate());
                    intent.putExtra("status" , beans.getStatus());
                    context.startActivity(intent);
                }
            });*/


        }
    }
}
