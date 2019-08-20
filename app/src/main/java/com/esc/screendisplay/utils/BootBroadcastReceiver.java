package com.esc.screendisplay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.esc.screendisplay.MainActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    private Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_BOOT)){
            //开机启动完成后，要做的事情
            Log.i(TAG, "BootBroadcastReceiver onReceive()!");
//            Toast.makeText(context, "BootBroadcastReceiver onReceive()!", Toast.LENGTH_SHORT).show();
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent2 = new Intent(context, MainActivity.class);
//                    intent2.setAction("android.intent.action.MAIN");
//                    intent2.addCategory("android.intent.category.LAUNCHER");
//                    //下面这句话必须加上才能实现开机自动运行app的界面
//                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent2);
//                }
//            });

            PackageManager pm = context.getPackageManager();    //包管理者
            Intent it = new Intent();                       //意图
            it = pm.getLaunchIntentForPackage("com.esc.screendisplay");   //值为应用的包名
            if (null != it)
            {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);         //启动意图
            }
        }
    }
}
