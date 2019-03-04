package com.jld.InformationRelease.presenter;

import android.content.Context;

import com.jld.InformationRelease.base.BasePresenterImpl;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.model.UploadScreenModel;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/16 17:42
 */
public class UploadScreenPresenter extends BasePresenterImpl<BaseResponse> {


    private final Context context;
    private final UploadScreenModel mModle;

    /**
     * 构造方法
     *
     * @param view 具体业务的接口对象
     */
    public UploadScreenPresenter(IViewToPresenter view, Context context) {
        super(view);
        this.context = context;
        mModle = new UploadScreenModel(context);
    }

    /**
     * 上传截屏
     * @param screenUlr
     * @param requestID
     */
    public void uploadScreen(String screenUlr,int requestID) {
        mModle.uploadScreen(screenUlr,this,requestID);
    }

}
