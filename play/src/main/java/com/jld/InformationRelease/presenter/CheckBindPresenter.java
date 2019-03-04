package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.request.CheckBindRequest;
import com.jld.InformationRelease.bean.response.CheckBindResponse;
import com.jld.InformationRelease.model.CheckBindModel;

/**
 * Created by boping on 2017/8/10.
 */

public class CheckBindPresenter extends BasePresenterImpl<CheckBindResponse> {

    private final CheckBindModel mCheckBindModel;
    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public CheckBindPresenter(IViewToPresenter view, Context context) {
        super(view);
        mCheckBindModel = new CheckBindModel(context);
    }

    /**
     * 检查设备是否被绑定
     * @param body
     * @param requestTag
     */
    public  void deviceCheckBind(CheckBindRequest body,int requestTag){
        mCheckBindModel.deviceCheckBind(body,this,requestTag);
    }
}
