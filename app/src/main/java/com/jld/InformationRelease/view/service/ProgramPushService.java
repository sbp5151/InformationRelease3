package com.jld.InformationRelease.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.jld.InformationRelease.bean.request_bean.ProgramRequestBean;
import com.jld.InformationRelease.bean.response_bean.FileResponseBean;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.TerminalFunctionPresenter;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;

/**
 * 后台节目推送
 */
public class ProgramPushService extends Service implements IViewToPresenter {

    private static final String TAG = "ProgramPushService";
    private static final int IMG_UPDATE = 0x01;//图片上传请求码
    private static final int PROGRAM_UPDATE = 0x02;//节目上传请求码
    private boolean mImgIsUpdate = false;//图片是否上传完成
    /**
     * 上传完成监听
     */
    PushCompleteListener mCompleteListener;
    /**
     * 上传数据
     */
    ProgramRequestBean mBody;
    private MyBinder mMyBinder;

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    @Override
    public void showProgress(int requestTag) {
    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(Object data, int requestTag) {
        if (requestTag == PROGRAM_UPDATE) {
            mCompleteListener.updateSucceed();
        } else if (requestTag == IMG_UPDATE) {
            mImgIsUpdate = true;
            FileResponseBean responseBean = (FileResponseBean) data;
            ArrayList<String> images = mBody.getImages();
            images.clear();
            //图片上传完成，设置图片url
            for (String imgUrl : responseBean.getFileUrls()) {
                images.add(imgUrl);
            }
            //再上传节目
            updateProgram();
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        if (requestTag == PROGRAM_UPDATE) {
            mCompleteListener.updateDefeated();
        } else if (requestTag == IMG_UPDATE) {

        }
    }

    public class MyBinder extends Binder {
        //监听上传结果
        public void sendCompleteListener(PushCompleteListener completeListener) {
            mCompleteListener = completeListener;
        }

        //传递上传数据
        public void sendPushData(ProgramRequestBean body) {
            LogUtil.d(TAG, "sendPushData:" + body);
            mBody = body;
        }

        //开始上传
        public void startPush() {
            Gson gson = new Gson();
            String toJson = gson.toJson(mBody);
            LogUtil.d(TAG, "tojson:" + toJson);
            //上传图片
            if (!mImgIsUpdate && mBody.getImages().size() > 0) {
                FilePresenter filePresenter = new FilePresenter(ProgramPushService.this, ProgramPushService.this);
                filePresenter.updateFiles(mBody.getImages(), IMG_UPDATE);
            } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                updateProgram();
            }
        }
    }

    /**
     * 上传节目
     */
    public void updateProgram() {
        TerminalFunctionPresenter presenter = new TerminalFunctionPresenter(this, this);

        presenter.pushProgram(mBody, PROGRAM_UPDATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public interface PushCompleteListener {
        void updateSucceed();// 上传成功

        void updateDefeated();// 上传失败
    }
}
