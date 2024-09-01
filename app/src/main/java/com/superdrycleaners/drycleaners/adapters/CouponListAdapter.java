package com.superdrycleaners.drycleaners.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.superdrycleaners.drycleaners.beans.CouponBannerModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ViewHolder> {

    private List<CouponBannerModel> list;
    private Context context;
    ImageView imageView;
    Date sdate;
    String date1;

    public CouponListAdapter(Context context, List<CouponBannerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_layout , parent , false);
        return new CouponListAdapter.ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final CouponListAdapter.ViewHolder holder, int position) {
        //String IMAGE_BASE = "http://dry-clean.in/backend/";
        final CouponBannerModel model = list.get(position);

        holder.itemName.setText(model.getCouponCode());
        holder.re_id.setText("Rs."+" "+model.getAmount());
        holder.type.setText("Coupon Type:"+" "+model.getCoupon_type());


        SharedPreferences sp = context.getSharedPreferences(ConfigClass.MYPREF, Context.MODE_APPEND);
        final String userID = sp.getString(ConfigClass.USERID, "");

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        String CurrentDate = mYear + "/" + mMonth + "/" + mDay;
        String dateInString = CurrentDate; // Select date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateInString));
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 14);    //14 dats add
        sdf = new SimpleDateFormat("dd-MM-yyyy");

        Date resultdate = new Date(c.getTimeInMillis());

        dateInString = sdf.format(resultdate);
        System.out.println(dateInString);
        holder.itemView.setTag(model);
        holder.date_text.setText("Expiry Date:"+" "+dateInString);
        holder.status_id.setText(model.getStatus());

        String stat=holder.status_id.getText().toString();
        Log.e("hgth",stat);

        String s=new String("Y");

        if (stat.equals(s)){
            holder.status_id.setText("Status:"+" "+"Redeemed");
            holder.buy.setVisibility(View.GONE);


        }else {
            holder.buy.setVisibility(View.VISIBLE);


        }

        if (CurrentDate.equals(dateInString)) {
            holder.buy.setVisibility(View.GONE);


        } else {

        }

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CouponBannerModel bn = (CouponBannerModel) holder.itemView.getTag();
                //Toast.makeText(context,"Selected item position is---"+model.getCouponCode(),Toast.LENGTH_SHORT).show();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", userID);
                    jsonObject.put("user_code",bn.getUser_code());
                    jsonObject.put("coupon_id", bn.getId());
                    jsonObject.put("coupon_amt", bn.getAmount());
                    GetReddemedAmount(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void GetReddemedAmount(JSONObject data) {
                final ProgressDialog pd = new ProgressDialog(context);
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("Please Wait...!");


                JsonObjectRequest jsonObjReq=new JsonObjectRequest(Request.Method.POST, ConfigClass.PRO_COUPON_AMOUNT, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response===> " , String.valueOf(response));
                        try {
                            if (!response.getBoolean("error")) {
                                Toast.makeText(context, "Redeemed Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context ,""+response.getString("message").toString(), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        holder.buy.setVisibility(View.GONE);






                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("responseError===> " , String.valueOf(error));
                        Toast.makeText(context , error.toString() , Toast.LENGTH_LONG).show();


                        //pd.dismiss();

                        if (error instanceof TimeoutError ||error instanceof NoConnectionError){
                            Toast.makeText(context,"No Connection",Toast.LENGTH_SHORT).show();

                        }else if (error instanceof AuthFailureError){
                            Toast.makeText(context,"Authentication Failure",Toast.LENGTH_SHORT).show();

                        }else if (error instanceof ServerError){
                            Toast.makeText(context,"Server Error",Toast.LENGTH_SHORT).show();

                        }else if (error instanceof NetworkError){
                            Toast.makeText(context,"Network Error",Toast.LENGTH_SHORT).show();

                        }else if (error instanceof ParseError){
                            Toast.makeText(context,"Not Parsed",Toast.LENGTH_SHORT).show();

                        }

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);


            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView itemName  , name , buy , type , re_id , date_text,status_id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.code_id);
            re_id=itemView.findViewById(R.id.Rs_id);
            type=itemView.findViewById(R.id.type_id);
            date_text=itemView.findViewById(R.id.ex_id);
            imageView=itemView.findViewById(R.id.img_id);
            buy=itemView.findViewById(R.id.redeem_id);
            status_id=itemView.findViewById(R.id.demd_id);
        }
    }
}
