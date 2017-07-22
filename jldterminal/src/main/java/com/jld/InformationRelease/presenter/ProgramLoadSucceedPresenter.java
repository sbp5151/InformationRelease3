package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.model.ProgramLoadSucceedModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/3 17:53
 */
public class ProgramLoadSucceedPresenter extends BasePresenterImpl<BaseResponse> {

    private final ProgramLoadSucceedModel mMode;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public ProgramLoadSucceedPresenter(IViewToPresenter view, Context context) {
        super(view);
        mMode = new ProgramLoadSucceedModel(context);
    }
    public void programLoadSucceedBack(String deviceId, String programId,int requestTag){

        mMode.programLoadSucceedBack(deviceId,programId,this,requestTag);
    }

}
