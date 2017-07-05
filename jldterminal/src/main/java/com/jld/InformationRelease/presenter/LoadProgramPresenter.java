package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.model.LoadProgramModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 9:43
 */
public class LoadProgramPresenter extends BasePresenterImpl<ProgramResponseBean> {

    private final LoadProgramModel mProgramModel;
    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public LoadProgramPresenter(IViewToPresenter view, Context context) {
        super(view);
        mProgramModel = new LoadProgramModel(context);
    }

    /**
     * 节目加载
     * @param programID 节目ID
     * @param requestTag
     */
    public void LoadProgram(String programID,int requestTag){
        mProgramModel.loadProgram(programID,this,requestTag);
    }
}
