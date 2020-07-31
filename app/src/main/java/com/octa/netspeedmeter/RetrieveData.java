package com.octa.netspeedmeter;

import android.net.TrafficStats;
import java.util.ArrayList;
import java.util.List;

public class RetrieveData {
    private static long totalDownload = 0;
    private static long totalDownload_n = 0;
    private static long totalUpload = 0;
    private static long totalUpload_n = 0;

    public static List<Long> findData() {
        List<Long> allData = new ArrayList();
        if (totalDownload == 0) {
            totalDownload = TrafficStats.getTotalRxBytes();
        }
        if (totalUpload == 0) {
            totalUpload = TrafficStats.getTotalTxBytes();
        }
        long newTotalDownload = TrafficStats.getTotalRxBytes();
        long incDownload = newTotalDownload - totalDownload;
        long newTotalUpload = TrafficStats.getTotalTxBytes();
        long incUpload = newTotalUpload - totalUpload;
        totalDownload = newTotalDownload;
        totalUpload = newTotalUpload;
        allData.add(incDownload);
        allData.add(incUpload);
        return allData;
    }

    public static long getNotificationData() {
        if (totalDownload_n == 0) {
            totalDownload_n = TrafficStats.getTotalRxBytes();
        }
        if (totalUpload_n == 0) {
            totalUpload_n = TrafficStats.getTotalTxBytes();
        }
        long newTotalDownload = TrafficStats.getTotalRxBytes();
        long incDownload = newTotalDownload - totalDownload_n;
        long newTotalUpload = TrafficStats.getTotalTxBytes();
        long incUpload = newTotalUpload - totalUpload_n;
        totalDownload_n = newTotalDownload;
        totalUpload_n = newTotalUpload;
        return incDownload + incUpload;
    }
}

