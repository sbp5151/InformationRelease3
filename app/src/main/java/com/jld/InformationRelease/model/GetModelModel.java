package com.jld.InformationRelease.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.bean.response_bean.GetModelResponse;
import com.jld.InformationRelease.interfaces.IPresenterToModel;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;
import com.jld.InformationRelease.util.URLConstant;
import com.jld.InformationRelease.util.UserConstant;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/18 16:06
 */
public class GetModelModel {


    private final GetModelService mFileService;
    private final Context mContext;

    public GetModelModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mFileService = mRetrofit.create(GetModelService.class);
        mContext = context;
    }

    public void getModel(final IPresenterToModel<GetModelResponse> callback,int requestId){

        SharedPreferences sp = mContext.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        String userid = sp.getString(UserConstant.USER_ID, "");

        mFileService.getModel(userid, MD5Util.getMD5(userid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<GetModelResponse>(callback,requestId));
    }

    interface GetModelService {
        @POST(URLConstant.GET_MODEL)
        Observable<GetModelResponse> getModel(
                @Field("userid") String userid,
                @Field("sign") String sign
        );
    }
}
