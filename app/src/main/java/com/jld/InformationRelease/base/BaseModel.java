package com.jld.InformationRelease.base;

import android.content.Context;

import com.jld.InformationRelease.model.FileService;
import com.jld.InformationRelease.util.RetrofitManager;

import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 14:43
 */
public class BaseModel {

    public FileService mFileService;
    public Context mContext;

    public BaseModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mFileService = mRetrofit.create(FileService.class);
        mContext = context;
    }
}
