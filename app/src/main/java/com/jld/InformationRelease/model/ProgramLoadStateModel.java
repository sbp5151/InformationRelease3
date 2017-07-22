package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver;
import com.jld.InformationRelease.bean.request_bean.PushStateRequest;
import com.jld.InformationRelease.bean.response_bean.ProgramPushStateResponse;
import com.jld.InformationRelease.interfaces.IPresenterListen;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
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
 * @create-time 2017/7/4 10:06
 */
public class ProgramLoadStateModel {

    private Context mContext;
    private final ProgramLoadStateService mStateService;

    public ProgramLoadStateModel(Context context) {
        mContext = context;
        Retrofit retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mStateService = retrofit.create(ProgramLoadStateService.class);
    }

    public void loadState(String programId, final IPresenterListen<ProgramPushStateResponse> callback, final int requestTag) {
        String md5 = MD5Util.getMD5(Constant.S_KEY + programId);
        PushStateRequest body = new PushStateRequest(md5, programId);
        mStateService.loadState(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ProgramPushStateResponse>(callback, requestTag) {
                    @Override
                    public void onNext(ProgramPushStateResponse value) {
                        if (value != null && value.getResult().equals("0")) {//成功
                            callback.requestSuccess(value, requestTag);
                        } else if (value != null) {//失败
                            callback.requestError(new Exception(value.getMsg()), requestTag);
                        } else {//错误
                            callback.requestError(new Exception("获取数据错误，请重试！"), requestTag);
                        }
                    }
                });
    }
}
