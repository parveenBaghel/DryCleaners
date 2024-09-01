package com.superdrycleaners.drycleaners.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.App.CustomTimePickerDialog;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {
    String[] deliveryAddress = {"Select Address", "Pickup Address", "Store Address", "Optional"};
    AppCompatEditText enterAddress;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String str_date, newDate;
    private String str_time = "";
    private String itemsName = "";
    private String pickupID;
    AppCompatTextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

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
        viewController();
    }

    private void viewController() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        pickupID = String.valueOf(getIntent().getExtras().getString("pickup_id", "0"));
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        final String userID = sp.getString(ConfigClass.USERID, "");

        final AppCompatTextView dateText = findViewById(R.id.date_text);
        timeText = findViewById(R.id.time_text);
        enterAddress = findViewById(R.id.enterAddress);
        final AppCompatSpinner picupAdress = findViewById(R.id.addressSpinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deliveryAddress);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        picupAdress.setAdapter(dataAdapter);

        findViewById(R.id.setDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(DeliveryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                str_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                newDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dateText.setText(newDate);
                                Log.e("ssdsadas==>", newDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        findViewById(R.id.timePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time hear and add 2 hour and pass hour and minut in dialog parametr
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, 2);// 2 add
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
                str_time = sdf.format(cal.getTime());
                Log.e("TEST", str_time);
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(DeliveryActivity.this, timeSetlistner, hour, minute, false);
                customTimePickerDialog.show();

            }


        });

        picupAdress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_color));

                if (position != 0) {
                    itemsName = parent.getItemAtPosition(position).toString();
                    Log.e("values==> ", itemsName);
                }
                if (position == 3) {
                    enterAddress.setVisibility(View.VISIBLE);
                } else {
                    enterAddress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.pickupSubmit_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatTextView codPayment_text = findViewById(R.id.codPayment);
                String codPayment = codPayment_text.getText().toString();
                if (dateText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Date..!", Toast.LENGTH_LONG).show();
                }
                /*else if (timeText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Time..!", Toast.LENGTH_LONG).show();
                } */
                else if (itemsName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Address..!", Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("pickup_id", pickupID);
                        jsonObject.put("uid", userID);
                        jsonObject.put("delivery_date_time", str_date + " " + str_time);
                        jsonObject.put("delivery_location", itemsName);
                        jsonObject.put("payment_option", codPayment);
                        jsonObject.put("delivery_other", "");
                        Log.d("DeliveryRequest Json-->",jsonObject.toString());
                        requestService(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void requestService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(DeliveryActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.DELIVERY, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("pick response===>", String.valueOf(response));
                        pd.dismiss();
                        try {
                            boolean status = response.getBoolean("error");
                            if (!status) {
                                successMessage();
                            } else {
                                Toast.makeText(getApplicationContext(), "" + response.getString("message").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
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


    private void successMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Your Delivery request Successfully submitted..!");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DeliveryActivity.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timeSetlistner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String getAMPMValue = "a.m";
            if (hourOfDay > 11) {
                getAMPMValue = "p.m";
                if (hourOfDay != 12) {
                    hourOfDay = hourOfDay - 12;


                }

            }
            timeText.setText("" + hourOfDay + ":" + minute + " " + getAMPMValue);
        }
    };

}
