package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.response_bean.GetModelResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.model.GetModelModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/18 16:03
 */
public class GetModelPresenter extends BasePresenterImpl<GetModelResponse> {

    private final GetModelModel mModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public GetModelPresenter(IViewListen view, Context context) {
        super(view);
        mModel = new GetModelModel(context);
    }

    /**
     * 获取节目模板
     * @param requestId
     */
    public void getModel(int requestId){
        mModel.getModel(this,requestId);
    }
}
