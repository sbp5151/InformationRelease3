package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/3 17:41
 */
public class ProgramLoadSucceedModel {

    private final ProgramLoadSucceedService mSucceedService;

    public ProgramLoadSucceedModel(Context context) {
        Retrofit retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mSucceedService = retrofit.create(ProgramLoadSucceedService.class);
    }

    /**
     * 节目推送并加载成功反馈
     * @param programId
     * @param deviceId
     * @param callBack
     * @param requestTag
     */
    public void programLoadSucceedBack(String programId, String deviceId, final IPresenterToModel<BaseResponse> callBack, final int requestTag) {
        String md5 = MD5Util.getMD5(Constant.S_KEY + deviceId + programId);
        mSucceedService.load_succeed(programId, deviceId, md5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver2<BaseResponse>(callBack, requestTag));
    }
}
