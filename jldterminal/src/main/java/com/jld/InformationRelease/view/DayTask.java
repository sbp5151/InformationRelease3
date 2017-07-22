package com.jld.InformationRelease.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jld.InformationRelease.base.DayTaskItem;

import java.util.ArrayList;

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

    public static final long s24HOUR = 1000 * 60 * 60 * 24;

    /**
     * 创建任务定时器
     * @param context
     * @param items
     */
    public static void createDayTask(Context context, ArrayList<DayTaskItem> items) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        for (int i = 0; i < items.size(); i++) {
            Intent intent = new Intent(context, MainActivity.DayTaskReceiver.class);
            intent.setAction("load_program");
            PendingIntent sender =
                    PendingIntent.getBroadcast(context, 0, intent, 0);
            //每天执行一次任务的StartTime
            alarm.setRepeating(AlarmManager.RTC_WAKEUP,Long.parseLong(items.get(i).getStateTime()),s24HOUR,sender);
        }
    }
}
