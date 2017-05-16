package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.request_bean.GetTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.model.GetTerminalModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 14:33
 *
 * 获取绑定的终端设备
 */
public class GetTerminalPresenter extends BasePresenterImpl<GetTerminalResponse> {

    private final GetTerminalModel mModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public GetTerminalPresenter(IViewToPresenter view, Context context) {
        super(view);
        mModel = new GetTerminalModel(context);
    }

    /**
     * 获取所有绑定的终端设备
     * @param request
     * @param requestTag
     */
    public void getTerminal(GetTerminalRequest request,int requestTag) {

        mModel.getTerminal(request,this,requestTag);
    }
}
