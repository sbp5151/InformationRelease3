package com.jld.InformationRelease.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jld.InformationRelease.base.BaseObserver2;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.interfaces.IPresenterListen;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.RetrofitManager;
import com.jld.InformationRelease.util.URLConstant;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static final String TEXT_FORM_DATA = "text/plain";
    public static final String IMAGE_FORM_DATA = "video/*";
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
     * @param callback
     */
    public void updateFiles(ArrayList<String> imgPath, final FilePresenter callback, final int requestTag) {

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
                .subscribe(new BaseObserver2(callback, requestTag));
    }

    public void updateFile(String filePath, final IPresenterListen<BaseResponse> callback, final int requestTag) {

        Log.d(TAG, "filePath:" + filePath);
        /**sign*/
        String md5Sig = MD5Util.getMD5(Constant.S_KEY);
        RequestBody sign = RequestBody.create(mTextType, md5Sig);
        /**file*/
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(mFileType, file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        mFileService.updateFile(sign, filePart)
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
                        callback.requestError(e, requestTag);
                        LogUtil.d(TAG, "onError:" + e);
                    }

                    @Override
                    public void onComplete() {
                        callback.requestComplete(requestTag);
                        LogUtil.d(TAG, "onComplete");

                    }
                });
    }

    /**
     * android上传文件到服务器
     * <p/>
     * 请求的rul
     *
     * @return 返回响应的内容
     */
    public static void uploadFile2(String imgPath,PushFileListener listener) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        // String ss = getHeadiconPath();
        try {
            URL url = new URL(URLConstant.BASE_HTTP_URL + URLConstant.PUSH_FILE);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();// http连接
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(1024 * 1024);// 1M
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 保持一直连接
            httpURLConnection.setRequestProperty("Charset", "UTF-8");// 编码
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);// POST传递过去的编码

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());// 输出流
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + imgPath.substring(
                    imgPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(imgPath);// 文件输入流，写入到内存中
            LogUtil.d(TAG, "水水水水picPath = " + imgPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();// http输入，即得到返回的结果
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            LogUtil.d(TAG, "水水水水result = " + result);
            dos.close();
            is.close();
            if (!TextUtils.isEmpty(result)) {
                JSONObject json = new JSONObject(result);
                String result1 = json.getString("result");
                String iamgurl = json.getString("msg");
                if(result1.equals("0")){
                    listener.pushSucceed(iamgurl);
                }else{
                    listener.pushDefeat(result1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.pushDefeat(e.getLocalizedMessage());
        }
    }

    public interface PushFileListener{
         void pushSucceed(String fileUrl);
         void pushDefeat(String defeat);
    }

}
