package com.jld.InformationRelease.base;

import com.jld.InformationRelease.interfaces.IPresenterListen;

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
public abstract class BaseObserver<T> implements Observer<T> {

    IPresenterListen<T> mCallback;
    int mRequestTag;

    public BaseObserver(IPresenterListen<T> callback, int requestTag) {
        mRequestTag = requestTag;
        mCallback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallback.beforeRequest(mRequestTag);

    }

    @Override
    public void onComplete() {
        mCallback.requestComplete(mRequestTag);
    }

    @Override
    public void onError(Throwable e) {
        mCallback.requestError(new Exception("网络错误"), mRequestTag);
    }
}