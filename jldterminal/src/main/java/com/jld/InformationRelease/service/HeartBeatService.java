package com.jld.InformationRelease.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.presenter.HeartBeatPresenter;
import com.jld.InformationRelease.util.Constant;

/**
 * 在线心跳包发送服务
 */
public class HeartBeatService extends Service implements IViewToPresenter<BaseResponse> {


    private HeartBeatPresenter mPresenter;
    private String mDeviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        mPresenter = new HeartBeatPresenter(this, this);
        mDeviceId = getSharedPreferences(Constant.share_key, MODE_PRIVATE).getString(Constant.DEVICE_ID, "");
    }

    public HeartBeatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void send_heart_beat(){
//        mPresenter.send_heart_beat(mDeviceId,SEND_HEART_TAG);
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {

    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }
}
