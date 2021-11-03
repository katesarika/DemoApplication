package com.example.demoapplication;

import android.content.Context;
import android.net.ConnectivityManager;

public class DeviceManager {
    //Check Network Connection
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
