package com.superdrycleaners.drycleaners.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.superdrycleaners.drycleaners.PayUGateway.PayMentGateWay;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private String userID;
    private double purchaseAmount, offerAmount, totalAmount;
    private TextView billAmount;
    private TextView finalAmount;
    private double payableAmount;
    private RadioButton walletOption, otherOption;
    private Button payAction;
    private double restAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = findViewById(R.id.walletToolbar);
        billAmount = findViewById(R.id.billAmountTV);
        finalAmount = findViewById(R.id.finalAmountTV);
        walletOption = findViewById(R.id.walletRB);
        otherOption = findViewById(R.id.otherRB);
        payAction = findViewById(R.id.payButton);
        setTitle("Payment");
        checkConnection();
        setSupportActionBar(toolbar);
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        String amount = sp.getString("PayableAmount", "0");
        payableAmount = Double.parseDouble(amount);
        billAmount.setText(" Bill Amount Rs." + " " + String.format("%.2f", payableAmount));


        walletOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAmount.setText("");
                calculatePayableAmount();
            }
        });

        otherOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAmount.setText("Payable Amount - Rs." + " " + String.format("%.2f", payableAmount));
            }
        });

        payAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount();
            }
        });


        JSONObject offerAmount = new JSONObject();
        try {
            offerAmount.put("uid", userID);
            offer_amount(offerAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void payAmount() {
        if (walletOption.isChecked()) {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            if (totalAmount >= payableAmount) {
                String userID = sp.getString(ConfigClass.USERID, "");
                String name = sp.getString(ConfigClass.NAME, "");
                String mobile = sp.getString(ConfigClass.MOBILE_NO, "");
                String billNo = sp.getString("BillNo", "");
                String p_staus = sp.getString("pay_statu", null);
                String p_id = sp.getString("ide", null);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", userID);
                    jsonObject.put("bill_no", billNo);
                    jsonObject.put("mobile_no", mobile);
                    jsonObject.put("user_name", name);
                    jsonObject.put("p_status", p_staus);
                    jsonObject.put("p_id", p_id);
                    jsonObject.put("wallet_amount", payableAmount);
                    jsonObject.put("extra_amount", "0");

                    getStatusService(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SharedPreferences.Editor editor = sp.edit();
                userID = sp.getString(ConfigClass.USERID, "");
                String name = sp.getString(ConfigClass.NAME, "");
                String address = sp.getString(ConfigClass.ADDRESS, "");
                String mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
                editor.putString("BillAmount", "" + restAmount);
                editor.putString("WalletAmount", "" + totalAmount);
                editor.apply();
                Intent intent = new Intent(WalletActivity.this, PayMentGateWay.class);
                intent.putExtra("FIRST_NAME", name);
                intent.putExtra("PHONE_NUMBER", mobileNumber);
                intent.putExtra("EMAIL_ADDRESS", address);
                intent.putExtra("RECHARGE_AMT", "0");
                intent.putExtra("ActivityType", "Bill");
                startActivity(intent);
                WalletActivity.this.finish();
            }
        } else {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            userID = sp.getString(ConfigClass.USERID, "");
            String name = sp.getString(ConfigClass.NAME, "");
            String address = sp.getString(ConfigClass.ADDRESS, "");
            String mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
            editor.putString("BillAmount", "" + payableAmount);
            editor.putString("WalletAmount", "0");
            editor.apply();
            Intent intent = new Intent(WalletActivity.this, PayMentGateWay.class);
            intent.putExtra("FIRST_NAME", name);
            intent.putExtra("PHONE_NUMBER", mobileNumber);
            intent.putExtra("EMAIL_ADDRESS", address);
            intent.putExtra("RECHARGE_AMT", "0");
            intent.putExtra("ActivityType", "Bill");
            startActivity(intent);
            WalletActivity.this.finish();


        }
    }

    private void calculatePayableAmount() {
        double amount = 0.0;
        if (payableAmount > totalAmount) {
            restAmount = payableAmount - totalAmount;
            finalAmount.setText("Payable Amount - Rs." + " " + String.format("%.2f", restAmount));
        }

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(getApplicationContext());
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
            String userID = sp.getString(ConfigClass.USERID, "");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void purchese_amount(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(WalletActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.PURCHASE_AMOUNT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pd.dismiss();
                Log.e("PurchaseResponse===> ", String.valueOf(response));
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray purchaseArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObject1 = purchaseArray.getJSONObject(0);
                        String purchase_amt = jsonObject1.optString("purchase_amount");

                        if (!purchase_amt.equals("null")) {
                            purchaseAmount = Double.parseDouble(purchase_amt);
                        } else {
                            purchaseAmount = 0.0;
                        }

                        calculateAmount();

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
                Log.e("responseError===> ", String.valueOf(error));


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

    public void calculateAmount() {
        totalAmount = offerAmount - purchaseAmount;
        walletOption.setText("Wallet - Rs." + " " + String.format("%.2f", totalAmount));
    }

    private void offer_amount(JSONObject data) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.PRO_OFFER_AMOUNT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("OfferResponse===> ", String.valueOf(response));
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray pro_offer_Array = jsonObject.getJSONArray("data");
                        JSONObject jsonObject1 = pro_offer_Array.getJSONObject(0);
                        String ofrAmount = jsonObject1.optString("offer_amount");
                        if (!ofrAmount.equals("null")) {
                            offerAmount = Double.parseDouble(ofrAmount);
                        } else {
                            offerAmount = 0.0;
                        }
                        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("offer_amount", String.valueOf(offerAmount));
                        editor.apply();


                        JSONObject purchaceAmt = new JSONObject();
                        purchaceAmt.put("uid", userID);
                        purchese_amount(purchaceAmt);


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
                Log.e("responseError===> ", String.valueOf(error));

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


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        JSONObject offerAmut = new JSONObject();
        try {
            offerAmut.put("uid", userID);
            offer_amount(offerAmut);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getStatusService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(WalletActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.PAYMENT_STATUS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("PaymentStatus", String.valueOf(response));
                        pd.dismiss();
                        try {
                            Toast.makeText(WalletActivity.this, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(WalletActivity.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(WalletActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(WalletActivity.this, "Empty!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(WalletActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(WalletActivity.this, "Not Parsed", Toast.LENGTH_SHORT).show();
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
}