package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver;
import com.jld.InformationRelease.bean.request_bean.CheckVersionRequest;
import com.jld.InformationRelease.bean.response_bean.CheckVersionResponse;
import com.jld.InformationRelease.interfaces.IPresenterListen;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by boping on 2017/8/21.
 */

public class CheckVersionUpdateModel {

    private Context mContext;
    private CheckVersionUpdateService mUpdateService;

    public CheckVersionUpdateModel(Context context) {
        mContext = context;
        Retrofit retrofit = RetrofitManager.getInstanceDownload(mContext).getRetrofit();
        mUpdateService = retrofit.create(CheckVersionUpdateService.class);
    }

    /**
     * 版本更新检查
     *
     * @param request
     * @param callback
     * @param requestTag
     */
    public void checkVersionUpdate(CheckVersionRequest request, final IPresenterListen<CheckVersionResponse> callback, final int requestTag) {

        mUpdateService.checkVersionUpdate(GeneralUtil.getVersion(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<CheckVersionResponse>(callback, requestTag) {
                    @Override
                    public void onNext(CheckVersionResponse value) {
                        callback.requestSuccess(value, requestTag);
                    }
                });
    }
}
