package com.superdrycleaners.drycleaners.App;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class CheckForUpdate {
    public static final String ACTION_APP_VERSION_CHECK="app-version-check";

    public static void launchPlayStoreApp(Context context)
    {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=SSR+Info+Soft&hl=en" + appPackageName)));
        }

    }

    public static int getRemoteVersionNumber(Context context)
    {
        int versionCode=0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode=pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


}
