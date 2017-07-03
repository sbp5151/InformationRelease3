package com.jld.InformationRelease.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

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
                        LogUtil.d(TAG, "压缩：" + imgs.get(currentItem));
                        photoCompress(imgs.get(currentItem));
                        currentItem++;
                    } else {
                        mCompressListener.compressComplete();
                    }
                    break;
            }
        }
    };

    private BitmapCompress() {
    }

    public static BitmapCompress getInstance() {
        if (mBitmapCompress == null)
            mBitmapCompress = new BitmapCompress();
        return mBitmapCompress;
    }

    public void compressBitmaps(ArrayList<String> images, CompressListener compressListener) {
        LogUtil.d(TAG, "compressBitmaps:" + images);
        currentItem = 0;
        imgs = images;
        mCompressListener = compressListener;
        mHandler.sendEmptyMessage(IMAGE_COMPRESS);
    }

    /**
     * 图片压缩
     *
     * @param path
     */
    private void photoCompress(final String path) {
        currentImg = path;
        LogUtil.d(TAG, "path:" + path);
        if (!TextUtils.isEmpty(path)) {
            //所获取图片的bitmap
            Bitmap photoBmp = BitmapFactory.decodeFile(path);
            if (photoBmp != null) {
                String[] split = path.split(File.separator);
                String imgName = split[split.length - 1];
                //创建miniphone文件夹
                String miniPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miniPhoto";
                File file = new File(miniPath);
                if (!file.exists())
                    file.mkdirs();
                int lastIndexOf = path.lastIndexOf(".");
                LogUtil.d(TAG, "imgName:" + imgName);
                String miniImgPath;
                if (imgName.contains("jldmini"))
                    miniImgPath = miniPath + File.separator + imgName;
                else
                    miniImgPath = miniPath + File.separator + "jldmini" + imgName;
                LogUtil.d(TAG, "miniImgPath:" + miniImgPath);
                if (!new File(miniImgPath).exists()) {//没有压缩过进行压缩
                    LogUtil.d(TAG, "压缩:");
                    //图片压缩
                    BitmapUtilPresenter presenter = new BitmapUtilPresenter(mBitmapCompressListen);
                    //图片压缩RxJava
                    presenter.bitmapCompress(photoBmp, miniImgPath);
                } else {
                    mCompressListener.compressDefeat(currentImg);
                    mHandler.sendEmptyMessage(IMAGE_COMPRESS);
                }
            } else {
                mCompressListener.compressDefeat(currentImg);
                mHandler.sendEmptyMessage(IMAGE_COMPRESS);
                throw new IllegalArgumentException(path);
            }
        } else {
            mCompressListener.compressDefeat(currentImg);
            mHandler.sendEmptyMessage(IMAGE_COMPRESS);
            throw new IllegalArgumentException(path);
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
