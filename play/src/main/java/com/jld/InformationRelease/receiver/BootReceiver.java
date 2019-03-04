package com.jld.InformationRelease.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jld.InformationRelease.util.L;
import com.jld.InformationRelease.view.SplashActivity;

/**
 * 开机自启
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d("BootReceiver:" + intent.getAction());
        Intent intent1 = new Intent(context, SplashActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
