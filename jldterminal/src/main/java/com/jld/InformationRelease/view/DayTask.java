package com.jld.InformationRelease.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.receiver.DayTaskReceiver;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/21 19:58
 */
public class DayTask {

    private static final long s24HOUR = 1000 * 60 * 60 * 24;

    private static final String TAG = "DayTask---";
    private static AlarmManager sAlarm;
    private static PendingIntent sPendingIntent;

    /**
     * 创建任务定时器
     *
     * @param context
     * @param items
     */
    public static void createDayTask(Context context, ArrayList<DayTaskItem> items) {

        if (sAlarm == null)
            sAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        else if (sPendingIntent != null) {//取消定时器
            sAlarm.cancel(sPendingIntent);
        }
        for (int i = 0; i < items.size(); i++) {
            Intent intent = new Intent(context, DayTaskReceiver.class);
            intent.setAction("load_program");
            //FLAG_CANCEL_CURRENT:如果当前系统中已经存在一个相同的PendingIntent对象，那么就将先将已有的PendingIntent取消，然后重新生成一个PendingIntent对象。
            /**
             *  1.如果声明的triggerAtMillis是一个过去的时间，闹钟将会立即被触发。
             *　2.如果已经有一个相同intent的闹钟被设置过了，那么前一个闹钟将会取消，被新设置的闹钟所代替。
             */
            sPendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(items.get(i).getProgramLocalId()),
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);

            String startTime = items.get(i).getStateTime();
            LogUtil.d(TAG, "startTime:" + startTime);
            String[] split = startTime.split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
            calendar.set(Calendar.SECOND, Integer.parseInt(split[2]));
            calendar.set(Calendar.MILLISECOND, 0);

            //每天执行一次任务的StartTime
            LogUtil.d(TAG, "calendar:" + calendar.getTimeInMillis());
            sAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (1000 * 5), s24HOUR, sPendingIntent);
        }
    }

    public static void alarmTask(Context context) {
        LogUtil.d(TAG, "alarmTask:" + System.currentTimeMillis());
        sAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, DayTaskReceiver.class);
        sPendingIntent = PendingIntent.getBroadcast(context, 312,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //每天执行一任务的StartTime
        LogUtil.d(TAG, "DayTaskReceiver:开启定时任务：" + TimeUtil.getTodayDateTime());
        sAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 5), s24HOUR, sPendingIntent);
    }
}
