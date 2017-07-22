package com.jld.InformationRelease.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 14:45
 */
public class TimeUtil {

    /**
     * 时间转毫秒
     */
    public static final int IS_GRAB = 0;
    public static final int NO_START = 1;
    public static final int IS_END = 2;

    public static int compareTime(String realTime, String timeFirst, String timeLast) {
        int flag = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date real = null;
        Date d1 = null;
        Date d2 = null;
        try {
            real = df.parse(realTime);
            d1 = df.parse(timeFirst);
            d2 = df.parse(timeLast);

            if ((real.getTime() - d1.getTime()) < 0) {

                return NO_START;
            } else if ((real.getTime() - d1.getTime()) >= 0 && (d2.getTime() - real.getTime()) >= 0) {
                return IS_GRAB;
            } else {
                return IS_END;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return flag;
    }

    /**
     * 获取时间差距
     */
    public static long getTimeGap(String timeFirst, String timeLast) {
        long timegap = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(timeFirst);
            d2 = df.parse(timeLast);

            timegap = d2.getTime() - d1.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timegap;
    }

    /**
     * 与当前时间的差距
     *
     * @param time
     * @return
     */
    public static long toCurrentTimeGap(String time) {
        String currentTime = getTodayDateTime();
        return getTimeGap(time, currentTime);
    }


    /**
     * 获取当前时间
     * @return
     */
    public static String getTodayDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        return format.format(new Date());
    }
    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14 16-09-00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dateBack(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            times = String.valueOf(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String dateFormat(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc * 1000L));
        return times;
    }
}
