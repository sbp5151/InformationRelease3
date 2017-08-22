package com.jld.InformationRelease.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by boping on 2017/8/22.
 */

public class FileDownloadUtil {

    public static final String TAG = "FileDownloadUtil";
    private final OkHttpClient mOkHttpClient;
    private static FileDownloadUtil sFileDownloadUtil;

    public static FileDownloadUtil getInstance() {
        if (sFileDownloadUtil == null)
            sFileDownloadUtil = new FileDownloadUtil();
        return sFileDownloadUtil;
    }

    private FileDownloadUtil() {
        mOkHttpClient = new OkHttpClient();
    }

    /**
     * 文件下载工具类
     *
     * @param url
     */
    public void FileDownload(final String url, final Context context, final OnFileDownloadListen fileDownloadListen) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, "onFailure:" + e.getMessage());
                fileDownloadListen.onFailure();
                //下载失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(TAG, "onResponse:" + response.body());
                InputStream in = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                long length = response.body().contentLength();
                File saveFile = new File(FileUtil.getExternalStorageDirectory(), url.substring(url.lastIndexOf("/") + 1));
                LogUtil.d(TAG, "saveFile.length():" + saveFile.length());
                LogUtil.d(TAG, "length:" + length);
                if (saveFile.exists()) {
                    if (saveFile.length() >= length) {//已下载直接安装
                        fileDownloadListen.onSuccess(saveFile.getAbsolutePath());
                        return;
                    } else if (saveFile.length() > 0) {//未下载完成 删除
                        saveFile.delete();
                        saveFile = new File(FileUtil.getExternalStorageDirectory(), url.substring(url.lastIndexOf("/") + 1));
                        saveFile.createNewFile();
                    }
                } else {
                    saveFile.createNewFile();
                }
                try {
                    in = response.body().byteStream();
                    fos = new FileOutputStream(saveFile);
                    long sum = 0;
                    while ((len = in.read(buf)) != -1) {
                        //下载中。。。
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / length * 100);
                        LogUtil.d(TAG, "progress:" + progress);
                        fileDownloadListen.onProgress(progress);
                    }
                    fos.flush();
                    //下载完成
                    fileDownloadListen.onSuccess(saveFile.getAbsolutePath());
                } catch (Exception e) {
                    //下载失败
                    fileDownloadListen.onFailure();
                    LogUtil.d(TAG, "下载失败:" + e.getMessage());
                    saveFile.delete();
                } finally {
                    if (in != null)
                        in.close();
                    if (fos != null)
                        fos.close();
                }
            }
        });
    }

    public interface OnFileDownloadListen {
        void onFailure();

        void onSuccess(String filePath);

        void onProgress(int progress);
    }
}
