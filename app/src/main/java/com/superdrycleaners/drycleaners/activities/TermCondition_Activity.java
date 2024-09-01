package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class TermCondition_Activity extends AppCompatActivity {
    TextView textView;
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_condition_);

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
        textView = findViewById(R.id.terms_condition);
        LoadTermAndCondition();


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
                            if (type.equals("main")){
                                content = jsonObject1.getString("content");
                                Spanned htmlAsSpanned = Html.fromHtml(content);
                                textView.setText(htmlAsSpanned);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                                }
                                break;
                            }
                        }

                    }else{
                        Toast.makeText(TermCondition_Activity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
}
