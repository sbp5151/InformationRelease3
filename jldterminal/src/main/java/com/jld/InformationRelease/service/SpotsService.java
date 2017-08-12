package com.jld.InformationRelease.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;

/**
 * Created by boping on 2017/8/12.
 */

public class SpotsService extends Service {

    private SpotsBinder mMyBind;
    private OnSportStopListen mOnSportStopListen;
    public static final int DURATION_STOP = 0x01;
    public static final String TAG = "SpotsService";
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DURATION_STOP:
                    mOnSportStopListen.onProgramStop();
                    break;
            }
        }
    };
    private String mStartTime;
    private String mStopTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mMyBind == null)
            mMyBind = new SpotsBinder();
        return mMyBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isGetTime = false;
        mHandler.removeMessages(DURATION_STOP);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

  public class SpotsBinder extends Binder {
        public void setOnSportStopListen(OnSportStopListen onSportStopListen) {
            mOnSportStopListen = onSportStopListen;
        }

        /**
         * 插播 时间段
         */
        public void setPeriodData(String startTime, String stopTime) {
            mStartTime = startTime;
            mStopTime = stopTime;
            isGetTime = false;
            mHandler.postDelayed(timeRun, 1000 * 10);
        }

        /**
         * 插播 时长
         *
         * @param duration
         */
        public void setDuration(String duration) {
            try {
                int iDuration = Integer.parseInt(duration);
                mHandler.removeMessages(DURATION_STOP);
                mHandler.sendEmptyMessageDelayed(DURATION_STOP, iDuration * 60 * 1000);
                mOnSportStopListen.onProgramStart();
            } catch (Exception e) {

            }
        }
    }

    boolean isGetTime = true;
    boolean isStartTime = false;
    Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            isGetTime = true;
            isStartTime = false;
            while (isGetTime) {
                long curTime = System.currentTimeMillis();
                curTime = curTime / 1000;
                curTime = curTime * 1000;
                LogUtil.d(TAG, "当前系统时间:" + curTime);
                if (curTime >= Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(mStopTime)))) {
                    mOnSportStopListen.onProgramStop();
                    isGetTime = false;
                } else if (curTime >= Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(mStartTime))) && !isStartTime) {
                    mOnSportStopListen.onProgramStart();
                    isStartTime = true;
                }
                Sleep5s();
            }
        }
    };

    private void Sleep5s() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface OnSportStopListen {
        void onProgramStop();

        void onProgramStart();
    }
}
