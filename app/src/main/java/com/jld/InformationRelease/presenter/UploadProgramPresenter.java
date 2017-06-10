package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.UpdateProgramResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.model.TerminalFunctionModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/5 19:16
 */
public class UploadProgramPresenter extends BasePresenterImpl<UpdateProgramResponse> {

    private final TerminalFunctionModel mTerminalFunctionModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public UploadProgramPresenter(IViewListen view, Context context) {
        super(view);
        mTerminalFunctionModel = new TerminalFunctionModel(context);

    }
    /**
     * 节目推送
     *
     * @param body
     * @param requestTag
     */
    public void uploadProgram(ProgramBean body, int requestTag) {
        mTerminalFunctionModel.retrofitPushProgram(body, this, requestTag);
    }

}
