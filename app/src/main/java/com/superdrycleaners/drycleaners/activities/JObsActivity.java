package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.superdrycleaners.drycleaners.adapters.JobsRateListAdapter;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JObsActivity extends AppCompatActivity implements AdapterCallback{
  AppCompatTextView addCart_count;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_jobs);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
               /* startActivity(new Intent(JObsActivity.this , RateChartCategoryList.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                JObsActivity.this.finish();*/
      }
    });
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    addCart_count = toolbar.findViewById(R.id.add_count);

    findViewById(R.id.addshow_cart).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(JObsActivity.this , AddJobsCart_Details.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
        JObsActivity.this.finish();
      }
    });


    showAddJobs();
    viewController();
  }


  private void viewController() {

    SharedPreferences sp = getSharedPreferences("MyPref" , MODE_PRIVATE);
    String itemID = sp.getString("id" , "");

    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("item_id", itemID);
      requestService(jsonObject);
    } catch (JSONException e) {
      e.printStackTrace();
    }




  }

  private void requestService(JSONObject data) {

    final List<RateModel> list = new ArrayList<>();
    final ProgressDialog pd = new ProgressDialog(JObsActivity.this);
    pd.setCancelable(false);
    pd.setCanceledOnTouchOutside(false);
    pd.setMessage("Please Wait...!");
    pd.show();

    final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.POST, ConfigClass.PRICE_LIST, data,
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
                      JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                      RateModel model = new RateModel();

                      model.setJob_id(jsonObject1.getString("id"));
                      model.setJob_itemID(jsonObject1.getString("item_id"));
                      model.setJob(jsonObject1.getString("job"));
                      model.setJob_minRate(jsonObject1.getString("min_rate"));
                      model.setJob_maxRate(jsonObject1.getString("max_rate"));
                      model.setJob_remark(jsonObject1.getString("remarks"));
                      model.setJob_date(jsonObject1.getString("created_at"));
                      model.setJob_status(jsonObject1.getString("status"));
                      model.setJob_active(jsonObject1.getString("active"));

                      list.add(model);
                    }
                    RecyclerView recyclerView = findViewById(R.id.Job_RecyclerView);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JObsActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    JobsRateListAdapter adapter = new JobsRateListAdapter(JObsActivity.this, list , JObsActivity.this);
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
        Toast.makeText(getApplicationContext(), "No Any Jobs..!", Toast.LENGTH_LONG).show();
        if (error instanceof TimeoutError ||error instanceof NoConnectionError){
          Toast.makeText(JObsActivity.this,"No Connection",Toast.LENGTH_SHORT).show();

        }else if (error instanceof AuthFailureError){
          Toast.makeText(JObsActivity.this,"Authentication Failure",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ServerError){
          Toast.makeText(JObsActivity.this,"No Any Jobs..!",Toast.LENGTH_SHORT).show();

        }else if (error instanceof NetworkError){
          Toast.makeText(JObsActivity.this,"Network Error",Toast.LENGTH_SHORT).show();

        }else if (error instanceof ParseError){
          Toast.makeText(JObsActivity.this,"Not Parsed",Toast.LENGTH_SHORT).show();

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

  private void showAddJobs() {
    sqLiteDB db = new sqLiteDB(JObsActivity.this);
    List<RateModel> jobs = db.getAddCart();

    AppCompatTextView add_count = findViewById(R.id.add_count);
    add_count.setText(""+jobs.size());
    for (RateModel model : jobs) {
      String log = "Name: " + model.getItemName() + " ,JOb Name: " + model.getJob();
    }
  }

  @Override
  public void onMethodCallback() {
    sqLiteDB db = new sqLiteDB(this);
    List<RateModel> jobs = db.getAddCart();
    AppCompatTextView add_count = findViewById(R.id.add_count);
    add_count.setText(""+jobs.size());
  }
}
