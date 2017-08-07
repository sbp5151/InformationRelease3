package com.jld.InformationRelease.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.view.MainActivity;

import java.util.ArrayList;

public class DayTaskService extends Service implements IViewToPresenter<ProgramResponseBean> {

    private static final String TAG = "DayTaskService";
    private MyBinder mMyBinder;
    private boolean isGetTime = true;
    /**
     * 节目请求标识
     */
    private static final int LOAD_PROGRAM_REQUEST = 0x01;
    /**
     * 时间对比间隔 5s
     */
    private int timeInterval = 5000;
    private Handler mMainHandler;
    ArrayList<DayTaskItem> mItems;
    /**
     * 当前任务的节目ID
     */
    private String mProgramID;
    /**
     * 当前加载的节目ID
     */
    private String mLoadProgramID;

    public DayTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }


    public class MyBinder extends Binder {
        public void sendHandler(Handler handler) {
            mMainHandler = handler;
        }

        public void sendTaskTimes(ArrayList<DayTaskItem> items) {
            mItems = items;
            LogUtil.d(TAG, "每日任务需要匹配的数据：" + mItems);
        }

        public void stopLoop() {
            isGetTime = false;
        }

        public void startLoop() {
            isGetTime = true;
            Thread thread = new Thread(timeRun);
            thread.start();
        }
    }

    Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            while (isGetTime) {
                if (mMainHandler == null || mItems == null) {
                    Sleep3s();
                    LogUtil.d(TAG, "等待数据匹配:");
                    continue;
                }
                long curTime = System.currentTimeMillis();
                curTime = curTime / 1000;
                curTime = curTime * 1000;
                LogUtil.d(TAG, "当前系统时间:" + curTime);
                for (DayTaskItem item : mItems) {
                    if (curTime >= Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(item.getStateTime()))) &&
                            curTime < Long.parseLong(TimeUtil.dateBack(TimeUtil.timeAddDate(item.getStopTime())))) {
                        LogUtil.d(TAG, "加载每日任务节目时间:" + item.getStateTime());
                        LogUtil.d(TAG, "加载每日任务节目ID:" + item.getProgramLocalId());
                        mLoadProgramID = item.getProgramLocalId();

                        //当前节目ID为空或者匹配ID和当前节目ID不同，则加载节目
                        if (!TextUtils.isEmpty(mLoadProgramID) &&
                                (TextUtils.isEmpty(mProgramID) || !mLoadProgramID.equals(mProgramID)))
                            loadProgram(mLoadProgramID);
                        mProgramID = mLoadProgramID;
                    }
                }
                Sleep3s();
            }
        }
    };

    /**
     * 根据节目ID加载节目
     *
     * @param programID 节目ID
     */
    private void loadProgram(String programID) {
        LogUtil.d(TAG, "加载节目:" + programID);
        LoadProgramPresenter presenter = new LoadProgramPresenter(this, this);
        presenter.LoadProgram(programID, LOAD_PROGRAM_REQUEST);
    }

    @Override
    public void loadDataSuccess(ProgramResponseBean data, int requestTag) {
        if (requestTag == LOAD_PROGRAM_REQUEST) {
            if (mMainHandler != null && isGetTime) {//替换节目
                Message message = mMainHandler.obtainMessage();
                message.obj = data;
                message.what = MainActivity.REPLACE_FRAGMENT;
                mMainHandler.sendMessage(message);
                mProgramID = mLoadProgramID;
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        isGetTime = false;
        stopSelf();
        return super.onUnbind(intent);
    }

    private void Sleep3s() {
        try {
            Thread.sleep(timeInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        isGetTime = false;
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }
}
