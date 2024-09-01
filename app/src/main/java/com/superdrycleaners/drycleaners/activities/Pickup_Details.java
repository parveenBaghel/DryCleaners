package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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

import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.adapters.RequestListAdapter;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.beans.RequestBeans;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pickup_Details extends AppCompatActivity {

  private String id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pickup__details);

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

    requestDetails();
    String id = getIntent().getExtras().getString("id");

    SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF , MODE_PRIVATE);
    String userID = sp.getString(ConfigClass.USERID , "");

    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("uid" , userID);
      //requestService(jsonObject);
    } catch (JSONException e) {
      e.printStackTrace();
    }


  }

  private void requestDetails() {
    id = getIntent().getExtras().getString("id" , "");
    String name = getIntent().getExtras().getString("name" , "");
    String address = getIntent().getExtras().getString("address" , "");
    String mobile = getIntent().getExtras().getString("mobile" , "");
    String remarks = getIntent().getExtras().getString("remark" , "");
    String date = getIntent().getExtras().getString("date" , "");
    final String status = getIntent().getExtras().getString("status" , "");

    AppCompatTextView text_name = findViewById(R.id.req_name);
    AppCompatTextView text_address = findViewById(R.id.req_address);
    AppCompatTextView text_mobile = findViewById(R.id.req_mobile);
    AppCompatTextView text_remarks = findViewById(R.id.req_remark);
    AppCompatTextView text_date = findViewById(R.id.req_date);
    AppCompatTextView text_status = findViewById(R.id.req_status);

    text_name.setText(name);
    text_address.setText(address);
    text_mobile.setText(mobile);
    text_remarks.setText(remarks);
    text_date.setText(date);
    text_status.setText(status);


    findViewById(R.id.delivery_request).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        // startActivity(new Intent(Pickup_Details.this , DeliveryActivity.class));
        if(status.equals("Pending")){
          successMessage();
        }else{
          Intent intent = new Intent(Pickup_Details.this , DeliveryActivity.class);
          intent.putExtra("pickup_id" , id);
          startActivity(intent);
          overridePendingTransition(R.anim.enter, R.anim.exit);
          Pickup_Details.this.finish();
        }

      }
    });
  }

  private void requestService(JSONObject data) {

    final List<RequestBeans> list = new ArrayList<>();
    final ProgressDialog pd = new ProgressDialog(Pickup_Details.this);
    pd.setCancelable(false);
    pd.setCanceledOnTouchOutside(false);
    pd.setMessage("Please Wait...!");
    pd.show();

    final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.POST, ConfigClass.PICKUP_LIST, data,
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
                      String macthID = jsonObject.getString("id");
                      if(id.equals(macthID)) {
                        JSONArray jsonArray1 = jsonObject.getJSONArray("jobdata");
                        {
                          for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            RateModel bean = new RateModel();

                            bean.setItemName(jsonObject1.getString("item_name"));
                            Log.e("data===> ", jsonObject1.getString("item_name"));
                          }
                        }
                      }
                    }
                    RecyclerView recyclerView = findViewById(R.id.pickuRequest_RecyclerView);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Pickup_Details.this);
                    recyclerView.setLayoutManager(layoutManager);
                    RequestListAdapter adapter = new RequestListAdapter(Pickup_Details.this , list,"Delivery");
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
        Toast.makeText(getApplicationContext() , "Empty!" , Toast.LENGTH_LONG).show();
        if (error instanceof TimeoutError ||error instanceof NoConnectionError){
          Toast.makeText(Pickup_Details.this,"No Connection",Toast.LENGTH_SHORT).show();

        }else if (error instanceof AuthFailureError){
          Toast.makeText(Pickup_Details.this,"Authentication Failure",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ServerError){
          Toast.makeText(Pickup_Details.this,"Empty!",Toast.LENGTH_SHORT).show();

        }else if (error instanceof NetworkError){
          Toast.makeText(Pickup_Details.this,"Network Error",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ParseError){
          Toast.makeText(Pickup_Details.this,"Not Parsed",Toast.LENGTH_SHORT).show();

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

  private void successMessage()
  {
    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage("Your Request is Pending. You Don't send Delivery Request..!");
    alertDialogBuilder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
              }
            });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }
}
