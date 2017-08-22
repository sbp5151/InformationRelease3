package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.request_bean.CheckVersionRequest;
import com.jld.InformationRelease.bean.response_bean.CheckVersionResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.model.CheckVersionUpdateModel;

/**
 * Created by boping on 2017/8/21.
 */

public class CheckVersionUpdatePresenter extends BasePresenterImpl<CheckVersionResponse>{

    private final CheckVersionUpdateModel mUpdateModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public CheckVersionUpdatePresenter(IViewListen view,Context context) {
        super(view);
        mUpdateModel = new CheckVersionUpdateModel(context);
    }

    /**
     * 版本更新检查
     * @param body
     * @param requestTag
     */
    public void checkVersionUpdate(CheckVersionRequest body,int requestTag){
        mUpdateModel.checkVersionUpdate(body,this,requestTag);
    }
}
