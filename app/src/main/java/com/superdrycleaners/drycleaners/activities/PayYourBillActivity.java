package com.superdrycleaners.drycleaners.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PayYourBillActivity extends AppCompatActivity {
    private String userID;
    private String amount;
    private double purchaseAmount, offerAmount, totalAmount;
    private EditText billAmount;
    private EditText remark;
    private double payableAmount;
    private Button payAction;
    private double walletAmount;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_your_bill);
        Toolbar toolbar = findViewById(R.id.payBillToolbar);
        billAmount = findViewById(R.id.amountEditText);
        remark = findViewById(R.id.remarkEditText);
        payAction = findViewById(R.id.payBillButton);
        setTitle("Pay your Bill");
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sharedPreferences.getString(ConfigClass.USERID, "");
        amount = sharedPreferences.getString("WalletAmount", "");
        if (!amount.isEmpty())
            walletAmount = Double.parseDouble(amount);

        Log.e("WalletAmount-",""+walletAmount);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        billAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!billAmount.getText().toString().trim().isEmpty()){
                    double amount = Double.parseDouble(billAmount.getText().toString().trim());
                    if (amount>walletAmount){
                        payAction.setClickable(false);
                        payAction.setEnabled(false);
                        billAmount.setError("Amount should be lower then wallet amount");
                    }else{
                        payAction.setClickable(true);
                        payAction.setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        payAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = sharedPreferences.getString(ConfigClass.NAME, "");
                String mobile = sharedPreferences.getString(ConfigClass.MOBILE_NO, "");
                String amount = billAmount.getText().toString().trim();
                String remarkText = remark.getText().toString().trim();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", userID);
                    jsonObject.put("bill_no", "");
                    jsonObject.put("mobile_no", mobile);
                    jsonObject.put("user_name", name);
                    jsonObject.put("p_status", "1");
                    jsonObject.put("amount", amount);
                    jsonObject.put("p_id", "");
                    jsonObject.put("wallet_amount", amount);
                    jsonObject.put("extra_amount", "0");
                    jsonObject.put("remark",remarkText);
                    getStatusService(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getStatusService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(PayYourBillActivity.this);
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
                            Toast.makeText(PayYourBillActivity.this, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PayYourBillActivity.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(PayYourBillActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(PayYourBillActivity.this, "Empty!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(PayYourBillActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(PayYourBillActivity.this, "Not Parsed", Toast.LENGTH_SHORT).show();
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