package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.App.ConnectivityReceiver;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.adapters.RateListAdapter;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RateChartCategoryList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        checkConnection();

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            LoadCategoryList();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
        }


    }


    private void LoadCategoryList() {
        final List<RateModel> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(RateChartCategoryList.this);
        pd.setMessage("Please wait...!");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.CATEGORY_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.e("CategoryList", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("error");
                    if (!status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            RateModel model = new RateModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setItemName(jsonObject1.getString("category"));
                            model.setImage(jsonObject1.getString("image"));
                            list.add(model);

                        }
                        RecyclerView recyclerView = findViewById(R.id.pricing_RecyclerView);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RateChartCategoryList.this);
                        recyclerView.setLayoutManager(layoutManager);
                        RateListAdapter adapter = new RateListAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "" + error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
