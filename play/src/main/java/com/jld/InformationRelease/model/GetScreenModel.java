package com.jld.InformationRelease.model;

import android.app.Activity;
import android.util.Log;

import com.jld.InformationRelease.util.DeviceUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/15 16:13
 */
public class GetScreenModel {

    private static final String TAG = "GetScreenModel";

    public void getScreen(final Activity activity, final String savePath, final GetScreenListen callback) {
        Log.d(TAG,"getScreen：");

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                Log.d(TAG,"subscribe：");
                boolean isSucceed = DeviceUtil.getScreens(activity, savePath);
                if(isSucceed)
                    e.onComplete();
                else
                    e.onError(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onScreenError();
                    }

                    @Override
                    public void onComplete() {

                        callback.onScreenComplete();
                    }
                });

    }

    public interface GetScreenListen {

        void onScreenError();
        void onScreenComplete();
    }
}
