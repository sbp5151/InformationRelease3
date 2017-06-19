package com.jld.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartReceiver extends BroadcastReceiver {

    public static final String TAG = "StartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("install_and_start")) {
            Log.d(TAG,"收到启动请求:"+intent.getAction());
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//注意,不能少
            context.startActivity(intent2);
        }
    }
}
