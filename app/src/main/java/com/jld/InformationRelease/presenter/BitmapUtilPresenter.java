package com.jld.InformationRelease.presenter;

import android.graphics.Bitmap;

import com.jld.InformationRelease.model.BitmapUtilModel;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/9 11:09
 */
public class BitmapUtilPresenter {

    private final BitmapUtilModel mModel;
    BitmapCompressListen mCompressListen;

    public BitmapUtilPresenter(BitmapCompressListen bitmapCompressListen) {
        mModel = new BitmapUtilModel();
        mCompressListen = bitmapCompressListen;
    }

    public void bitmapCompress(Bitmap bitmap,int quality, String compressFile) {
        mModel.bitmapCompress(bitmap, quality,compressFile, mCompressListen);
    }

    public interface BitmapCompressListen {

        public void compressSucceed(String compressPath);

        public void compressError();

        void onStart();
    }
}
