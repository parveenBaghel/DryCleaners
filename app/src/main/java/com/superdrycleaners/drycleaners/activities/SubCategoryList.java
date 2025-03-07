package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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
import com.superdrycleaners.drycleaners.adapters.SubCategoryAdapter;
import com.superdrycleaners.drycleaners.beans.SubCategoryModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubCategoryList extends AppCompatActivity {
    ArrayList<SubCategoryModel> sub_list;
    SubCategoryAdapter adapter;
    TextView textView;
    RecyclerView recyclerView;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_category_list);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.tv_cate_name);


        sub_list = new ArrayList<>();
        setSupportActionBar(toolbar);
        checkConnection();
        //toolbar.setBackgroundColor(Color.parseColor("#ca0606"));

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));


        SharedPreferences sharedPreferences = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        final String category_name = sharedPreferences.getString("itemName", null);
        textView.setText(category_name);

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
            String itemID = sp.getString("itemID", "");


            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("category_id", itemID);
                requestService(jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(SubCategoryList.this);
        pd.setMessage("Please wait...!");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.SUBCATEGORY_LIST, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response===> ", String.valueOf(response));
                pd.dismiss();
                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                try {

                    if (sub_list.size() > 0) {
                        sub_list.clear();
                        recyclerView.setAdapter(null);
                    }
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray subArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < subArray.length(); i++) {
                            JSONObject jsonObject1 = subArray.getJSONObject(i);
                            String category_id = jsonObject1.getString("id");
                            String category_name = jsonObject1.getString("item_name");
                            String category_image = jsonObject1.getString("item_image");
                            Log.e("subList===> ", category_id + category_name + category_image);

                            SubCategoryModel sliderUtils = new SubCategoryModel(category_id, category_name, category_image);
                            sub_list.add(sliderUtils);
                        }

                        recyclerView = findViewById(R.id.sub_recycler_view);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SubCategoryList.this);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new SubCategoryAdapter(SubCategoryList.this, sub_list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.setHasFixedSize(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //Toast.makeText(getApplicationContext() , error.toString() , Toast.LENGTH_LONG).show();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(SubCategoryList.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(SubCategoryList.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(SubCategoryList.this, "Server Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(SubCategoryList.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(SubCategoryList.this, "Not Parsed", Toast.LENGTH_SHORT).show();

                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);

    }

  /*  @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.item_id);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);


        //item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        //final SearchView searchView = (SearchView) item.getActionView();
        final SearchView searchView = (SearchView) item.getActionView();
        //searchView/setSupportActionBar(S);

        *//*searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" +
                getResources().getString(R.color.white) + "</font>"));
        searchView.setQueryHint("Find by-category");

*//*
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter == null) {
                    searchView.setEnabled(false);
                } else {
                    adapter.filter(newText);
                    *//*if (getSupportActionBar() != null) {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
                    }
*//*
                    //searchView.setIconified(false);

                }
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);


    }*/

    public void finish(View view) {
        finish();
    }
}
