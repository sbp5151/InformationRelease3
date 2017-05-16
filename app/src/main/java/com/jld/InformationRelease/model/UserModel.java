package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.bean.request_bean.RegisterRequestBean;
import com.jld.InformationRelease.bean.request_bean.UserRequest;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
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
 * @create-time 2017/4/12 18:02
 */
public class UserModel {

    private final UserModelService mUserService;

    public UserModel(Context context) {
        Retrofit  retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mUserService = retrofit.create(UserModelService.class);
    }

    /**
     * 登录请求
     *
     * @param body       请求参数
     * @param callBack   结果回调
     * @param requestTag 请求标识
     */
    public void retrofitLogin(UserRequest body, final IPresenterToModel<UserResponse> callBack, final int requestTag) {
        mUserService.login(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }

    /**
     * 注册请求
     *
     * @param body       请求参数
     * @param callBack   结果回调
     * @param requestTag 请求ID
     */
    public void retrofitRegister(RegisterRequestBean body, final IPresenterToModel<UserResponse> callBack, final int requestTag) {

        mUserService.register(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack,requestTag));
    }

}
