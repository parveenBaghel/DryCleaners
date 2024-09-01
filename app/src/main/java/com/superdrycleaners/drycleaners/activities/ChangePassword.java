package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    ProgressDialog progressDialog;
    TextView btn_update_pass_id;
    EditText ed_newPassword, ed_confirmPassword;
    TextView my_profile_name_ids;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        btn_update_pass_id = findViewById(R.id.btn_update_pass_id);
        ed_newPassword = findViewById(R.id.ed_newPassword);
        ed_confirmPassword = findViewById(R.id.ed_confirmPassword);
        my_profile_name_ids = findViewById(R.id.my_profile_name_ids);


        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        final String userID = sp.getString(ConfigClass.USERID, "");
        final String name = sp.getString(ConfigClass.NAME, null);
        my_profile_name_ids.setText(name);


        btn_update_pass_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String new_pass = ed_newPassword.getText().toString();
                final String con_pass = ed_confirmPassword.getText().toString();
                Log.e("pass", userID + new_pass + con_pass);

                boolean cancel = false;
                View focusView = null;
                if (TextUtils.isEmpty(new_pass)) {
                    ed_newPassword.setError("New Password should not be empty! ");
                    focusView = ed_newPassword;
                    focusView.requestFocus();
                    cancel = true;
                    if (TextUtils.isEmpty(con_pass)) {
                        ed_confirmPassword.setError("Confirm Password should not be empty!");
                        focusView = ed_confirmPassword;
                        focusView.requestFocus();
                        cancel = true;
                    }
                } else {


                }
                if (con_pass.equals(new_pass)) {
                    ed_newPassword.getText().clear();
                    ed_confirmPassword.getText().clear();
                    final JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("user_id", userID);
                        jsonObject.put("password", new_pass);
                        change_password_service(jsonObject);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Mismatch Password try Again!", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    private void change_password_service(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(ChangePassword.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please Wait...!");
        pd.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, ConfigClass.UPDATE_PASSWORD, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response===> ", String.valueOf(response));
                pd.dismiss();
                try {
                    if (response.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_LONG).show();
                        ChangePassword.this.finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
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
        VolleySingleton.getInstance(

                getApplicationContext()).

                addToRequestQueue(objectRequest);

    }

    public void profile_finish(View view) {
        finish();
    }


}
