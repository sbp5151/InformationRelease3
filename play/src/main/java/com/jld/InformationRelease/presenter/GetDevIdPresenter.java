package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.request.GetDevIdRequest;
import com.jld.InformationRelease.bean.response.GetDevIdResponse;
import com.jld.InformationRelease.model.GetDevIdModel;

/**
 * Created by boping on 2017/8/10.
 */

public class GetDevIdPresenter extends BasePresenterImpl<GetDevIdResponse> {

    private final GetDevIdModel mGetDevIdModel;
    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public GetDevIdPresenter(IViewToPresenter view, Context context) {
        super(view);
        mGetDevIdModel = new GetDevIdModel(context);
    }

    /**
     * 检查设备是否被绑定
     * @param body
     * @param requestTag
     */
    public  void getDevId(GetDevIdRequest body, int requestTag){
        mGetDevIdModel.getDevId(body,this,requestTag);
    }
}
