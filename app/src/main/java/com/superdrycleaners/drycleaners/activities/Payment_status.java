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
import com.superdrycleaners.drycleaners.adapters.PaymentStatusHistoryAdapter;
import com.superdrycleaners.drycleaners.beans.PaymentStatusModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Payment_status extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AdapterCallback {

    String p_staus, p_id;
    String userID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Payment History");
        checkConnection();
        setSupportActionBar(toolbar);
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            String userID = sp.getString(ConfigClass.USERID, "");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", userID);
                payment_requestService(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();

        }
    }

    private void payment_requestService(JSONObject data) {
        final List<PaymentStatusModel> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(Payment_status.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.PAYMENT_HISTORY, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response===> ", String.valueOf(response));
                        pd.dismiss();
                        try {
                            boolean status = response.getBoolean("error");
                            if (!status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String id = jsonObject.getString("id");
                                    String userid = jsonObject.getString("user_id");
                                    String amt = jsonObject.getString("amount");
                                    String billNo = jsonObject.getString("bill_no");
                                    String remark = jsonObject.getString("remark");
                                    String pay_staus = jsonObject.getString("pay_status");
                                    String date = jsonObject.getString("created_at");

                                    PaymentStatusModel model = new PaymentStatusModel(id, userid, amt, remark, pay_staus, billNo, date);
                                    list.add(model);

                                }
                                RecyclerView recyclerView = findViewById(R.id.payment_RecyclerView);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Payment_status.this);
                                recyclerView.setLayoutManager(layoutManager);

                                PaymentStatusHistoryAdapter adapter = new PaymentStatusHistoryAdapter(Payment_status.this, list, (AdapterCallback) Payment_status.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Payment_status.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Payment_status.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(Payment_status.this, "Empty!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(Payment_status.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(Payment_status.this, "Not Parsed", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        String userID = sp.getString(ConfigClass.USERID, "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userID);
            payment_requestService(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMethodCallback() {
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        String userID = sp.getString(ConfigClass.USERID, "");
        p_staus = sp.getString("pay_statu", null);
        p_id = sp.getString("ide", null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userID);
            jsonObject.put("p_status", p_staus);
            jsonObject.put("p_id", p_id);
            getStatusService(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getStatusService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(Payment_status.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.PAYMENT_STATUS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("PaymentStatus", String.valueOf(response));
                        pd.dismiss();
                        try {
                            Toast.makeText(Payment_status.this, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("uid", userID);
                                payment_requestService(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Payment_status.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Payment_status.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(Payment_status.this, "Empty!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(Payment_status.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(Payment_status.this, "Not Parsed", Toast.LENGTH_SHORT).show();
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
}
