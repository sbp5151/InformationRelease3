package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.bean.request.GetDevIdRequest;
import com.jld.InformationRelease.bean.response.GetDevIdResponse;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by boping on 2017/8/10.
 */

public class GetDevIdModel {

    private final GetDevIdService mGetDevIdService;
    public GetDevIdModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mGetDevIdService = mRetrofit.create(GetDevIdService.class);
    }

    /**
     * 设备检查是否被绑定
     * @param body
     * @param callBack
     * @param requestTag
     */
    public void getDevId(GetDevIdRequest body, final IPresenterToModel<GetDevIdResponse> callBack,
                                final int requestTag){

        mGetDevIdService.get_dev_id(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetDevIdResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callBack.beforeRequest(requestTag);
                    }

                    @Override
                    public void onNext(GetDevIdResponse value) {
                        if (value != null && value.getResult().equals("0")) {//成功
                            callBack.requestSuccess(value, requestTag);
                        } else if (value != null) {//失败
                            callBack.requestError(new Exception(value.getMsg()), requestTag);
                        } else {//错误
                            callBack.requestError(new Exception("获取数据错误，请重试！"), requestTag);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.requestError(e, requestTag);
                    }

                    @Override
                    public void onComplete() {
                        callBack.requestComplete(requestTag);
                    }
                });
    }
}
