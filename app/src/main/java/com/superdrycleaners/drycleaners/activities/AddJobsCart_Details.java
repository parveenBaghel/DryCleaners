package com.superdrycleaners.drycleaners.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.adapters.AddJobsListAdapter;
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;

import java.util.List;

public class AddJobsCart_Details extends AppCompatActivity implements AdapterCallback{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addjobs_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                /*startActivity(new Intent(AddJobsCart_Details.this , JObsActivity.class));
                AddJobsCart_Details.this.finish();*/
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        showAddJobs();
    }

    private void showAddJobs() {
        double min_amount = 0.0  , max_amount = 0.0;
        AppCompatTextView total_min_amount = findViewById(R.id.total_min_amount);
        AppCompatTextView total_max_amount = findViewById(R.id.total_max_amount);

        sqLiteDB db = new sqLiteDB(AddJobsCart_Details.this);
        List<RateModel> list = db.getAddCart();

        for (RateModel model : list) {
            String str_min = model.getJob_minRate();
            String str_max = model.getJob_maxRate();

            double min = Double.parseDouble(str_max);
            double max = Double.parseDouble(str_min);

            min_amount = min_amount + min;
            max_amount = max_amount + max;
        }

        total_max_amount.setText("MAX AMOUNT  :  "+max_amount);
        total_min_amount.setText("MIN AMOUNT  :  "+min_amount);

        RecyclerView recyclerView = findViewById(R.id.addJob_RecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddJobsCart_Details.this);
        recyclerView.setLayoutManager(layoutManager);
        AddJobsListAdapter adapter = new AddJobsListAdapter(AddJobsCart_Details.this, list , AddJobsCart_Details.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        findViewById(R.id.check_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddJobsCart_Details.this , Pickup_Request_Activity.class));
                AddJobsCart_Details.this.finish();
            }
        });

    }


    @Override
    public void onMethodCallback() {
        double min_amount = 0.0  , max_amount = 0.0;
        AppCompatTextView total_min_amount = findViewById(R.id.total_min_amount);
        AppCompatTextView total_max_amount = findViewById(R.id.total_max_amount);

        sqLiteDB db = new sqLiteDB(AddJobsCart_Details.this);
        List<RateModel> list = db.getAddCart();

        for (RateModel model : list) {
            String str_min = model.getJob_minRate();
            String str_max = model.getJob_maxRate();

            double min = Double.parseDouble(str_max);
            double max = Double.parseDouble(str_min);

            min_amount = min_amount + min;
            max_amount = max_amount + max;
        }

        total_max_amount.setText("MAX AMOUNT  :  "+max_amount);
        total_min_amount.setText("MIN AMOUNT  :  "+min_amount);
    }
}
