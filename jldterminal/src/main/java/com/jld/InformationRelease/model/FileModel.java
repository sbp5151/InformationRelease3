package com.jld.InformationRelease.model;

import android.content.Context;
import android.util.Log;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
    public static final String TEXT_FORM_DATA = "text/plain";
    public static final String IMAGE_FORM_DATA = "image/*";
    private final MediaType mTextType;
    private final MediaType mFileType;
    private final MediaType mImageType;

    public FileModel(Context context) {
        Retrofit mRetrofit = RetrofitManager.getInstance(context).getRetrofit();
        mFileService = mRetrofit.create(FileService.class);
        mTextType = MediaType.parse(TEXT_FORM_DATA);
        mFileType = MediaType.parse(MULTIPART_FORM_DATA);
        mImageType = MediaType.parse(IMAGE_FORM_DATA);
        mContext = context;
    }

    /**
     * 多张图片上传
     *
     * @param imgPath
     */
    public void updateFiles(ArrayList<String> imgPath, final IPresenterToModel<FileResponseBean> callback, final int requestTag) {

        /**sign*/
        String md5Sig = MD5Util.getMD5(Constant.S_KEY);
        RequestBody sign = RequestBody.create(mTextType, md5Sig);
        /**files*/
        HashMap<String, RequestBody> files = new HashMap<>();
        for (String str : imgPath) {
            File file = new File(str);
            RequestBody fileBody = RequestBody.create(mImageType, file);
            files.put("file\"; filename=\"" + file.getName(), fileBody);
        }
        mFileService.updateFiles(sign, files)
                .subscribeOn(Schedulers.io())           //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<FileResponseBean>(callback, requestTag));
    }

    public void updateFile(String filePath, final IPresenterToModel<FileResponseBean> callback, final int requestTag) {
        Log.d(TAG, "filePath:" + filePath);
        /**sign*/
        String md5Sig = MD5Util.getMD5(Constant.S_KEY);
        RequestBody sign = RequestBody.create(mTextType, md5Sig);
        /**file*/
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(mImageType, file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        mFileService.updateFile(sign, filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<FileResponseBean>(callback, requestTag));

    }
}
