package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.request_bean.UserRequest;
import com.jld.InformationRelease.bean.request_bean.RegisterRequestBean;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.model.UserModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/12 17:16
 * <p>
 * <p>
 * 登录、注册网络请求Presenter
 * <p>
 * BaseResponse：此处使用多态，传入BaseResponse子类，也可新建UserResponse定义user独有属性
 */
public class UserPresenter extends BasePresenterImpl<UserResponse> {

    private final UserModel mUserModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public UserPresenter(IViewToPresenter view, Context context) {
        super(view);
        mUserModel = new UserModel(context);
    }

    /**
     * 登录请求
     *
     * @param body       请求参数
     * @param requestTag 请求标识
     */
    public void loginRequest(UserRequest body, int requestTag) {
        mUserModel.retrofitLogin(body, this, requestTag);
    }

    /**
     * 注册请求
     *
     * @param body       请求参数
     * @param requestTag 请求标识
     */
    public void registerRequest(RegisterRequestBean body, int requestTag) {
        mUserModel.retrofitRegister(body, this, requestTag);
    }


}
