package com.jld.InformationRelease.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/12 17:02
 */
public class DeviceUtil {

    /**
     * 关机
     */
    public static void showDown() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启
     */
    public static void restart() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot "}); //关机
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 音量设置
     *
     * @param volume
     */
    public static void volumeSetting(Context context, int volume) {

        VolumeUtil.setVolume(context, volume);
    }

    /**
     * 定时开关机
     *
     * @param offTime
     * @param onTime
     */
    public static void timeOffOn(String offTime, String onTime) {

        // TODO: 2017/5/15实现定时开关机代码
    }


    /**
     * 获取当前截屏
     *
     * @return
     */
    public static boolean getScreens(Activity context, String savePath) throws IOException {

        View viewScreen = context.getWindow().getDecorView();
        viewScreen.setDrawingCacheEnabled(true);
        viewScreen.buildDrawingCache();
        Bitmap bitmap = viewScreen.getDrawingCache();
        Log.d("getScreens","bitmap:"+bitmap);
        FileOutputStream fos = new FileOutputStream(savePath);
        if(fos!=null){
            if(bitmap!=null){
                bitmap.compress(Bitmap.CompressFormat.PNG,90,fos);
                viewScreen.destroyDrawingCache();
                fos.flush();
                fos.close();
                return true;
            }
            fos.flush();
            fos.close();
        }
        return false;
    }
}
