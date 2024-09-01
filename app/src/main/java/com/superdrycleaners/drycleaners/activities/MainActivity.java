package com.superdrycleaners.drycleaners.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
//import com.google.android.play.core.appupdate.AppUpdateInfo;
//import com.google.android.play.core.appupdate.AppUpdateManager;
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
//import com.google.android.play.core.install.InstallState;
//import com.google.android.play.core.install.InstallStateUpdatedListener;
//import com.google.android.play.core.install.model.AppUpdateType;
//import com.google.android.play.core.install.model.InstallStatus;
//import com.google.android.play.core.install.model.UpdateAvailability;
//import com.google.android.play.core.tasks.OnSuccessListener;
//import com.google.android.play.core.tasks.Task;
import com.superdrycleaners.R;

import com.superdrycleaners.drycleaners.Config.ConfigClass;
import com.superdrycleaners.drycleaners.adapters.HomeAdapter;
import com.superdrycleaners.drycleaners.adapters.ImageSliding_Adapter;
import com.superdrycleaners.drycleaners.adapters.MyAdapter;
import com.superdrycleaners.drycleaners.adapters.TitleAdapter;
import com.superdrycleaners.drycleaners.beans.Banner;
import com.superdrycleaners.drycleaners.beans.ModelClass;
import com.superdrycleaners.drycleaners.beans.TitleModel;
import com.superdrycleaners.drycleaners.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String url = "http://androidmkab.com/paperVersion.json";
    String VersionUpdate;
    private List<ModelClass> items;
    private HomeAdapter adapter;
    ImageSliding_Adapter sliding_adapter;

    private static int currentPage = 0;
    private static final Integer[] XMEN = {R.drawable.bnbnn};
    private final ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    List<TitleModel> titleList;
    ArrayList<Banner> bannerModels;

    ViewPager viewPager;
    List<Banner> sliderImg;

    private boolean loginStatus;
    public static final String MYPREF = "MyPref";
    public static final String USERID = "uid";
    ProgressDialog progressDialog;
    String userCode;
    DrawerLayout drawer;
    TextView walletAmount;
    double offer_amt, purchaseAmount, totalAmount;
    String userID;

    private static final int REQ_CODE_VERSION_UPDATE = 530;
//    private AppUpdateManager appUpdateManager;
//    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);

        sliderImg = new ArrayList<>();
        bannerModels = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.pager);
//        walletAmount = findViewById(R.id.walletAmountTV);


        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        loginStatus = sp.getBoolean(ConfigClass.LOGINSTATUS, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerViewController(toolbar);
        loadBanner();
        initViewPager();


        titleList = new ArrayList<>();
        titleList.add(new TitleModel("Pickup Request", R.drawable.his));
        titleList.add(new TitleModel("Delivery Request", R.drawable.his));
        titleList.add(new TitleModel("Rate Chart", R.drawable.ra));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        TitleAdapter titleAdapter = new TitleAdapter(this, titleList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(titleAdapter);


//        checkForAppUpdate();

        userID = sp.getString(ConfigClass.USERID, "");
        userCode = sp.getString(ConfigClass.USER_CODE, "");


//        JSONObject offerAmut = new JSONObject();
//        try {
//            offerAmut.put("uid", userID);
//            offer_amount(offerAmut);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    private void initViewPager() {
        for (int i = 0; i < XMEN.length; i++)
            XMENArray.add(XMEN[i]);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new MyAdapter(MainActivity.this, XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        Log.e("image_testing", ">>>>length>>>:" + XMEN.length);
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                Log.e("image_testing", ">>>>run>>>:");
                if (currentPage == sliderImg.size()) {
                    currentPage = 0;
                    Log.e("image_testing", ">>>>currentPage>>>:" + currentPage);
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
                Log.e("image_testing", ">>>>currentPage>>tt>:" + currentPage);
            }
        }, 2000, 2000);

    }

    private void loadBanner() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ConfigClass.BANNER_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e("bannerlist", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString("error");
                    if (error.equals("false")) {
                        JSONArray bnArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < bnArray.length(); i++) {
                            JSONObject jsonObject1 = bnArray.getJSONObject(i);
                            String banner_name = jsonObject1.getString("id");
                            String banner_image = jsonObject1.getString("banner_image");

                            Banner sliderUtils = new Banner(banner_image, banner_name);
                            sliderImg.add(sliderUtils);

                            sliding_adapter = new ImageSliding_Adapter(sliderImg, MainActivity.this);
                            viewPager.setAdapter(sliding_adapter);

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void purchese_amount(JSONObject data) {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
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

//                        calculateAmount();

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

    /*public void calculateAmount() {
        totalAmount = offer_amt - purchaseAmount;
        walletAmount.setText("Rs." + " " + String.format("%.2f", totalAmount));
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("WalletAmount",""+totalAmount);
        editor.apply();

    }*/

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
//        checkNewAppVersionState();
        SharedPreferences sp = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
        userID = sp.getString(ConfigClass.USERID, "");

        /*JSONObject offerAmut = new JSONObject();
        try {
            offerAmut.put("uid", userID);
            offer_amount(offerAmut);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                Log.d("Update flow Result: ", "" + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
//                unregisterInstallStateUpdListener();
            }
        }
    }

    @Override
    protected void onDestroy() {
//        unregisterInstallStateUpdListener();
        super.onDestroy();
    }


    private void drawerViewController(Toolbar toolbar) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                    MainActivity.this);
            alertDialogBuilder.setTitle("Confirm");
            alertDialogBuilder.setMessage("Are you sure want to Exit?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    }
            ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        }
    }

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_pickup:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, Pickup_Request_Activity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;
            case R.id.nav_pricing:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, RateChartCategoryList.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;

            case R.id.nav_pay_bill:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, PayYourBillActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
            case R.id.nav_profile:
                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;
            case R.id.nav_Pickuprequest:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, CounterRequest.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);


                break;
            case R.id.nav_delivery:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, CounterList.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);


                break;
            case R.id.nav_counter_Req:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, PickuRequestList_Activity.class).putExtra("pickup_id" , 0));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);


                break;
            case R.id.nav_counter:

                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, DeliveryListActivity.class));

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);


                break;
            /*case R.id.nav_history:
                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, Offers.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;*/
            /*case R.id.nav_coupon:
                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, Coupon.class));

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;*/
            /*case R.id.nav_offers:
                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, OrderHistory.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);

                break;*/
            case R.id.nav_termcondition:
                if (loginStatus) {
                    startActivity(new Intent(MainActivity.this, TermCondition_Activity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);


                break;
            case R.id.nav_logout:
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                        MainActivity.this);
                alertDialogBuilder.setTitle("Confirm");

                alertDialogBuilder.setMessage("Are you sure want to Logout?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = getSharedPreferences(ConfigClass.MYPREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                MainActivity.super.finish();
                           /* moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());

                        */
                            }
                        }
                ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


                break;
            case R.id.nav_share:

                if (loginStatus) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Super Dry Cleaners");
                    String app_url = "https://play.google.com/store/apps/details?id=com.superdrycleaners " + "Use Code=" + userCode + " " + "to sign up/invite";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, app_url);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


   /* private void checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdateAndUnregister();
            }
        };

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                        // Before starting an update, register a listener for updates.
                        appUpdateManager.registerListener(installStateUpdatedListener);
                        // Start an update.
                        MainActivity.this.startAppUpdateFlexible(appUpdateInfo);
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Start an update.
                        MainActivity.this.startAppUpdateImmediate(appUpdateInfo);
                    }
                }
            }
        });
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void startAppUpdateFlexible(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            unregisterInstallStateUpdListener();
        }
    }*/

    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private void popupSnackbarForCompleteUpdateAndUnregister() {
        Snackbar snackbar =
                Snackbar.make(drawer, getString(R.string.update_app), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.restart, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.black));
        snackbar.show();

//        unregisterInstallStateUpdListener();
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    /*private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        new OnSuccessListener<AppUpdateInfo>() {
                            @Override
                            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                //FLEXIBLE:
                                // If the update is downloaded but not installed,
                                // notify the user to complete the update.
                                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                    MainActivity.this.popupSnackbarForCompleteUpdateAndUnregister();
                                }

                                //IMMEDIATE:
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    MainActivity.this.startAppUpdateImmediate(appUpdateInfo);
                                }
                            }
                        });

    }*/

    /**
     * Needed only for FLEXIBLE update
     */
    /*private void unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
    }*/

}



