package com.jld.InformationRelease.presenter;

import android.app.Activity;

import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.util.LogUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/15 16:55
 */
public class GetScreenPresenter {
    private final GetScreenModel mModel;
    public GetScreenPresenter() {
        mModel = new GetScreenModel();
    }

    public void getScreen(Activity activity, String savePath, GetScreenModel.GetScreenListen listen){
        LogUtil.d("GetScreenPresenter","getScreen：");

        mModel.getScreen(activity,savePath,listen);
    }
}
