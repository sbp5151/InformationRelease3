package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.L;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 9:48
 */
public class LoadProgramModel {

    public static final String TAG = "LoadProgramModel";
    private final LoadProgramService mProgramService;

    public LoadProgramModel(Context context) {

        Retrofit retrofit = RetrofitManager.getInstance(context).getRetrofit();
        mProgramService = retrofit.create(LoadProgramService.class);
    }

    public void loadProgram(String programId, final IPresenterToModel<ProgramResponseBean> callBack, final int requestTag) {
        String md5 = MD5Util.getMD5(Constant.S_KEY + programId);
        LoadProgramService.LoadProgramBody body = new LoadProgramService.LoadProgramBody();
        body.setSign(md5);
        body.setProgramid(programId);
        mProgramService.loadProgram(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ProgramResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callBack.beforeRequest(requestTag);
                    }

                    @Override
                    public void onNext(ProgramResponseBean value) {
                        L.d(TAG,"onNext:"+value);
                        if (value != null && value.getResult().equals("0")) {//成功
                            callBack.requestSuccess(value, requestTag);//多态
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
