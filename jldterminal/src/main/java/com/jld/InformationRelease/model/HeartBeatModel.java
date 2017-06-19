package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/1 14:01
 */
public class HeartBeatModel {

    private final Retrofit mRetrofit;
    private final HeartBeatService mService;

    public HeartBeatModel(Context context) {
        mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mService = mRetrofit.create(HeartBeatService.class);
    }

    public void sendHeartBeat(String deviceId, IPresenterToModel<BaseResponse> callBack, int requestTag) {
        mService.send_heart_beat(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<BaseResponse>(callBack, requestTag));
    }
}
