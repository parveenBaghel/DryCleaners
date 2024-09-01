package com.superdrycleaners.drycleaners.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

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

public class SignUpActivity extends AppCompatActivity {
  public static final String MYPREF = "MyPref";
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor editor;
  RelativeLayout sign_btn;
  AlertDialog dialog;
  String device_id, userCode;
  final Context context = this;
  private Button button;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // requestWindowFeature(Window.FEATURE_NO_TITLE);
    // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_sign_up);
    sign_btn = (RelativeLayout) findViewById(R.id.sign_btn);

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

    sharedPreferences=getSharedPreferences("Pref",MODE_PRIVATE);
    device_id=sharedPreferences.getString("device ID",null);
    Log.e("device ID", device_id);

   SharedPreferences sp1 = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
    String userID = sp1.getString(ConfigClass.USERID, "");
    userCode = sp1.getString("user_code" ,"");
    Log.e("responseError===> " , userCode+userID);


    initViewController();


  }


  public void initViewController() {


    final AppCompatEditText email = findViewById(R.id.emailID);
    final AppCompatEditText mobile = findViewById(R.id.mobileNumber);
    final AppCompatEditText password = findViewById(R.id.password);
    final AppCompatEditText fullName = findViewById(R.id.fullName);
    final AppCompatEditText pinNumber = findViewById(R.id.pinNumber);
    final AppCompatEditText address = findViewById(R.id.adress);
    final AppCompatEditText refer=findViewById(R.id.refer_code_id);


    findViewById(R.id.signup_BTN).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {


        String str_email = email.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_password = password.getText().toString().trim();
        String str_fullName = fullName.getText().toString().trim();
        String str_pinNumber = pinNumber.getText().toString().trim();
        String str_address = address.getText().toString().trim();
        String str_refer=refer.getText().toString().trim();

        if (str_email.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Email", Toast.LENGTH_LONG).show();
        } else if (str_mobile.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Mobile", Toast.LENGTH_LONG).show();
        } else if (str_password.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Password", Toast.LENGTH_LONG).show();
        } else if (str_fullName.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Full Name", Toast.LENGTH_LONG).show();
        } else if (str_pinNumber.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Pin Number", Toast.LENGTH_LONG).show();
        } else if (str_address.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Enter First Address", Toast.LENGTH_LONG).show();
        } else {

          JSONObject jsonObject = new JSONObject();

          try {
            jsonObject.put("mobile_no", str_mobile);
            jsonObject.put("email", str_email);
            jsonObject.put("password", str_password);
            jsonObject.put("name", str_fullName);
            jsonObject.put("address", str_address);
            jsonObject.put("pincode", str_pinNumber);
            jsonObject.put("device_id", device_id);
            jsonObject.put("refer_code", str_refer);
            Log.e("Data===> ", jsonObject.toString());
            signUpService(jsonObject);
          } catch (JSONException e) {
            e.printStackTrace();
          }


        }

      }
    });


  }
  private void signUpService(JSONObject data) {
    final ProgressDialog pd = new ProgressDialog(SignUpActivity.this);
    pd.setCancelable(false);
    pd.setCanceledOnTouchOutside(false);
    pd.setMessage("Please Wait...!");
    pd.show();

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.POST, ConfigClass.SIGNUP, data,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                try {
                  boolean status = response.getBoolean("error");
                  if (!status) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#E04034")); //any color your want
                    toast.show();

                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    SignUpActivity.this.finish();
                  }else{
                    Toast.makeText(getApplicationContext(),""+response.getString("message"),Toast.LENGTH_SHORT).show();
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }

              }
            }, new Response.ErrorListener() {

      @Override
      public void onErrorResponse(VolleyError error) {
        pd.dismiss();
        //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "User Already Exist..!", Toast.LENGTH_LONG).show();
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

  public void onCheckedTerms(View view) {
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(SignUpActivity.this);
    View mView = getLayoutInflater().inflate(R.layout.dialog_app_updates, null);
    final CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
            /*mBuilder.setTitle("What's new in Super Dry Cleaner");
            mBuilder.setMessage("1.All articles accepted at customer’s risk. \n2- No Guarantee of stain’s removel,colour fade,colour spill, Embroidery colour,  Beads,Buttons,Pasting Damages or any kind of wear & tear, No claim/compensation in any case only repair,if possible will be done. " +
                    "\n3-In the event of loss, misplacement interchange or damage the firm will have the option of either replacing or repairing the articles.The liability of the firm for the loss or injury of any kind shall be one fourth of the price of articles. The price of that goods in such cases will be determined according to the market value and articles will be resumed by the firm." +
                    "\n4-Articles that remain unclaimed by customers upto 2 months from the date of delivery, will be donated to the needful. No claims & compensation in that case." +
                    "\n5-The firm does not hold responsibility of anything left in the pocket of the articles.");
*/
    mBuilder.setView(mView);

    mBuilder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {

        //linearLayout.setBackgroundColor(Color.parseColor("#413E3E"));
        //dialogInterface.dismiss();
        //Toast.makeText(getApplicationContext(),"check ok",Toast.LENGTH_SHORT).show();

      }
    });

    dialog = mBuilder.create();
    dialog.show();
    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4c4c4c")));

    mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
          //Toast.makeText(getApplicationContext(),"checked",Toast.LENGTH_SHORT).show();


        }else{
          storeDialogStatus(false);
          Toast.makeText(getApplicationContext(),"Please check & Agree",Toast.LENGTH_SHORT).show();
          //mDialog.dismiss();
          SignUpActivity.super.finish();
        }
      }

    });

    if(getDialogStatus()){

      //Toast.makeText(getApplicationContext(),"Terms",Toast.LENGTH_SHORT).show();

    }else{
      dialog.show();
    }
  }

  private void storeDialogStatus(boolean isChecked){
    SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
    mEditor.putBoolean("item", isChecked);
    mEditor.apply();
  }

  private boolean getDialogStatus(){
    SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
    return mSharedPreferences.getBoolean("item", false);
  }


}






