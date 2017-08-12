package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.bean.request_bean.ChangeNickRequest;
import com.jld.InformationRelease.bean.request_bean.ChangePWRequestBean;
import com.jld.InformationRelease.bean.request_bean.FeedBackRequest;
import com.jld.InformationRelease.bean.request_bean.RegisterRequestBean;
import com.jld.InformationRelease.bean.request_bean.RetrievePWRequestBean;
import com.jld.InformationRelease.bean.request_bean.UserRequest;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
import com.jld.InformationRelease.interfaces.IPresenterListen;
import com.jld.InformationRelease.util.LogUtil;
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
    public static final String TAG = "UserModel";

    public UserModel(Context context) {
        Retrofit retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mUserService = retrofit.create(UserModelService.class);
    }

    /**
     * 登录请求
     *
     * @param body       请求参数
     * @param callBack   结果回调
     * @param requestTag 请求标识
     */
    public void retrofitLogin(UserRequest body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
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
    public void retrofitRegister(RegisterRequestBean body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
        LogUtil.d(TAG, "RegisterRequestBean:" + body);
        mUserService.register(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }

    /**
     * 找回密码
     *
     * @param body
     * @param callBack
     * @param requestTag
     */
    public void retrievePW(RetrievePWRequestBean body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
        LogUtil.d(TAG, "retrievePW:");
        mUserService.retrievePassword(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }

    /**
     * 密码修改
     *
     * @param body
     * @param callBack
     * @param requestTag
     */
    public void changePw(ChangePWRequestBean body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
        LogUtil.d(TAG, "changePw:");
        mUserService.changerPassword(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }

    /**
     * 用户名修改
     *
     * @param body
     * @param callBack
     * @param requestTag
     */
    public void changeNick(ChangeNickRequest body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
        LogUtil.d(TAG, "ChangeNick:");
        mUserService.changeNick(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }

    /**
     * 意见反馈
     * @param body
     * @param callBack
     * @param requestTag
     */
    public void feedBack(FeedBackRequest body, final IPresenterListen<UserResponse> callBack, final int requestTag) {
        LogUtil.d(TAG, "feedBack:");
        mUserService.feedBack(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<UserResponse>(callBack, requestTag));
    }
}
