package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.model.VerifyCodeModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 16:56
 */
public class VerifyCodePresenter extends BasePresenterImpl<VerifyCodeResponseBean> {

    private  VerifyCodeModel mPresenter;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public VerifyCodePresenter(IViewToPresenter view, Context context) {
        super(view);
        mPresenter = new VerifyCodeModel(context);
    }
    /**
     * 获取验证码请求
     *
     * @param body       请求参数
     * @param requestTag 请求标识
     */
    public void getVerifyCode(VerifyCodeRequestBean body, int requestTag) {
        mPresenter.retrofitVerifyCode(body, this, requestTag);
    }
}
