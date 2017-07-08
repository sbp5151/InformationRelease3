package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.response_bean.ProgramLoadStateResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.model.ProgramLoadStateModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 10:09
 */
public class ProgramLoadStatePresenter extends BasePresenterImpl<ProgramLoadStateResponse> {

    private final ProgramLoadStateModel mStateModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public ProgramLoadStatePresenter(IViewListen view, Context context) {
        super(view);
        mStateModel = new ProgramLoadStateModel(context);
    }

    public void programLoadState(String programId,int requestTag){
        mStateModel.loadState(programId,this,requestTag);
    }
}
