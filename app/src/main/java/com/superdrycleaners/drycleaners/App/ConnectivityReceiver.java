package com.superdrycleaners.drycleaners.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by akhilesh on 19-Feb-18.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
  public static ConnectivityReceiverListener connectivityReceiverListener;

  public ConnectivityReceiver(){
    super();
  }

  @Override
  public void onReceive(Context context, Intent arg1) {

    ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
    boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();

    if (connectivityReceiverListener!=null){
      connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
    }
  }

  public static boolean isConnected(Context context){
    ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//    ConnectivityManager cm=(ConnectivityManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
    return activeNetwork!=null && activeNetwork.isConnected();
  }

  public interface ConnectivityReceiverListener {
    void onNetworkConnectionChanged(boolean isConnected);
  }
}
