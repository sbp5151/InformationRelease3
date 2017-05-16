package com.jld.InformationRelease.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/25 11:37
 */
public class FileModel {

    private static final String TAG = "FileModel";
    private final FileService mFileService;
    private Context mContext;
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public FileModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mFileService = mRetrofit.create(FileService.class);
        mContext = context;
    }

    /**
     * 多张图片上传
     *
     * @param imgPath
     */
    public void updateFiles(ArrayList<String> imgPath, final IPresenterToModel<FileResponseBean> callback, final int requestTag) {
        HashMap<String, RequestBody> files = new HashMap<>();
        for (String str : imgPath) {
            File file = new File(str);
            RequestBody fileBody = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
            files.put("file\"; filename=\"" + file.getName() + "\"", fileBody);
        }
        String sign = MD5Util.getMD5(Constant.S_KEY);
        mFileService.updateFiles(sign,files)
                .subscribeOn(Schedulers.io())           //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<FileResponseBean>(callback, requestTag));
    }

    public void updateFile(String filePath, final IPresenterToModel<BaseResponse> callback, final int requestTag) {
        Log.d(TAG, "body:" + filePath);
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        mFileService.updateFile(fileBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(BaseResponse value) {
                        LogUtil.d(TAG, "onNext:" + value);
                        if (value != null && value.getResult().equals("0")) {//成功
                            callback.requestSuccess(value, requestTag);
                        } else if (value != null) {//失败
                            callback.requestError(new Exception(value.getMsg()), requestTag);
                        } else {//错误
                            callback.requestError(new Exception("获取数据错误，请重试！"), requestTag);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(TAG, "onError");

                    }

                    @Override
                    public void onComplete() {
                        LogUtil.d(TAG, "onComplete");

                    }
                });

    }

    /**
     * 文件描述
     *
     * @param descriptionString
     * @return
     */
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, File file) {
        // 为file建立RequestBody实例
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
