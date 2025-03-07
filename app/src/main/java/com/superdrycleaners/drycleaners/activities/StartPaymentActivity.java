package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.App.ConnectivityReceiver;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.PayUGateway.PayMentGateWay;
import com.superdrycleaners.drycleaners.beans.OfferBannerModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartPaymentActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    EditText fname, pnumber, emailAddress, payAmt;
    Button Paynow;
    String userID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_payment_activity);
        checkConnection();


        fname = (EditText) findViewById(R.id.fname);
        pnumber = (EditText) findViewById(R.id.pnumber);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        payAmt = (EditText) findViewById(R.id.rechargeAmt);
        Paynow = (Button) findViewById(R.id.Paynow);

       /* SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        Log.e("data",userID);*/

        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        final String name = sp.getString(ConfigClass.NAME, "");
        final String email = sp.getString(ConfigClass.EMAIL, "");
        final String address = sp.getString(ConfigClass.ADDRESS, "");
        final String mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
        final String pincode = sp.getString(ConfigClass.PINCODE, "");
        final String Offer_Amt = sp.getString("offer_amount", null);
        final String Offer_id = sp.getString("id", null);
        final String Offer_Per = sp.getString("offer_per", null);

        Log.e("data", name + email + address + mobileNumber + pincode + Offer_Per + ",ID-" + Offer_id + "OfferAmt-" + Offer_Amt);


        //Log.e("data",Offer_Amt+Offer_id+userID);


        fname.setText(name);
        pnumber.setText(mobileNumber);
        emailAddress.setText(email);
        payAmt.setText(Offer_Amt);

        fname.setEnabled(false);
        pnumber.setEnabled(false);
        emailAddress.setEnabled(false);
        payAmt.setEnabled(false);


        Paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("offer_id", Offer_id);
                    jsonObject.put("name", name);
                    jsonObject.put("mobile", mobileNumber);
                    jsonObject.put("amount", Offer_Amt);
                    jsonObject.put("user_id", userID);
                    jsonObject.put("offer_per", Offer_Per);
                    requestService(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        showSnack(isConnected);

    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            String userID = sp.getString(ConfigClass.USERID, "");


        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
            //noConnection.setVisibility(View.VISIBLE);
        }
    }

    private void requestService(JSONObject data) {
        final List<OfferBannerModel> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(StartPaymentActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Processing Payment...!");
        pd.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.ORDER_PLACE, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Place Order res===> ", String.valueOf(response));
                String getPhone = pnumber.getText().toString().trim();
                String getEmail = emailAddress.getText().toString().trim();
                String getAmt = payAmt.getText().toString().trim();
                String getFname = fname.getText().toString().trim();
                try {
                    if (!response.getBoolean("error")) {
                        Intent intent = new Intent(getApplicationContext(), PayMentGateWay.class);
                        intent.putExtra("FIRST_NAME", getFname);
                        intent.putExtra("PHONE_NUMBER", getPhone);
                        intent.putExtra("EMAIL_ADDRESS", getEmail);
                        intent.putExtra("RECHARGE_AMT", getAmt);
                        intent.putExtra("ActivityType", "Recharge");
                        startActivity(intent);
                        StartPaymentActivity.this.finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("responseError===> ", String.valueOf(error));
                Toast.makeText(getApplicationContext(), ""+error.toString(), Toast.LENGTH_LONG).show();

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

    public void back(View view) {
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


    public void payment_finish(View view) {
        finish();
    }
}
