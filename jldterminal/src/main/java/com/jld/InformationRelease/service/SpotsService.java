package com.jld.InformationRelease.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;

/**
 * Created by boping on 2017/8/12.
 */

public class SpotsService extends Service implements IViewToPresenter<ProgramResponseBean> {

    private SpotsBinder mMyBind;
    private OnSportStopListen mOnSportStopListen;
    private static final int DURATION_STOP = 0x01;
    //时间
    private static final int DURATION_REQUEST = 0x10;
    //时间段
    private static final int PERIOD_REQUEST = 0x11;

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
    private DayTaskItem mSpotsData;
    private LoadProgramPresenter mPresenter;

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
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        mPresenter = new LoadProgramPresenter(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(ProgramResponseBean data, int requestTag) {
        if (requestTag == DURATION_REQUEST) {//时长
            int iDuration = Integer.parseInt(mSpotsData.getStateTime());
            mHandler.sendEmptyMessageDelayed(DURATION_STOP, iDuration * 60 * 1000);
            mOnSportStopListen.onProgramStart(data);
        } else if (requestTag == PERIOD_REQUEST) {//时间段
            mOnSportStopListen.onProgramStart(data);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }

    public class SpotsBinder extends Binder {
        public void setOnSportStopListen(OnSportStopListen onSportStopListen) {
            mOnSportStopListen = onSportStopListen;
        }

        public void setData(DayTaskItem spotsData) {
            mSpotsData = spotsData;
            if (mSpotsData.getType().equals("0")) {//按时
                String programId = mSpotsData.getProgramLocalId();
                //节目加载
                mPresenter.LoadProgram(programId, DURATION_REQUEST);
            } else if (mSpotsData.getType().equals("1")) {//按次 忽略

            } else if (mSpotsData.getType().equals("2")) {//时间段
                isGetTime = false;
                mHandler.postDelayed(timeRun, 1000 * 10);
            }
        }

        public void stopService() {
            mHandler.removeMessages(DURATION_STOP);
            isGetTime = false;
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
                if (curTime >= Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(mSpotsData.getStateTime())))) {
                    mOnSportStopListen.onProgramStop();
                    isGetTime = false;
                } else if (curTime >= Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(mSpotsData.getStopTime()))) && !isStartTime) {
                    String programId = mSpotsData.getProgramLocalId();
                    //节目加载
                    mPresenter.LoadProgram(programId, PERIOD_REQUEST);
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

        void onProgramStart(ProgramResponseBean data);
    }
}
