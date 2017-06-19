package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.model.HeartBeatModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/1 13:53
 */
public class HeartBeatPresenter extends BasePresenterImpl<BaseResponse> {

    private final HeartBeatModel mModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public HeartBeatPresenter(IViewToPresenter view, Context context) {
        super(view);
        mModel = new HeartBeatModel(context);
    }

    public void send_heart_beat(String deviceId, int requestTag) {
        mModel.sendHeartBeat(deviceId, this, requestTag);
    }
}
