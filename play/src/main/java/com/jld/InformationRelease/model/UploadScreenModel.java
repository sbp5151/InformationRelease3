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
 * @create-time 2017/5/15 16:13
 */
public class UploadScreenModel {

    private static final String TAG = "UploadScreenModel";
    private final UploadScreenService mScreenService;

    public UploadScreenModel(Context context) {

        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mScreenService = mRetrofit.create(UploadScreenService.class);
    }

    /**
     * 上传截屏
     * @param screenUrl 截屏url地址
     * @param callback
     * @param requestID
     */
    public void uploadScreen(String screenUrl, final IPresenterToModel<BaseResponse> callback, int requestID) {

        String sign = MD5Util.getMD5(Constant.S_KEY);
        mScreenService.uploadScreen(screenUrl,sign)
                .subscribeOn(Schedulers.io())//设置上游线程（耗时）
                .observeOn(AndroidSchedulers.mainThread())//设置下游线程(更新UI)
                .subscribe(new BaseObserver2<BaseResponse>(callback,requestID));//连接上下游
    }

}
