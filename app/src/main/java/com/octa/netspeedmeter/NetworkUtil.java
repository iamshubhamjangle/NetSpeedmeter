package com.octa.netspeedmeter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtil {
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;
    private static int TYPE_WIFI = 1;

    private static int getConnectivityStatus(Context context) {
        @SuppressLint("WrongConstant") NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == 1) {
                return TYPE_WIFI;
            }
            if (activeNetwork.getType() == 0) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        if (conn == TYPE_WIFI) {
            return "wifi_enabled";
        }
        if (conn == TYPE_MOBILE) {
            return "mobile_enabled";
        }
        if (conn == TYPE_NOT_CONNECTED) {
            return "no_connection";
        }
        return null;
    }

    @SuppressLint("WrongConstant")
    public static List<String> getConnectivityInfo(Context context) {
        List<String> connInfo = new ArrayList();

        @SuppressLint("WrongConstant")

        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        String network_status = getConnectivityStatusString(context);

        if (network_status == "wifi_enabled") {
            connInfo.add("wifi_enabled");
            @SuppressLint("WrongConstant") WifiInfo info = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
            int percentage = (int) ((((double) WifiManager.calculateSignalLevel(info.getRssi(), 10)) / 9.0d) * 100.0d);
            connInfo.add(info.getSSID());
            connInfo.add(Integer.toString(percentage) + " %");
        } else if (network_status.equals("mobile_enabled")) {
            connInfo.add("mobile_enabled");
            connInfo.add(((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName());
        } else {
            connInfo.add("no_connection");
        }
        return connInfo;
    }
}
