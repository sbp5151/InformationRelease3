package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.interfaces.IPresenterToModel;
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
 * @create-time 2017/5/11 16:59
 */
public class VerifyCodeModel {

    private final UserModelService mUserService;
    public VerifyCodeModel(Context context) {
        Retrofit retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mUserService = retrofit.create(UserModelService.class);
    }


    /**
     * 获取验证码请求
     *
     * @param body       请求参数
     * @param callback   结果回调
     * @param requestTag 请求标识
     */
    public void retrofitVerifyCode(VerifyCodeRequestBean body, final IPresenterToModel<VerifyCodeResponseBean> callback, final int requestTag) {
        mUserService.getVerifyCode(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<VerifyCodeResponseBean>(callback,requestTag));

    }
}
