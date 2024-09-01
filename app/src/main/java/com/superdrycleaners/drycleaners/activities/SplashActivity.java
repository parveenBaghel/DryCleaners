package com.superdrycleaners.drycleaners.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.superdrycleaners.R;

public class SplashActivity extends Activity {
  public static final String MYPREF = "MyPref";
  SharedPreferences sharedPreferences;
  SharedPreferences.Editor editor;

  public static final String MyPREFERENCES2 = "MyPrefs" ;
  SharedPreferences sharedpreferences2;
  public boolean isFirstRun;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_splash);

    initViews();

    Thread background = new Thread() {
      public void run() {

        try {
          // Thread will sleep for 2 seconds
          sleep(5000);


          // After 2 seconds redirect to another intent
          Intent i = new Intent(SplashActivity.this, MainActivity.class);
          startActivity(i);
          overridePendingTransition(R.anim.enter, R.anim.exit);

          //Remove activity
          finish();
        } catch (Exception ignored) {
        }
      }
    };
    // start thread
    background.start();
  }

  private void initViews() {
    //Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT).show();
    // Get hardware and software information
       String information = getHardwareAndSoftwareInfo();
    Log.e("deviceInfo",information);

    sharedPreferences=getSharedPreferences("Pref",MODE_PRIVATE);
    editor=sharedPreferences.edit();
    editor.putString("device ID",Build.ID);
    editor.apply();
    Log.e("device ID",Build.ID);
    //checkFirstRun();
    //Toast.makeText(getApplicationContext(),information,Toast.LENGTH_SHORT).show();
  }

  private String getHardwareAndSoftwareInfo() {
    return getString(R.string.serial) + " " + Build.SERIAL + "\n" +
            getString(R.string.model) + " " + Build.MODEL + "\n" +
            getString(R.string.id) + " " + Build.ID + "\n" +
            getString(R.string.manufacturer) + " " + Build.MANUFACTURER + "\n" +
            getString(R.string.brand) + " " + Build.BRAND + "\n" +
            getString(R.string.type) + " " + Build.TYPE + "\n" +
            getString(R.string.user) + " " + Build.USER + "\n" +
            getString(R.string.base) + " " + Build.VERSION_CODES.BASE + "\n" +
            getString(R.string.incremental) + " " + Build.VERSION.INCREMENTAL + "\n" +
            getString(R.string.sdk) + " " + Build.VERSION.SDK + "\n" +
            getString(R.string.board) + " " + Build.BOARD + "\n" +
            getString(R.string.host) + " " + Build.HOST + "\n" +
            getString(R.string.fingerprint) + " " + Build.FINGERPRINT + "\n" +
            getString(R.string.versioncode) + " " + Build.VERSION.RELEASE;
  }

  public void checkFirstRun() {
    System.out.println("its in check first run");
    isFirstRun = getSharedPreferences("PREFERENCE2",  MODE_PRIVATE).getBoolean("isFirstRun", true);
    if (isFirstRun){
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
      getSharedPreferences("PREFERENCE2", MODE_PRIVATE)
              .edit()
              .putBoolean("isFirstRun", false)
              .apply();

    }
    else{
      //Toast.makeText(getApplicationContext(),"second",Toast.LENGTH_SHORT).show();
      startActivity(new Intent(SplashActivity.this, MainActivity.class));

    }
  }

}









