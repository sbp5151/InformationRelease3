package com.jld.InformationRelease.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.StartActivity;

/**
 * 开机自启
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("BootReceiver:" + intent.getAction());
        Intent intent1 = new Intent(context, StartActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
