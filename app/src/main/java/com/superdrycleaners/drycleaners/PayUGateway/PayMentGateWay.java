package com.superdrycleaners.drycleaners.PayUGateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.activities.Offers;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PayMentGateWay extends Activity {
    private ArrayList<String> post_val = new ArrayList<String>();
    private String post_Data = "";
    WebView webView;
    final Activity activity = this;
    private String tag = "PayMentGateWay";
    private String hash, hashSequence;
    ProgressDialog progressDialog;

//    String merchant_key="zBxSQi"; // live
//    String salt="ZhraT96O"; // live

    String merchant_key = "g2yvix"; // test chnage=g2yvix
    String salt = "SDrCWacB"; // test SDrCWacB
    String action1 = "";
    String base_url = "https://secure.payu.in";
    //https://secure.payu.in
    //String base_url="https://secure.payu.in";//
    int error = 0;
    String hashString = "";
    Map<String, String> params;
    String txnid = "";

    String SUCCESS_URL = "https://www.payumoney.com/mobileapp/payumoney/success.php"; // failed
    String FAILED_URL = "https://www.payumoney.com/mobileapp/payumoney/failure.php";

    Handler mHandler = new Handler();
    String userID;
    String Offer_id;
    String mobileNumber;
    String Offer_Amt;
    String activityType;


    static String getFirstName, getNumber, getEmailAddress, getRechargeAmt;


    ProgressDialog pDialog;

    @SuppressLint({"JavascriptInterface", "WrongConstant", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(activity);

        Intent oIntent = getIntent();

        activityType = oIntent.getExtras().getString("ActivityType");
        getFirstName = oIntent.getExtras().getString("FIRST_NAME");
        getNumber = oIntent.getExtras().getString("PHONE_NUMBER");
        getEmailAddress = oIntent.getExtras().getString("EMAIL_ADDRESS");
        getRechargeAmt = oIntent.getExtras().getString("RECHARGE_AMT");


        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");
        final String name = sp.getString(ConfigClass.NAME, "");
        final String email = sp.getString(ConfigClass.EMAIL, "");
        final String address = sp.getString(ConfigClass.ADDRESS, "");
        mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
        Offer_Amt = sp.getString("offer_amount", null);
        Offer_id = sp.getString("id", null);
        final String pincode = sp.getString(ConfigClass.PINCODE, "");
        Log.e("data", name + email + address + mobileNumber + pincode);
        Log.e("data", Offer_Amt + Offer_id + userID + mobileNumber);


        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        webView = new WebView(this);
        setContentView(webView);

        params = new HashMap<String, String>();
        params.put("key", merchant_key);
        if (activityType.equals("Bill")) {
            String billAmount = sp.getString("BillAmount", "");
            params.put("amount", billAmount);
        } else {
            params.put("amount", Offer_Amt);
        }

        params.put("firstname", getFirstName);
        params.put("email", getEmailAddress);
        params.put("phone", getNumber);
        params.put("productinfo", "Recharge Wallet");
        params.put("surl", SUCCESS_URL);
        params.put("furl", FAILED_URL);
        params.put("service_provider", "payUbiz");
        params.put("lastname", "");
        params.put("address1", "");
        params.put("address2", "");
        params.put("city", "");
        params.put("state", "");
        params.put("country", "");
        params.put("zipcode", "");
        params.put("udf1", "");
        params.put("udf2", "");
        params.put("udf3", "");
        params.put("udf4", "");
        params.put("udf5", "");
        params.put("pg", "");

        if (empty(params.get("txnid"))) {
            Random rand = new Random();
            String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
            txnid = hashCal("SHA-256", rndm).substring(0, 20);
            params.put("txnid", txnid);
        } else
            txnid = params.get("txnid");
        //String udf2 = txnid;
        String txn = "abcd";
        hash = "";
        String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
        if (empty(params.get("hash")) && params.size() > 0) {
            if (empty(params.get("key"))
                    || empty(params.get("txnid"))
                    || empty(params.get("amount"))
                    || empty(params.get("firstname"))
                    || empty(params.get("email"))
                    || empty(params.get("phone"))
                    || empty(params.get("productinfo"))
                    || empty(params.get("surl"))
                    || empty(params.get("furl"))
                    || empty(params.get("service_provider"))

            ) {
                error = 1;
            } else {
                String[] hashVarSeq = hashSequence.split("\\|");

                for (String part : hashVarSeq) {
                    hashString = (empty(params.get(part))) ? hashString.concat("") : hashString.concat(params.get(part));
                    hashString = hashString.concat("|");
                }
                hashString = hashString.concat(salt);
                hash = hashCal("SHA-512", hashString);
                action1 = base_url.concat("/_payment");


            }
        } else if (!empty(params.get("hash"))) {
            hash = params.get("hash");
            action1 = base_url.concat("/_payment");
        }
        webView.setWebViewClient(new MyWebViewClient() {
            public void onPageFinished(WebView view, final String url) {
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

        });
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(2);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);

        //webView.addJavascriptInterface(new PayUJavaScriptInterface(getApplicationContext()), "PayUMoney");
        webView.addJavascriptInterface(new PayUJavaScriptInterface(), "PayUMoney");
        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("key", merchant_key);
        mapParams.put("hash", PayMentGateWay.this.hash);
        mapParams.put("txnid", (empty(PayMentGateWay.this.params.get("txnid"))) ? "" : PayMentGateWay.this.params.get("txnid"));
        Log.d(tag, "txnid: " + PayMentGateWay.this.params.get("txnid"));
        mapParams.put("service_provider", "payUbiz");

        mapParams.put("amount", (empty(PayMentGateWay.this.params.get("amount"))) ? "" : PayMentGateWay.this.params.get("amount"));
        mapParams.put("firstname", (empty(PayMentGateWay.this.params.get("firstname"))) ? "" : PayMentGateWay.this.params.get("firstname"));
        mapParams.put("email", (empty(PayMentGateWay.this.params.get("email"))) ? "" : PayMentGateWay.this.params.get("email"));
        mapParams.put("phone", (empty(PayMentGateWay.this.params.get("phone"))) ? "" : PayMentGateWay.this.params.get("phone"));

        mapParams.put("productinfo", (empty(PayMentGateWay.this.params.get("productinfo"))) ? "" : PayMentGateWay.this.params.get("productinfo"));
        mapParams.put("surl", (empty(PayMentGateWay.this.params.get("surl"))) ? "" : PayMentGateWay.this.params.get("surl"));
        mapParams.put("furl", (empty(PayMentGateWay.this.params.get("furl"))) ? "" : PayMentGateWay.this.params.get("furl"));
        mapParams.put("lastname", (empty(PayMentGateWay.this.params.get("lastname"))) ? "" : PayMentGateWay.this.params.get("lastname"));

        mapParams.put("address1", (empty(PayMentGateWay.this.params.get("address1"))) ? "" : PayMentGateWay.this.params.get("address1"));
        mapParams.put("address2", (empty(PayMentGateWay.this.params.get("address2"))) ? "" : PayMentGateWay.this.params.get("address2"));
        mapParams.put("city", (empty(PayMentGateWay.this.params.get("city"))) ? "" : PayMentGateWay.this.params.get("city"));
        mapParams.put("state", (empty(PayMentGateWay.this.params.get("state"))) ? "" : PayMentGateWay.this.params.get("state"));

        mapParams.put("country", (empty(PayMentGateWay.this.params.get("country"))) ? "" : PayMentGateWay.this.params.get("country"));
        mapParams.put("zipcode", (empty(PayMentGateWay.this.params.get("zipcode"))) ? "" : PayMentGateWay.this.params.get("zipcode"));
        mapParams.put("udf1", (empty(PayMentGateWay.this.params.get("udf1"))) ? "" : PayMentGateWay.this.params.get("udf1"));
        mapParams.put("udf2", (empty(PayMentGateWay.this.params.get("udf2"))) ? "" : PayMentGateWay.this.params.get("udf2"));

        mapParams.put("udf3", (empty(PayMentGateWay.this.params.get("udf3"))) ? "" : PayMentGateWay.this.params.get("udf3"));
        mapParams.put("udf4", (empty(PayMentGateWay.this.params.get("udf4"))) ? "" : PayMentGateWay.this.params.get("udf4"));
        mapParams.put("udf5", (empty(PayMentGateWay.this.params.get("udf5"))) ? "" : PayMentGateWay.this.params.get("udf5"));
        mapParams.put("pg", (empty(PayMentGateWay.this.params.get("pg"))) ? "" : PayMentGateWay.this.params.get("pg"));
        webview_ClientPost(webView, action1, mapParams.entrySet());

    }

    private final class PayUJavaScriptInterface {

        PayUJavaScriptInterface() {

        }

        @JavascriptInterface
        public void success(String id, final String paymentId) {

            Log.e("PayU Response", "ID-" + id + "PaymentID-" + paymentId);
            mHandler.post(new Runnable() {
                public void run() {
                    mHandler = null;
                    Log.e("PayU Response", "");
                    Toast.makeText(getApplicationContext(), "Successfully payment" + getFirstName, Toast.LENGTH_LONG).show();

                    if (activityType.equals("Bill")) {
                        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                        String userID = sp.getString(ConfigClass.USERID, "");
                        String name = sp.getString(ConfigClass.NAME, "");
                        String mobile = sp.getString(ConfigClass.MOBILE_NO, "");
                        String billNo = sp.getString("BillNo", "");
                        String p_staus = sp.getString("pay_statu", null);
                        String p_id = sp.getString("ide", null);
                        String billAmount = sp.getString("BillAmount", "");
                        String walletAmount = sp.getString("WalletAmount", "");
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("uid", userID);
                            jsonObject.put("bill_no", billNo);
                            jsonObject.put("mobile_no", mobile);
                            jsonObject.put("user_name", name);
                            jsonObject.put("p_status", p_staus);
                            jsonObject.put("p_id", p_id);
                            jsonObject.put("wallet_amount", walletAmount);
                            jsonObject.put("extra_amount", billAmount);

                            getStatusService(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_id", userID);
                            jsonObject.put("offer_id", Offer_id);
                            jsonObject.put("pay_status", "Success");
                            Log.e("Payment Status===> ", jsonObject.toString());
                            sendDataPaymentRespone(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }


            });
        }

        @JavascriptInterface
        public void failure(final String id, String error) {
            Log.e("Payment Failed-", "ID-" + id + ",Error-" + error);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            });
        }

        @JavascriptInterface
        public void failure() {
            Log.e("Payment ", "Failure");
            failure("");
        }

        @JavascriptInterface
        public void failure(final String params) {
            Log.e("Payment ", "Failure");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Payment ", "Failure");
                }

            });

        }


    }

    private void sendDataPaymentRespone(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(PayMentGateWay.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Processing Please Wait...!");
        pd.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.ORDER_UPDATE, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Payment Status Res===> ", String.valueOf(response));
                try {
                    if (!response.getBoolean("error")) {
                        Intent intent = new Intent(PayMentGateWay.this, Offers.class);
                        startActivity(intent);
                        pd.dismiss();
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
                Log.e("errorresponse===> ", String.valueOf(error));
                pd.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();


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

    private void getStatusService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(PayMentGateWay.this);
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
                            Toast.makeText(PayMentGateWay.this, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PayMentGateWay.this, "No Connection", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(PayMentGateWay.this, "Authentication Failure", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    Toast.makeText(PayMentGateWay.this, "Empty!", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NetworkError) {
                    Toast.makeText(PayMentGateWay.this, "Network Error", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(PayMentGateWay.this, "Not Parsed", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("PaymentGateway", "request code " + requestCode + " resultcode " + resultCode);

    }


    public void webview_ClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");
        Log.d(tag, "webview_ClientPost called");

        //setup and load the progress bar
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading. Please wait...");
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }

    public void success(long id, final String paymentId) {

        mHandler.post(new Runnable() {
            public void run() {
                mHandler = null;
                postDataBackend();

                Toast.makeText(getApplicationContext(), "Successfully payment\n redirect from Success Function", Toast.LENGTH_LONG).show();

            }

            private void postDataBackend() {
                final ProgressDialog pd = new ProgressDialog(PayMentGateWay.this);
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("Please Wait...!");
                pd.show();
                Toast.makeText(getApplicationContext(), "POST DETA", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public boolean empty(String s) {
        if (s == null || s.trim().equals(""))
            return true;
        else
            return false;
    }

    public String hashCal(String type, String str) {
        byte[] hashseq = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();


            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);

            }

        } catch (NoSuchAlgorithmException nsae) {
        }

        return hexString.toString();


    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (url.startsWith("http")) {
                // Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                // progressDialog.show();
                view.loadUrl(url);
                System.out.println("myresult " + url);
                //return true;
            } else {
                return false;
            }

            return true;
        }

    }

    private void sendErrorPaymentRespone(JSONObject data) {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.ORDER_UPDATE, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Payment Status Res===> ", String.valueOf(response));

             /*   try {
                    if (!response.getBoolean("error")) {
                        Intent intent = new Intent(PayMentGateWay.this, Offers.class);
                        startActivity(intent);
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errorresponse===> ", String.valueOf(error));
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();


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
    public void onBackPressed() {
        super.onBackPressed();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("offer_id", Offer_id);
            jsonObject.put("pay_status", "Failed");

            Log.e("Payment Status===> ", jsonObject.toString());
            sendErrorPaymentRespone(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}









