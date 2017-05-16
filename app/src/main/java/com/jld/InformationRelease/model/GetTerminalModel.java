package com.jld.InformationRelease.model;

import android.content.Context;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.bean.request_bean.GetTerminalRequest;
import com.jld.InformationRelease.bean.response_bean.GetTerminalResponse;
import com.jld.InformationRelease.interfaces.IPresenterToModel;
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
 * @create-time 2017/5/11 14:40
 */
public class GetTerminalModel{

    private final GetTerminalService mFileService;
    private final Context mContext;

    public GetTerminalModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mFileService = mRetrofit.create(GetTerminalService.class);
        mContext = context;
    }

    public void getTerminal(GetTerminalRequest request, final IPresenterToModel<GetTerminalResponse> callback, final int requestTag){

        mFileService.getTerminal(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<GetTerminalResponse>(callback,requestTag));
    }


}
