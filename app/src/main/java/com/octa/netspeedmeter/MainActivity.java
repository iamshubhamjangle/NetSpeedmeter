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
    TextView textViewdownload, textViewUpload, textViewInfo;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        doubleBackToExitPressedOnce = false;

        startButton = findViewById(R.id.buttonStart);
        textViewdownload = findViewById(R.id.download);
        textViewUpload =  findViewById(R.id.upload);
        textViewInfo = findViewById(R.id.textViewInfo);

        this.dataThread = new Thread(new MyThreadClass());
        this.dataThread.setName("showSpeed");
        this.dataThread.start();
    }

    private final class MyThreadClass implements Runnable {
        public void run() {
            int noOfLoop = 0;

            synchronized (this) {
                while (MainActivity.this.dataThread.getName().equals("showSpeed")) {
                    Log.d("MyTAG", String.valueOf(noOfLoop));
                    getData();
                    try {
                        wait(1000);
                        noOfLoop++;
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
        //long totalData = mDownload + mUpload;

        if (network_status!="no_connection") {
            showSpeed(mDownload, mUpload);
        }else{
            Log.d("MyTAG", "NO NETWORK");
        }
    }

    public void showSpeed(long mDownload, long mUpload) {
        List<String> connStatus = NetworkUtil.getConnectivityInfo(getApplicationContext());

        String network_name;

        if ((connStatus.get(0)).equals("wifi_enabled")) {
            // (connStatus(1)) is network name and (connStatus(2)) is the ssid
            network_name = "WIFI strength " + (connStatus.get(2));
        } else if ((connStatus.get(0)).equals("mobile_enabled")) {
            network_name = connStatus.get(1);
        } else {
            network_name = "";
        }

        DecimalFormat df = new DecimalFormat("#.##");
        String downSpeed, upSpeed;


        if (mUpload < 128) {
            upSpeed = ((int) mUpload) + " B/s";
        } else if (mUpload < 1048576) {
            upSpeed = (((int) mUpload) / 1024) + " KB/s";
        } else {
            upSpeed = df.format(((double) mUpload) / 1048576.0d) + " MB/s";
        }

        if (mDownload < 128) {
            downSpeed = ((int) mDownload) + " B/s";
        } else if (mDownload < 1048576) {
            downSpeed = (((int) mDownload) / 1024) + " KB/s";
        } else {
            downSpeed = df.format(((double) mDownload) / 1048576.0d) + " MB/s";
        }

        Log.d("MyTAG", String.format("%s %s %s", downSpeed, upSpeed, network_name));
//        textViewdownload.setText(downSpeed);
//        textViewUpload.setText(upSpeed);
//        textViewInfo.setText(network_name);
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
