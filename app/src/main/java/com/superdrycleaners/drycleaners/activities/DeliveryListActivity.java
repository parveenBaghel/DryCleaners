package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.superdrycleaners.drycleaners.adapters.DeliveryListAdapter;
import com.superdrycleaners.drycleaners.beans.RequestBeans;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryListActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                jsonObject.put("uid", userID);
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
        final List<RequestBeans> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(DeliveryListActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.DELIVERY_LIST, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("response===> " , String.valueOf(response));
                        pd.dismiss();

                        try {
                            boolean status = response.getBoolean("error");
                            if(!status)
                            {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    RequestBeans bean = new RequestBeans();
                                    bean.setId(jsonObject.getString("id"));
                                    bean.setPickup_id(jsonObject.getString("pickup_id"));
                                    bean.setDate(jsonObject.getString("delivery_date_time"));
                                    bean.setAddress(jsonObject.getString("delivery_location"));
                                    bean.setPayment_option(jsonObject.getString("payment_option"));
                                    bean.setStatus(jsonObject.getString("status"));
                                    list.add(bean);
                                }
                                RecyclerView recyclerView = findViewById(R.id.delivery_RecyclerView);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DeliveryListActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                DeliveryListAdapter adapter = new DeliveryListAdapter(DeliveryListActivity.this , list);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getApplicationContext() ,""+response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //Toast.makeText(getApplicationContext() , "Empty!" , Toast.LENGTH_LONG).show();
                if (error instanceof TimeoutError ||error instanceof NoConnectionError){
                    Toast.makeText(DeliveryListActivity.this,"No Connection",Toast.LENGTH_SHORT).show();

                }else if (error instanceof AuthFailureError){
                    Toast.makeText(DeliveryListActivity.this,"Authentication Failure",Toast.LENGTH_SHORT).show();

                }else if (error instanceof ServerError){
                    Toast.makeText(DeliveryListActivity.this,"Empty!",Toast.LENGTH_SHORT).show();

                }else if (error instanceof NetworkError){
                    Toast.makeText(DeliveryListActivity.this,"Network Error",Toast.LENGTH_SHORT).show();

                }else if (error instanceof ParseError){
                    Toast.makeText(DeliveryListActivity.this,"Not Parsed",Toast.LENGTH_SHORT).show();

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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
