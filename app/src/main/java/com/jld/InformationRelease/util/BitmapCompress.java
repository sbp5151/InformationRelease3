package com.jld.InformationRelease.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jld.InformationRelease.presenter.BitmapUtilPresenter;

import java.io.File;
import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/22 14:38
 * <p>
 * 图片压缩工具类 单例类
 */
public class BitmapCompress {

    private static BitmapCompress mBitmapCompress;
    private static final int IMAGE_COMPRESS = 0x11;
    private ArrayList<String> imgs;
    private String currentImg;
    private int currentItem = 0;
    private CompressListener mCompressListener;
    public static final String TAG = "BitmapCompress";
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IMAGE_COMPRESS:
                    if (currentItem < imgs.size()) {
                        photoCompress(imgs.get(currentItem));
                        currentItem++;
                    } else {
                        mCompressListener.compressComplete();
                    }
                    break;
            }
        }
    };
    private String mMiniPath;
    private File mMiniFile;

    private BitmapCompress() {
    }

    public static BitmapCompress getInstance() {
        if (mBitmapCompress == null)
            mBitmapCompress = new BitmapCompress();
        return mBitmapCompress;
    }

    public void compressBitmaps(ArrayList<String> images, CompressListener compressListener) {
        currentItem = 0;
        imgs = images;
        mCompressListener = compressListener;
        createMiniPhotoFile();
        mHandler.sendEmptyMessage(IMAGE_COMPRESS);
    }

    /**
     * 图片压缩
     *
     * @param path
     */
    private void photoCompress(final String path) {
        currentImg = path;
        LogUtil.d(TAG, "图片压缩路径：" + path);
        if (!TextUtils.isEmpty(path)) {
            long fileSize = new File(path).length();
            LogUtil.d(TAG, "图片压缩前大小：" + fileSize);
            //所获取图片的bitmap
            Bitmap photoBmp = BitmapFactory.decodeFile(path);
            if (photoBmp != null) {
                if (fileSize <= 50 * 1024) {
                    LogUtil.d(TAG, "太小不用压缩");
                    mCompressListener.compressSucceed(path);
                    mHandler.sendEmptyMessage(IMAGE_COMPRESS);
                    return;
                }
                String imgName = path.substring(path.lastIndexOf(File.separator) + 1);
                LogUtil.d(TAG, "图片名称：" + imgName);
                File[] miniFiles = mMiniFile.listFiles();
                for (File file : miniFiles) {
                    if (file.getName().equals("jldmini" + imgName)) {//不用压缩
                        LogUtil.d(TAG, "不用压缩：" + file.getAbsolutePath());
                        mCompressListener.compressSucceed(file.getAbsolutePath());
                        mHandler.sendEmptyMessage(IMAGE_COMPRESS);
                        return;
                    }
                }
                LogUtil.d(TAG, "进行压缩：" + imgName);
                imgName = mMiniPath + File.separator + "jldmini" + imgName;
                //图片压缩
                BitmapUtilPresenter presenter = new BitmapUtilPresenter(mBitmapCompressListen);
                //图片压缩RxJava
                if (fileSize >= 10 * 1024 * 1024) {
                    presenter.bitmapCompress(photoBmp, 20, imgName);
                } else if (fileSize >= 5 * 1024 * 1024) {
                    presenter.bitmapCompress(photoBmp, 30, imgName);
                } else if (fileSize >= 3 * 1024 * 1024) {
                    presenter.bitmapCompress(photoBmp, 40, imgName);
                } else if (fileSize >= 1024 * 1024) {
                    presenter.bitmapCompress(photoBmp, 55, imgName);
                } else if (fileSize >= 800 * 1024) {
                    presenter.bitmapCompress(photoBmp, 65, imgName);
                } else if (fileSize >= 500 * 1024) {
                    presenter.bitmapCompress(photoBmp, 75, imgName);
                } else if (fileSize >= 300 * 1024) {
                    presenter.bitmapCompress(photoBmp, 85, imgName);
                } else {
                    presenter.bitmapCompress(photoBmp, 95, imgName);
                }
            } else {
                mCompressListener.compressDefeat(currentImg);
                mHandler.sendEmptyMessage(IMAGE_COMPRESS);
            }
        } else {
            mCompressListener.compressDefeat(currentImg);
            mHandler.sendEmptyMessage(IMAGE_COMPRESS);
            Log.e(TAG, "photoCompress: 压缩的图片的路径为空");
        }
    }

    private void createMiniPhotoFile() {
        if (TextUtils.isEmpty(mMiniPath)) {
            mMiniPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miniPhoto";
            mMiniFile = new File(mMiniPath);
            if (!mMiniFile.exists())
                mMiniFile.mkdirs();
        }
    }

    //图片压缩返回监听
    BitmapUtilPresenter.BitmapCompressListen mBitmapCompressListen = new BitmapUtilPresenter.BitmapCompressListen() {
        @Override
        public void compressSucceed(String compressPath) {//成功并返回结果
            mCompressListener.compressSucceed(compressPath);
            mHandler.sendEmptyMessage(IMAGE_COMPRESS);
            LogUtil.d(TAG, "压缩成功：" + compressPath);
        }

        @Override
        public void compressError() {//失败
            mCompressListener.compressDefeat(currentImg);
            mHandler.sendEmptyMessage(IMAGE_COMPRESS);
            LogUtil.d(TAG, "压缩失败：" + currentImg);
        }

        @Override
        public void onStart() {//开始
        }
    };

    /**
     * 图片压缩返回监听
     */
    public interface CompressListener {
        //压缩成功 返回压缩后路径
        void compressSucceed(String imgPath);

        //压缩完成
        void compressComplete();

        //压缩失败
        void compressDefeat(String imgPath);
    }
}
