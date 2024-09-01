package com.superdrycleaners.drycleaners.App;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by akhilesh on 21-Dec-17.
 */

public class MyApplication extends Application {
  private RequestQueue requestQueue;
  private static MyApplication mInstance;

  @Override
  public void onCreate() {
    super.onCreate();
    mInstance = this;
  }

  public static synchronized MyApplication getInstance() {
    return mInstance;
  }


  public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
    ConnectivityReceiver.connectivityReceiverListener = listener;


  }

  public RequestQueue getRequestQueue() {
    if (requestQueue == null) {
      requestQueue = Volley.newRequestQueue(getApplicationContext());
      return requestQueue;
    }
    return requestQueue;
  }
  public void AddToRequestQueue(Request request, String tag){
    request.setTag(tag);
    getRequestQueue().add(request);
  }

  // call the all request matching with the givin tag

  public Void cancelAllRequests(String tag){
    getRequestQueue().cancelAll(tag);
    return null;
  }
}




