package com.jld.test;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdjustTimeReceiver extends BroadcastReceiver {
    public static final String ADJUST_TIME_ACTION = "jld_adjust_time";
    private static final String TAG = "AdjustTimeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String time = intent.getStringExtra("adjust_time");
        if (action.equals(ADJUST_TIME_ACTION) && !TextUtils.isEmpty(time)) {
            Log.d(TAG, "time: " + Long.parseLong(time));
            String lTime = ms2Date(Long.parseLong(time));
            String[] tiems = lTime.split(":");
            setDate(context, Integer.parseInt(tiems[0]), Integer.parseInt(tiems[1]), Integer.parseInt(tiems[2]));
            setTime(context, Integer.parseInt(tiems[3]), Integer.parseInt(tiems[4]));
        }
    }

    public static String ms2Date(long _ms) {
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    static void setDate(Context context, int year, int month, int day) {
        Log.d(TAG, "setDate: " + year);
        Log.d(TAG, "setDate: " + month);
        Log.d(TAG, "setDate: " + day);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    static void setTime(Context context, int hourOfDay, int minute) {
        Log.d(TAG, "setTime: " + hourOfDay);
        Log.d(TAG, "setTime: " + minute);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
}
