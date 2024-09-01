package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.superdrycleaners.drycleaners.App.ConnectivityReceiver;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.adapters.CouponListAdapter;
import com.superdrycleaners.drycleaners.beans.CouponBannerModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coupon extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    public static final String MYPREF = "MyPref";
    public static final String LOGINSTATUS = "login_status";
    public static final String USERID = "uid";
    String userCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("My Coupons");
        setSupportActionBar(toolbar);

        SharedPreferences sp1 = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        String userID = sp1.getString(ConfigClass.USERID, "");
        userCode = sp1.getString(ConfigClass.USER_CODE, "");

        Log.e("USERCODE===> " , userCode+userID);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // onBackPressed();
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        checkConnection();


    }

    private void checkConnection() {
        boolean isConnected= ConnectivityReceiver.isConnected();
        showSnack(isConnected);

    }
    private void showSnack(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            String userID = sp.getString(ConfigClass.USERID, "");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_code", userCode);
                requestService(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
            //noConnection.setVisibility(View.VISIBLE);
        }
    }

    private void requestService(JSONObject data) {
        final List<CouponBannerModel> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(Coupon.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();
        JsonObjectRequest jsonObjReq=new JsonObjectRequest(Request.Method.POST, ConfigClass.COUPON_LIST, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response===> " , String.valueOf(response));
                pd.dismiss();
                //Toast.makeText(getApplicationContext() , response.toString() , Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String error = jsonObject.getString("error");

                    if (error.equals("false")) {
                        JSONArray bannerArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<bannerArray.length();i++){
                            JSONObject jsonObject1=bannerArray.getJSONObject(i);
                            CouponBannerModel model = new CouponBannerModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setCouponImage(jsonObject1.getString("coupon_image"));
                            model.setCouponCode(jsonObject1.getString("coupon_code"));
                            model.setAmount(jsonObject1.getString("amount"));
                            model.setExp_days(jsonObject1.getString("exp_days"));
                            model.setCoupon_type(jsonObject1.getString("coupon_type"));
                            model.setC_type(jsonObject1.getString("c_type"));
                            model.setUser_code(jsonObject1.getString("user_code"));
                            model.setStatus(jsonObject1.getString("status"));
                            model.setInstallation_date(jsonObject1.getString("installation_date"));
                            list.add(model);
                        }
                        RecyclerView recyclerView = findViewById(R.id.coupon_RecyclerView);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Coupon.this);
                        recyclerView.setLayoutManager(layoutManager);
                        CouponListAdapter adapter = new CouponListAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(getApplicationContext(),""+response.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("responseError===> " , String.valueOf(error));
                //Toast.makeText(getApplicationContext() , error.toString() , Toast.LENGTH_LONG).show();
                if (error instanceof TimeoutError ||error instanceof NoConnectionError){
                    Toast.makeText(Coupon.this,"No Connection",Toast.LENGTH_SHORT).show();

                }else if (error instanceof AuthFailureError){
                    Toast.makeText(Coupon.this,"Authentication Failure",Toast.LENGTH_SHORT).show();

                }else if (error instanceof ServerError){
                    Toast.makeText(Coupon.this,"Server Error",Toast.LENGTH_SHORT).show();

                }else if (error instanceof NetworkError){
                    Toast.makeText(Coupon.this,"Network Error",Toast.LENGTH_SHORT).show();

                }else if (error instanceof ParseError){
                    Toast.makeText(Coupon.this,"Not Parsed",Toast.LENGTH_SHORT).show();

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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);






    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
