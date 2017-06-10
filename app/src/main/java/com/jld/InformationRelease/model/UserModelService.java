package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.request_bean.ChangePWRequestBean;
import com.jld.InformationRelease.bean.request_bean.RegisterRequestBean;
import com.jld.InformationRelease.bean.request_bean.RetrievePWRequestBean;
import com.jld.InformationRelease.bean.request_bean.UserRequest;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/12 9:49
 */
public interface UserModelService {
    //http://www.jianshu.com/p/308f3c54abdd retrofit详解

    /**
     * 登录
     */
    @POST(URLConstant.LOGIN_URL)
    Observable<UserResponse> login(@Body UserRequest body);

    /**
     * 注册
     */
    @POST(URLConstant.REGISTER_URL)
    Observable<UserResponse> register(@Body RegisterRequestBean body);

    /**
     * 修改密码
     */
    @POST(URLConstant.CHANGE_PASSWORD)
    Observable<UserResponse> changerPassword(@Body ChangePWRequestBean body);

    /**
     * 找回密码
     */
    @POST(URLConstant.RETRIEVE_PASSWORD)
     Observable<UserResponse> retrievePassword(@Body RetrievePWRequestBean body);

    /**
     * 注册获取验证码
     */
    @POST(URLConstant.GET_VERIFY_CODE)
    Observable<VerifyCodeResponseBean> getVerifyCode1(@Body VerifyCodeRequestBean body);

    /**
     * 找回密码获取验证码
     */
    @POST(URLConstant.SMS_GET)
    Observable<VerifyCodeResponseBean> getVerifyCode2(@Body VerifyCodeRequestBean body);

}
