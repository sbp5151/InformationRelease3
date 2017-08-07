package com.jld.InformationRelease.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;

public class DayTaskReceiver extends BroadcastReceiver {

    private static final String TAG = "DayTaskReceiver";
    private static Handler mMianHandler;

    public DayTaskReceiver() {
    }

    public DayTaskReceiver(Handler mainHandler) {
        mMianHandler = mainHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "节目切换:" + TimeUtil.getTodayDateTime());
//        if (mMianHandler != null)
//            mMianHandler.sendEmptyMessage(MainActivity.DAY_TASK_START);

    }
}
