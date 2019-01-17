package com.example.administrator.battery;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String TAG = "MainActivity";

    private static final int CODE_MULTI_PERMISSION = 300;

    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("电池电量");
//        builder.setMessage("电量:" + battery + "%");
//        builder.setNeutralButton("确定", null);
//        builder.create();
//        builder.show();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(mIntentReceiver, filter);

        requestMultiPermissions(MainActivity.this,new String[]{
                PERMISSION_READ_EXTERNAL_STORAGE,
                PERMISSION_WRITE_EXTERNAL_STORAGE});
    }

    /*接受屏幕亮灭的广播接收*/
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG,"屏幕点--灭--了");
                try {
                    FileUtil.writeFileContent(getTimeString() + "-off");
                }catch (IOException e){
                    Log.e(TAG,"write time fail");
                }

            }else if(action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(TAG,"屏幕点--亮--了");
                try {
                    FileUtil.writeFileContent(getTimeString()+"-on");
                }catch (IOException e){
                    Log.e(TAG,"write time fail");
                }
            }
        }
    };

    private String getTimeString(){
        String time = "";
        Calendar cal = Calendar.getInstance();
        String year = java.lang.String.valueOf(cal.get(Calendar.YEAR));
        String month = java.lang.String.valueOf(cal.get(Calendar.MONTH))+1;
        String day = java.lang.String.valueOf(cal.get(Calendar.DATE));
        String hour="";
        String minute="";
        String second="";
        if (cal.get(Calendar.AM_PM) == 0){
            hour = java.lang.String.valueOf(cal.get(Calendar.HOUR));
        }
        else {
            hour = java.lang.String.valueOf(cal.get(Calendar.HOUR) + 12);
        }
        minute = java.lang.String.valueOf(cal.get(Calendar.MINUTE));
        second = java.lang.String.valueOf(cal.get(Calendar.SECOND));
        time = year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-"+second;
        return time;
    }


    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity,String[] permission) {

        if (null != permission && permission.length > 0) {
            ActivityCompat.requestPermissions(activity, permission, CODE_MULTI_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"requestCode="+requestCode+","+permissions.length+","+grantResults.length);
        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult( permissions, grantResults);
            return;
        }
    }


    private void requestMultiResult( String[] permissions, int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (TextUtils.equals(permissions[i] ,PERMISSION_READ_EXTERNAL_STORAGE) ||
                        TextUtils.equals(permissions[i] ,PERMISSION_WRITE_EXTERNAL_STORAGE) ){
                    Toast.makeText(MainActivity.this,"sd 读写好了！",Toast.LENGTH_LONG).show();
                    FileUtil.createFile();
                }
            }
        }
    }

}
