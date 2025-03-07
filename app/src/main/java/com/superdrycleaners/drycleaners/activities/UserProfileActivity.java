package com.superdrycleaners.drycleaners.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.App.ConnectivityReceiver;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.beans.PincodeModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private AppCompatEditText editName, edit_address, edit_email, edit_pincode, edit_pass, mobile_number;
    private String userID;
    TextView textView, textView1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MYPREF = "MyPref";
    public static final String LOGINSTATUS = "login_status";
    public static final String USERID = "uid";
    public static String SELECTED_PINCODE = "";
    public static String SELECTED_LOCATION = "";
    double offer_amt, purchaseAmount;
    String pur_amt;
    TextView change_pass_id;
    double totalAmount;
    ArrayAdapter<String> locationAdapter;
    ArrayList<PincodeModel> listOfPincodes = new ArrayList<PincodeModel>();
    ArrayList<PincodeModel> listOfLocations = new ArrayList<PincodeModel>();
    AppCompatSpinner pincodeSpinner;
    AppCompatSpinner locationSpinner;
    ArrayAdapter<String> pincodeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

        textView = findViewById(R.id.bal_amt);
        textView1 = findViewById(R.id.text);
        change_pass_id = findViewById(R.id.change_pass_id);
        pincodeSpinner = findViewById(R.id.pincodeSpinner);
        locationSpinner = findViewById(R.id.locationSpinner);

        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        String offamt = sp.getString("offer_amount", null);
        // Log.e("jkgnkrj",offamt);


        SharedPreferences.Editor editor = sp.edit();
        editor.putString("offer_amount", String.valueOf(offer_amt));
        editor.apply();

        userID = sp.getString(ConfigClass.USERID, "");


        /*JSONObject offerAmut = new JSONObject();
        try {
            offerAmut.put("uid", userID);
            offer_amount(offerAmut);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        checkConnection();
        viewController();
        change_pass_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, ChangePassword.class));

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


            JSONObject purchaceAmt = new JSONObject();
            JSONObject offerAmut = new JSONObject();
            try {
                offerAmut.put("uid", userID);
                purchaceAmt.put("uid", userID);
                purchese_amount(purchaceAmt);
                offer_amount(offerAmut);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();

        }
    }


    private void purchese_amount(JSONObject data) {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.PURCHASE_AMOUNT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                Log.e("responseError===> ", String.valueOf(error));
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();


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
        totalAmount = offer_amt - purchaseAmount;
        textView.setText("Rs." + " " + String.format("%.2f", totalAmount));
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
                            offer_amt = Double.parseDouble(ofrAmount);
                        } else {
                            offer_amt = 0.0;
                        }

                        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("offer_amount", String.valueOf(offer_amt));
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
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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

    private void getPincode() {
        final List<String> list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(UserProfileActivity.this);
        pd.setMessage("Please wait...!");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.GET_PINCODE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("OfferResponse===> ", String.valueOf(response));
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PincodeModel model = new PincodeModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setPincodeId(jsonObject1.getString("pincode"));
                            model.setLocation("");
                            listOfPincodes.add(model);
                            list.add(jsonObject1.getString("pincode"));
                        }
                        if (pincodeAdapter == null) {
                            pincodeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                            pincodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            pincodeSpinner.setAdapter(pincodeAdapter);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void getLocation(String pincode) {
        List<String> list = new ArrayList<String>();
        list.clear();
        final ProgressDialog pd = new ProgressDialog(UserProfileActivity.this);
        pd.setMessage("Please wait...!");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.GET_LOCATION + "?id=" + pincode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("OfferResponse===> ", String.valueOf(response));
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PincodeModel model = new PincodeModel();
                            model.setId(jsonObject1.getString("id"));
                            model.setPincodeId(jsonObject1.getString("pincode_id"));
                            model.setLocation(jsonObject1.getString("location"));
                            listOfLocations.add(model);
                            list.add(jsonObject1.getString("location"));
                        }


                        locationAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        locationSpinner.setAdapter(locationAdapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pd.dismiss();
                locationSpinner.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                locationSpinner.setVisibility(View.GONE);
                Log.e("responseError===> ", String.valueOf(error));
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void viewController() {
        final ScrollView showProfile_section = findViewById(R.id.showProfile_selction);
        final ScrollView edit_profile_section = findViewById(R.id.edit_profile_section);
        final AppCompatTextView edit_user_profile = findViewById(R.id.edit_user_profile);
        final AppCompatTextView save_profile = findViewById(R.id.save_profile);


        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        final String name = sp.getString(ConfigClass.NAME, "");
        final String email = sp.getString(ConfigClass.EMAIL, "");
        final String address = sp.getString(ConfigClass.ADDRESS, "");
        final String mobileNumber = sp.getString(ConfigClass.MOBILE_NO, "");
        final String pincode = sp.getString(ConfigClass.PINCODE, "");
        final String location = sp.getString(ConfigClass.USER_LOCATION, "");
        Log.e("ghijo", mobileNumber + pincode);
        SELECTED_PINCODE = pincode;
        SELECTED_LOCATION = location;

        findViewById(R.id.back_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.finish();
            }
        });

        findViewById(R.id.edit_user_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPincode();
                edit_profile_section.setVisibility(View.VISIBLE);
                showProfile_section.setVisibility(View.GONE);
                edit_user_profile.setVisibility(View.GONE);
                save_profile.setVisibility(View.VISIBLE);
                pincodeSpinner.setEnabled(true);
                locationSpinner.setEnabled(true);

                editProfileMethod(name, email, address, mobileNumber, pincode);
            }
        });

        findViewById(R.id.save_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  showProfile_section.setVisibility(View.VISIBLE);
                // edit_profile_section.setVisibility(View.GONE);
                // edit_user_profile.setVisibility(View.VISIBLE);
                // save_profile.setVisibility(View.GONE);

                saveProfileMethod(name, email, address, mobileNumber, SELECTED_PINCODE, SELECTED_LOCATION);

            }
        });

        pincodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_color));
                getLocation(listOfPincodes.get(position).getId());
                SELECTED_PINCODE = listOfPincodes.get(position).getPincodeId();
                Log.d("Spinner Pincode", listOfPincodes.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_color));
                SELECTED_LOCATION = listOfLocations.get(position).getLocation();
                Log.d("Location", "Location" + SELECTED_LOCATION);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AppCompatTextView name_text = findViewById(R.id.show_name);
        AppCompatTextView email_text = findViewById(R.id.show_email);
        AppCompatTextView address_text = findViewById(R.id.show_address);
        AppCompatTextView mobile_text = findViewById(R.id.show_number);
        AppCompatTextView pincode_text = findViewById(R.id.show_pincode);
        AppCompatTextView location_text = findViewById(R.id.show_location);

        name_text.setText(name);
        email_text.setText(email);
        address_text.setText(address);
        mobile_text.setText(mobileNumber);
        pincode_text.setText(pincode);
        location_text.setText(location);

    }


    @SuppressLint("WrongViewCast")
    private void editProfileMethod(String name, String email, String address, String mobileNumber, String pincode) {
        editName = findViewById(R.id.edit_name);
        edit_address = findViewById(R.id.edit_address);
        edit_email = findViewById(R.id.edit_email);
        edit_pincode = findViewById(R.id.edit_pincode);
        mobile_number = findViewById(R.id.mobile_number);

        editName.setText(name);
        edit_address.setText(address);
        edit_email.setText(email);
        edit_pincode.setText(pincode);
        mobile_number.setText(mobileNumber);
    }

    @SuppressLint("WrongViewCast")
    private void saveProfileMethod(String name, String email, String address, String mobileNumber, String pincode, String location) {
        editName = findViewById(R.id.edit_name);
        edit_address = findViewById(R.id.edit_address);
        edit_email = findViewById(R.id.edit_email);
        edit_pincode = findViewById(R.id.edit_pincode);
        mobile_number = findViewById(R.id.mobile_number);

       /* editName.setText(name);
        edit_address.setText(address);
        edit_email.setText(email);
        edit_pincode.setText(pincode);`
        mobile_number.setText(mobileNumber);*/

        String saveName = editName.getText().toString();
        String saveAddress = edit_address.getText().toString();
        String saveEmail = edit_email.getText().toString();
        String savePincode = edit_pincode.getText().toString();
        String saveNumber = mobile_number.getText().toString();

        Log.e("savename===> ", saveName);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_no", saveNumber);
            jsonObject.put("name", saveName);
            jsonObject.put("uid", userID);
            jsonObject.put("email", saveEmail);
            jsonObject.put("address", saveAddress);
            jsonObject.put("pincode", pincode);
            jsonObject.put("location", location);
            editProfileService(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void editProfileService(JSONObject data) {

        final ProgressDialog pd = new ProgressDialog(UserProfileActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        Log.e("data==> ", String.valueOf(data));
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.EDIT_PROFILE, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("response===> ", String.valueOf(response));
                        pd.dismiss();

                        try {
                            boolean status = response.getBoolean("error");
                            if (!status) {
                                successMessage();
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
                //  Toast.makeText(getApplicationContext(), "Login credentials are wrong. Please try again!", Toast.LENGTH_LONG).show();
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
        alertDialogBuilder.setMessage("Profile Updated Successfully...!");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences preferences = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                        UserProfileActivity.this.finish();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
