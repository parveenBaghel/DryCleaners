package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.App.ConnectivityReceiver;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.adapters.OffersListAdapter;
import com.superdrycleaners.drycleaners.beans.OfferBannerModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Offers extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MYPREF = "MyPref";
    public static final String LOGINSTATUS = "login_status";
    public static final String USERID = "uid";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Offers");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
            GetOfferListFromServer();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();

        }
    }

    private void GetOfferListFromServer() {
        final List<OfferBannerModel> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(Offers.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.OFFER_BANNERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.e("OFFERlIST", response);
                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray bannerArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < bannerArray.length(); i++) {
                            JSONObject jsonObject1 = bannerArray.getJSONObject(i);
                            OfferBannerModel model = new OfferBannerModel();
                            model.setOfferImage(jsonObject1.getString("offer_image"));
                            model.setOfferAmount(jsonObject1.getString("offer_amount"));
                            model.setOfferTotal(jsonObject1.getString("offer_total"));
                            model.setOfferPercentage(jsonObject1.getString("offer_per"));
                            model.setDescription(jsonObject1.getString("description"));
                            model.setOfferId(jsonObject1.getString("id"));
                            list.add(model);
                        }
                        RecyclerView recyclerView = findViewById(R.id.offer_RecyclerView);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Offers.this);
                        recyclerView.setLayoutManager(layoutManager);
                        OffersListAdapter adapter = new OffersListAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    } else {
                        Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json;charset=UTF-8");


                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Offers.this, MainActivity.class);
        startActivity(intent);
        this.finish();

    }
}
