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

    public static final String TAG = "TimeUtil";

    /**
     * 时间转日期
     *
     * @param time
     * @return
     */
    public static String timeAddDate(String time) {
        String todayDateTime = TimeUtil.getTodayDateTime();
        String[] splitTime = todayDateTime.split(" ");
        time = splitTime[0] + " " + time;
        return time;
    }

    /**
     * time1 比 time2 大返回true 否则返回false
     * 时间格式  00:00:00
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean timeCompare(String time1, String time2) {

        String[] time1s = time1.split(":");
        String[] time2s = time2.split(":");
        for (int i = 0; i < time1s.length; i++) {
            if (Integer.parseInt(time1s[i]) > Integer.parseInt(time2s[i])) {
                return true;
            } else if (Integer.parseInt(time1s[i]) < Integer.parseInt(time2s[i])) {
                return false;
            }
        }
        return false;
    }

    /**
     * 计算时间差值
     *
     * @param timeFirst 时间1 日期格式
     * @param timeLast  时间2 日期格式
     * @return
     */
    public static long getTimeGap(String timeFirst, String timeLast) {
        long timegap = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        if (timeFirst.getBytes().length == 13)
            timeFirst = timeFirst.substring(0, 10);
        if (timeLast.getBytes().length == 13)
            timeLast = timeLast.substring(0, 10);

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
     * @param time 日期格式
     * @return
     */
    public static long toCurrentTimeGap(String time) {
//        String currentTime = getTodayDateTime();
//        return getTimeGap(time, currentTime);
        return System.currentTimeMillis()-Long.parseLong(time);
    }

    /**
     * 获取当前时间
     *
     * @return 日期格式
     */
    public static String getTodayDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * 日期格式转时间戳格式 例如（"2014-06-14 16-09-00"）返回时间戳
     *
     * @param time 日期格式
     * @return 时间戳格式
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
     * 时间戳格式转日期格式 例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time 时间戳格式
     * @return 日期格式
     */
    public static String dateFormat(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc * 1000L));
        return times;
    }
}
