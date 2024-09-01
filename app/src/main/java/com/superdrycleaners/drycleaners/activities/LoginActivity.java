package com.superdrycleaners.drycleaners.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;


public class LoginActivity extends AppCompatActivity {

    CheckBox selectCheckBox;
    boolean loginStatus;
    AppCompatEditText email_text;
    AppCompatEditText password_text;
    String email, pass;
    AlertDialog alertDialog;
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        selectCheckBox = findViewById(R.id.checkBox);

        LoadTermAndCondition();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViewController();


    }

    private void initViewController() {
        findViewById(R.id.new_userSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_app_updates, null);
                TextView textView = mView.findViewById(R.id.termAndConditionTV);
                Spanned htmlAsSpanned = Html.fromHtml(content);
                textView.setText(htmlAsSpanned);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                }

                mBuilder.setView(mView);

                mBuilder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                        overridePendingTransition(R.anim.enter, R.anim.exit);


                    }
                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                alertDialog = mBuilder.create();
                alertDialog.show();

                if (getDialogStatus()) {

                    //Toast.makeText(getApplicationContext(),"Terms",Toast.LENGTH_SHORT).show();

                } else {
                    alertDialog.show();
                }
            }

            private void storeDialogStatus(boolean isChecked) {
                SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("item", isChecked);
                mEditor.apply();
            }

            private boolean getDialogStatus() {
                SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
                return mSharedPreferences.getBoolean("item", false);
            }


        });

        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDailog();
            }
        });


        email_text = findViewById(R.id.email_text);
        password_text = findViewById(R.id.password_text);

        findViewById(R.id.login_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_text.getText().toString().trim();
                pass = password_text.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", pass);
                editor.apply();
                Log.e("saveCredential", email + pass);


                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter First Email or Mobile Number.!", Toast.LENGTH_LONG).show();
                } else if (pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter First Password.!", Toast.LENGTH_LONG).show();
                } else {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("mobile_no", email);
                        jsonObject.put("password", pass);
                        Log.e("Data===> ", jsonObject.toString());
                        loginService(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    private void LoadTermAndCondition() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.TERM_CONDITION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e("TermCondition", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray bnArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < bnArray.length(); i++) {
                            JSONObject jsonObject1 = bnArray.getJSONObject(i);
                            String type = jsonObject1.getString("type");
                            if (type.equals("register")) {
                                content = jsonObject1.getString("content");
                                break;
                            }
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }


    private void loginService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.LOGIN, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response===> ", String.valueOf(response));
                        pd.dismiss();
                        try {
                            boolean status = response.getBoolean("error");
                            if (!status) {
                                SharedPreferences.Editor editor = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE).edit();
                                editor.putString(ConfigClass.USERID, response.getString("uid"));
                                JSONObject jobj = response.getJSONObject("data");
                                editor.putString(ConfigClass.NAME, jobj.getString("name"));
                                editor.putString(ConfigClass.EMAIL, jobj.getString("email"));
                                editor.putString(ConfigClass.MOBILE_NO, jobj.getString("mobile_no"));
                                editor.putString(ConfigClass.ADDRESS, jobj.getString("address"));
                                editor.putString(ConfigClass.PINCODE, jobj.getString("pincode"));
                                editor.putString(ConfigClass.USER_CODE, jobj.getString("user_code"));
                                editor.putString(ConfigClass.USER_LOCATION, jobj.getString("location"));
                                editor.putBoolean(ConfigClass.LOGINSTATUS, true);
                                editor.apply();

//                                String isRedeemed = jobj.getString("coupon_status");
//                                if (!isRedeemed.equals("Y")) {
//                                    showCustomDialog();
//                                } else {}
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    LoginActivity.this.finish();


                            } else {
                                Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    private void showCustomDialog() {
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        final View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.main_dialog_layout, viewGroup, false);

                        AppCompatButton button_ok = (AppCompatButton) dialogView.findViewById(R.id.buttonOk);
                        button_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                LoginActivity.this.finish();


                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setView(dialogView);
                        alertDialog = builder.create();
                        alertDialog.show();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("responseError===> ", String.valueOf(error));
                Toast.makeText(getApplicationContext(), "Login credentials are wrong. Please try again!", Toast.LENGTH_LONG).show();
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


    private void forgotPasswordDailog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.forgot_pass_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);

        final AppCompatEditText enterNumber = dialog.findViewById(R.id.entNumber);
        TextView submit_BTN = dialog.findViewById(R.id.submit_BTN);

        submit_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg_number = Objects.requireNonNull(enterNumber.getText()).toString().trim();

                if (reg_number.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Register Number..!", Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("mobile_no", reg_number);

                        forgotPassService(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }


            }
        });

        dialog.findViewById(R.id.close_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void forgotPassService(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, ConfigClass.FORGOTPASS, data,
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

    private void successMessage() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Your password will be sent to registered no. Please check sms.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onChecked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        //Check which checkbox was clicked

        if (view.getId() == R.id.checkBox) {
            if (checked) {
                email = Objects.requireNonNull(email_text.getText()).toString().trim();
                pass = Objects.requireNonNull(password_text.getText()).toString().trim();
                Log.e("SaveCredential", email + pass);
                //Toast.makeText(getApplicationContext(), "checked", Toast.LENGTH_SHORT).show();
                email_text.setText(email);
                password_text.setText(pass);

            } else {
                // Toast.makeText(getApplicationContext(), "Not checked", Toast.LENGTH_SHORT).show();


            }
        }
    }
}

