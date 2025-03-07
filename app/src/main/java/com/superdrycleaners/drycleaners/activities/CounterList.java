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
import com.superdrycleaners.drycleaners.adapters.CounterDeliveryListAdapter;
import com.superdrycleaners.drycleaners.adapters.RequestListAdapter;
import com.superdrycleaners.drycleaners.beans.CounterRequestBeans;
import com.superdrycleaners.drycleaners.beans.RequestBeans;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.counter_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setTitle("Counter List");
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
    boolean isConnected= ConnectivityReceiver.isConnected(getApplicationContext());
    showSnack(isConnected);
  }
  private void showSnack(boolean isConnected) {
    if (isConnected) {
      SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
      String userID = sp.getString(ConfigClass.USERID, "");

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("uid", userID);
        counter_requestService(jsonObject);
        Log.e("UserID-",""+userID);
      } catch (JSONException e) {
        e.printStackTrace();
      }

    } else {
      Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
    }
  }

  private void counter_requestService(JSONObject data) {
    final List<RequestBeans> list = new ArrayList<>();
    final ProgressDialog pd = new ProgressDialog(CounterList.this);
    pd.setCancelable(false);
    pd.setCanceledOnTouchOutside(false);
    pd.setMessage("Please Wait...!");
    pd.show();

    final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.POST, ConfigClass.COUNTER_LIST, data,
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
                      RequestBeans model = new RequestBeans();
                      model.setPickup_id(jsonObject.getString("id"));
                      model.setName(jsonObject.getString("name"));
                      model.setAddress(jsonObject.getString("address"));
                      model.setMobile(jsonObject.getString("mobile_no"));
                      model.setRemarks(jsonObject.getString("remarks"));
                      model.setDate(jsonObject.getString("pickup_date"));
                      model.setTime(jsonObject.getString("pickup_time"));
                      model.setD_req(jsonObject.getString("d_req"));
                      model.setStatus(jsonObject.getString("status"));
                      list.add(model);
                    }
                    RecyclerView recyclerView = findViewById(R.id.delivery_RecyclerView);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CounterList.this);
                    recyclerView.setLayoutManager(layoutManager);
                    RequestListAdapter adapter = new RequestListAdapter(CounterList.this , list,"Counter");
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
        if (error instanceof TimeoutError ||error instanceof NoConnectionError){
          Toast.makeText(CounterList.this,"No Connection",Toast.LENGTH_SHORT).show();

        }else if (error instanceof AuthFailureError){
          Toast.makeText(CounterList.this,"Authentication Failure",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ServerError){
          Toast.makeText(CounterList.this,"Empty!",Toast.LENGTH_SHORT).show();

        }else if (error instanceof NetworkError){
          Toast.makeText(CounterList.this,"Network Error",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ParseError){
          Toast.makeText(CounterList.this,"Not Parsed",Toast.LENGTH_SHORT).show();

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

