package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.request.UpdateTimeRequest;
import com.jld.InformationRelease.model.UpdateTimeModel;

/**
 * Created by boping on 2017/8/10.
 */

public class UpdateTimePresenter extends BasePresenterImpl<BaseResponse> {

    private final UpdateTimeModel mUpdateTimeModel;
    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public UpdateTimePresenter(IViewToPresenter view, Context context) {
        super(view);
        mUpdateTimeModel = new UpdateTimeModel(context);
    }

    /**
     * 检查设备是否被绑定
     * @param body
     * @param requestTag
     */
    public  void updateTime(UpdateTimeRequest body, int requestTag){
        mUpdateTimeModel.updateTime(body,this,requestTag);
    }
}
