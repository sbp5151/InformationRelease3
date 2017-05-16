package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.request_bean.BindingRequest;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.bean.request_bean.ProgramRequestBean;
import com.jld.InformationRelease.bean.request_bean.ShowdownRestartRequestBean;
import com.jld.InformationRelease.bean.request_bean.TimeShowdownRequestBean;
import com.jld.InformationRelease.bean.request_bean.VolumeAdjustRequestBean;
import com.jld.InformationRelease.model.TerminalFunctionModel;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 14:27
 * <p>
 *
 *
 * 对设备终端的所有的操作功能
 */
public class TerminalFunctionPresenter extends BasePresenterImpl<BaseResponse> {

    private final TerminalFunctionModel mTerminalFunctionModel;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public TerminalFunctionPresenter(Context context, IViewToPresenter view) {
        super(view);
        mTerminalFunctionModel = new TerminalFunctionModel(context);
    }

    /**
     * 设备绑定
     *
     * @param body
     * @param requestTag
     */
    public void binding(BindingRequest body, int requestTag) {
        mTerminalFunctionModel.retrofitBinding(body, this, requestTag);
    }

    /**
     * 节目推送
     *
     * @param body
     * @param requestTag
     */
    public void pushProgram(ProgramRequestBean body, int requestTag) {
        mTerminalFunctionModel.retrofitPushProgram(body, this, requestTag);
    }

    public void updateFile(){

    }

    /**
     * 关机&重启
     *
     * @param body
     * @param requestTag
     */
    public void showdownRestart(ShowdownRestartRequestBean body, int requestTag) {

        mTerminalFunctionModel.retrofitShowdownRestart(body, this, requestTag);
    }

    /**
     * 定时开关机
     *
     * @param body
     * @param requestTag
     */
    public void showdownRestart(TimeShowdownRequestBean body, int requestTag) {

        mTerminalFunctionModel.retrofitTimeShowdown(body, this, requestTag);
    }

    /**
     * 音量调节
     *
     * @param body
     * @param requestTag
     */
    public void volumeAdjust(VolumeAdjustRequestBean body, int requestTag) {
        mTerminalFunctionModel.retrofitVolumeAdjust(body, this, requestTag);
    }

}
