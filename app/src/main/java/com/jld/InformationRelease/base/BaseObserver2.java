package com.jld.InformationRelease.base;

import com.jld.InformationRelease.interfaces.IPresenterToModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 14:53
 */
public  class BaseObserver2<T> implements Observer<T> {

    IPresenterToModel<T> mCallback;
    int mRequestTag;

    public BaseObserver2(IPresenterToModel<T> callback, int requestTag) {
        mRequestTag = requestTag;
        mCallback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallback.beforeRequest(mRequestTag);

    }

    @Override
    public void onNext(T value) {
        BaseResponse baseResponse = (BaseResponse) value;
        if (baseResponse != null && baseResponse.getResult().equals("0")) {//成功
            mCallback.requestSuccess((T) baseResponse, mRequestTag);
        } else if (value != null) {//失败
            mCallback.requestError(new Exception(baseResponse.getMsg()), mRequestTag);
        } else {//错误
            mCallback.requestError(new Exception("获取数据错误，请重试！"), mRequestTag);
        }
    }

    @Override
    public void onComplete() {
        mCallback.requestComplete(mRequestTag);
    }

    @Override
    public void onError(Throwable e) {
        mCallback.requestError(e, mRequestTag);
    }
}