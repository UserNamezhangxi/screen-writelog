package com.example.administrator.battery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SETTING = 300;
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
        //requestPremission();
    }

    public void requestPremission(){
        AndPermission.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                       // FileUtil.createFile();
                    }
                })
                .start();

    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            FileUtil.createFile();
            Toast.makeText(MainActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING).show();
            }
        }
    };

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
        String time = "1970-01-01-00-00-00";
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
}
