package com.jld.InformationRelease.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.request.UpdateTimeRequest;
import com.jld.InformationRelease.presenter.UpdateTimePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;

import static com.jld.InformationRelease.view.MainActivity.UPDATE_TIME_REQUEST;

public class UpdateTimeService extends Service implements IViewToPresenter {

    private SharedPreferences mSp;
    private boolean isUpdateTime = true;

    public UpdateTimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        new Thread(updateTimeRun).start();
    }

    Runnable updateTimeRun = new Runnable() {
        @Override
        public void run() {
            while (isUpdateTime) {
                UpdateTimePresenter presenter = new UpdateTimePresenter(UpdateTimeService.this, UpdateTimeService.this);
                String deviceId = mSp.getString(Constant.DEVICE_ID, "");
                long timeMillis = System.currentTimeMillis();
                String sign = MD5Util.getMD5(Constant.S_KEY + timeMillis + deviceId);
                UpdateTimeRequest body = new UpdateTimeRequest(deviceId, timeMillis + "",sign);
                presenter.updateTime(body, UPDATE_TIME_REQUEST);
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUpdateTime = false;
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(Object data, int requestTag) {

    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }
}
