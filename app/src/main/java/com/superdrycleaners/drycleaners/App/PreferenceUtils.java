package com.superdrycleaners.drycleaners.App;

import android.content.Context;
import android.content.SharedPreferences;

import com.superdrycleaners.BuildConfig;

public class PreferenceUtils {
    // this is for version code
    private  final String APP_VERSION_CODE = "APP_VERSION_CODE";
    private SharedPreferences sharedPreferencesAppVersionCode;
    private SharedPreferences.Editor editorAppVersionCode;
    private static Context mContext;
    String versionName = BuildConfig.VERSION_NAME;
    int versionCode = BuildConfig.VERSION_CODE;

    public PreferenceUtils(Context context)
    {
        this.mContext=context;
        // this is for app versioncode
        sharedPreferencesAppVersionCode=mContext.getSharedPreferences(APP_VERSION_CODE,Context.MODE_APPEND);
        editorAppVersionCode=sharedPreferencesAppVersionCode.edit();
    }

    public void createAppVersionCode(int versionCode) {

        editorAppVersionCode.putInt(APP_VERSION_CODE, versionCode);
        editorAppVersionCode.apply();
    }

    public int getAppVersionCode()
    {
        return sharedPreferencesAppVersionCode.getInt(APP_VERSION_CODE,0); // as default  version code is 0
    }
}

