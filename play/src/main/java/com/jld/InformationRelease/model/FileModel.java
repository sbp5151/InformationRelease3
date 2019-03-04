package com.jld.InformationRelease.model;

import android.content.Context;
import android.util.Log;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.IPresenterToModel;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.interfaces.IBaseListener;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;
import com.jld.InformationRelease.util.utilcode.util.FileUtils;
import com.jld.InformationRelease.util.utilcode.util.LogUtils;
import com.jld.InformationRelease.util.utilcode.util.PathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

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
        mFileService = RetrofitManager.getInstance(context).getRetrofit().create(FileService.class);
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
    public void uploadFiles(ArrayList<String> imgPath, final IPresenterToModel<FileResponseBean> callback, final int requestTag) {

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
        mFileService.uploadFiles(sign, files)
                .subscribeOn(Schedulers.io())           //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<FileResponseBean>(callback, requestTag));
    }

    public void uploadFile(String filePath, final IPresenterToModel<FileResponseBean> callback, final int requestTag) {
        Log.d(TAG, "filePath:" + filePath);
        /**sign*/
        String md5Sig = MD5Util.getMD5(Constant.S_KEY);
        RequestBody sign = RequestBody.create(mTextType, md5Sig);
        /**file*/
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(mImageType, file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        mFileService.uploadFile(sign, filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver2<FileResponseBean>(callback, requestTag));

    }

    public void downloadVideo(final List<String> filePaths, final IBaseListener listener) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                for (String item : filePaths) {
                    e.onNext(item);//依次下载图片
                }
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext1: "+value);
                mFileService.downloadVideo(value)//下载图片
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(final ResponseBody value) {
                                Log.d(TAG, "onNext2: "+value);
                                Observable.create(new ObservableOnSubscribe<String>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                                        boolean isSave = saveToDisk(value);
                                        if (isSave)
                                            e.onNext("succeed");
                                        else e.onNext("failed");
                                        e.onComplete();
                                    }
                                }).subscribe(new Observer<String>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(String value) {
                                        Log.d(TAG, "onNext3: "+value);
                                        if (value.equals("succeed"))
                                            listener.succeed();
                                        else listener.failed("");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError3: "+e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(TAG, "onComplete3: ");
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError2: "+e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete2: ");
                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError1: "+e);

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete1: ");

            }
        });
    }

    private boolean saveToDisk(ResponseBody responseBody) {
        InputStream is = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            is = responseBody.byteStream();
            file = new File(PathUtils.getExternalStoragePath() + File.separator + "Jld_Video_Cache" + File.separator + System.currentTimeMillis() + "_lbj.mp4");
            FileUtils.delete(file);
            FileUtils.createOrExistsFile(file);
            fos = new FileOutputStream(file);
            long all = responseBody.contentLength();
            byte[] buf = new byte[2048];
            int len;
            long sum = 0;

            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum / all * 100);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            LogUtils.e(e.toString());
            FileUtils.delete(file);
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
