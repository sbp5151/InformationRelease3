package com.jld.InformationRelease.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.jld.InformationRelease.R;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/13 11:24
 */
public class VolumeUtil {

    public static int getCurrentVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //当前音量
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getMaxVolume(Context context) {
        //音量控制,初始化定义
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //最大音量
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setVolume(Context context, int volume) {
        if (volume > getMaxVolume(context) || volume < 0) {
            Toast.makeText(context,context.getString(R.string.volume_error),Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("setVolume","volume:"+volume);
        //音量控制,初始化定义
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //最大音量
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0); //tempVolume:音量绝对值
    }
}
