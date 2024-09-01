package com.superdrycleaners.drycleaners.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.superdrycleaners.drycleaners.beans.RateModel;
import com.superdrycleaners.drycleaners.dataBase.sqLiteDB;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CounterRequest extends AppCompatActivity {
    public static final String MYPREF = "MyPref";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String str_date, newDate;
    private String str_time;
    AppCompatTextView timeText;
    String selectTime, cuTime;
    EditText pickup_remark;
    String str_remark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Counter Request");
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
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        String mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
        String email = sp.getString(ConfigClass.EMAIL, "");
        String name = sp.getString(ConfigClass.NAME, "");
        String address = sp.getString(ConfigClass.ADDRESS, "");
        String userID = sp.getString(ConfigClass.USERID, "");
        String pickupID = getIntent().getExtras().getString("PickupID");
        Log.e("respponse", mobileNumber + email + name + address + userID);

        viewController(mobileNumber, email, name, address, userID, pickupID);
    }

    private void viewController(final String mobileNumber, final String email, final String name, final String address, final String userID, final String pickupID) {
        final AppCompatTextView dateText = findViewById(R.id.date_text);
        timeText = findViewById(R.id.time_text);
        pickup_remark = findViewById(R.id.pickup_remark);

        findViewById(R.id.setDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CounterRequest.this,
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
                CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(CounterRequest.this, timeSetlistner, hour, minute, false);
                customTimePickerDialog.show();


            }
        });


        findViewById(R.id.pickupSubmit_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_remark = pickup_remark.getText().toString().trim();
                if (dateText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Date..!", Toast.LENGTH_LONG).show();
                } else if (timeText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Time..!", Toast.LENGTH_LONG).show();
                } else if (pickup_remark.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Remark..!", Toast.LENGTH_LONG).show();
                } else {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("uid", userID);
                        jsonObject.put("mobile_no", mobileNumber);
                        jsonObject.put("email", email);
                        jsonObject.put("name", name);
                        jsonObject.put("address", address);
                        jsonObject.put("delivery_date", str_date);
                        jsonObject.put("delivery_time", str_time);
                        Log.e("giu", userID + mobileNumber + email + name + address + str_date + str_time);

                        sqLiteDB db = new sqLiteDB(CounterRequest.this);
                        List<RateModel> jobs = db.getAddCart();
                        JSONArray jsonArray = new JSONArray();
                        JSONArray jsonArray1 = new JSONArray();
                        JSONObject job1 = null;
                        for (RateModel model : jobs) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("jobID", model.getJob_id());
                            jsonObject1.put("jobItemsID", model.getJob_id());
                            jsonObject1.put("jobQunty", model.getJobQunty());
                            jsonArray.put(jsonObject1);
                        }
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("uid", userID);
                        jsonObject2.put("delivery_date", str_date);
                        jsonObject2.put("delivery_time", str_time);
                        jsonObject2.put("remark", "PickupID - " + pickupID + "Remark-" + str_remark);
                        Log.e("fdfdf=====> ", String.valueOf(jsonObject2));
                        pickuRequestService(jsonObject2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });
    }

    private void pickuRequestService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(CounterRequest.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.COUNTER_REQUSET, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("counter response===>", String.valueOf(response));
                        pd.dismiss();
                        try {
                            boolean status = response.getBoolean("error");
                            if (!status) {
                                ConterRequsetsuccessMessage();

                            } else {
                                Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_LONG).show();
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);


    }

    private void ConterRequsetsuccessMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Your pickup request Successfully submitted in My Counter List section...!");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(CounterRequest.this, MainActivity.class));
                        CounterRequest.this.finish();
                        sqLiteDB db = new sqLiteDB(CounterRequest.this);
                        db.deleteCartTable();
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
            cuTime = timeText.getText().toString().substring(0, 4);
            cuTime = String.valueOf(hourOfDay + ":" + minute + ":" + getAMPMValue);
            Log.e("CurrentTime", cuTime);//this text here pass params*/ CURRNT TIME HERE workinng code


            sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("Time", String.valueOf(hourOfDay + ":" + minute + ":" + getAMPMValue));
            editor.apply();

            switch (cuTime) {
                case "0:0:a.m":
                    //Toast.makeText(getActivity(),"12:00 a.m",Toast.LENGTH_SHORT).show();
                    timeText.setText("12:00 a.m");
                    break;

                case "0:30:a.m":
                    //Toast.makeText(getActivity(),"12:30 a.m",Toast.LENGTH_SHORT).show();
                    timeText.setText("12:30 a.m");
                    break;

            }

        }
    };
}





