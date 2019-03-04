package com.jld.InformationRelease.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.jld.InformationRelease.presenter.BitmapUtilPresenter;
import com.jld.InformationRelease.util.BitmapUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/9 11:05
 */
public class BitmapUtilModel {

    private static final String TAG = "BitmapUtilModel";

    public void bitmapCompress(final Bitmap bitmap, final int quality, final String compressFile, final BitmapUtilPresenter.BitmapCompressListen compressListen) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String s = BitmapUtil.compressBitmap(bitmap,quality, compressFile, true);
                // 释放Bitmap
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                e.onNext(compressFile);
            }
        })
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                        compressListen.onStart();
                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "onNext:" + value);
                        compressListen.compressSucceed(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:" + e.toString());
                        compressListen.compressError();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
}
