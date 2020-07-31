package com.octa.netspeedmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    public static boolean notification_status = true;
    Thread dataThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        doubleBackToExitPressedOnce = false;

        Button startButton = (Button) findViewById(R.id.buttonStart);
        TextView textViewdownload = (TextView) findViewById(R.id.download);
        TextView textViewUpload = (TextView) findViewById(R.id.upload);

        this.dataThread = new Thread(new MyThreadClass());
        this.dataThread.setName("showNotification");
        this.dataThread.start();
    }

    private final class MyThreadClass implements Runnable {
        public void run() {
            int i = 0;
            Log.d("MyTAG", String.valueOf(i));

            synchronized (this) {
                while (MainActivity.this.dataThread.getName().equals("showNotification")) {
                    getData();
                    try {
                        wait(1000);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void getData() {
        String network_status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
        Log.d("MyTAG", network_status);

        List<Long> allData = RetrieveData.findData();
        Long mDownload = allData.get(0);
        Long mUpload = allData.get(1);
        long receiveData = mDownload + mUpload;

        if (network_status!="no_connection") {
            showNotification(receiveData);
        }else{
            Log.d("MyTAG", "0kbps");
        }
    }

    public void showNotification(long receiveData) {
        List<String> connStatus = NetworkUtil.getConnectivityInfo(getApplicationContext());

        String network_name;

        if ((connStatus.get(0)).equals("wifi_enabled")) {
            network_name = (connStatus.get(1)) + " " + (connStatus.get(2));
        } else if ((connStatus.get(0)).equals("mobile_enabled")) {
            network_name = connStatus.get(1);
        } else {
            network_name = "";
        }

        DecimalFormat df = new DecimalFormat("#.##");
        String speed;
        if (receiveData < 128) {
            speed = "Speed " + ((int) receiveData) + " B/s" + " " + network_name;
        } else if (receiveData < 1048576) {
            speed = "Speed " + (((int) receiveData) / 1024) + " KB/s" + " " + network_name;
        } else {
            speed = "Speed " + df.format(((double) receiveData) / 1048576.0d) + " MB/s" + " " + network_name;
        }

        Log.d("MyTAG", speed);
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press Again To Exit...!", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 5000);
        }
    }
}
