package com.jld.InformationRelease.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.google.gson.Gson;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.UpdateProgramResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.UploadProgramPresenter;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;

/**
 * 后台节目推送
 */
public class ProgramPushService extends Service implements IViewListen<BaseResponse> {

    private static final String TAG = "ProgramPushService";
    private static final int IMG_UPDATE = 0x11;//图片上传请求码
    private static final int PROGRAM_UPDATE = 0x12;//节目上传请求码
    private static final int UPDATE_IMG = 0x01;//上传图片
    private boolean mImgIsUpdate = false;//图片是否上传完成
    /**
     * 上传完成监听
     */
    PushCompleteListener mCompleteListener;
    /**
     * 上传数据
     */
    ProgramBean mBody;
    private MyBinder mMyBinder;
    private int update_img_num = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMG:
                    LogUtil.d(TAG, "UPDATE_IMG:" + mUpdateImages.size());
                    LogUtil.d(TAG, "UPDATE_IMG:" + update_img_num);

                    if (update_img_num < mUpdateImages.size()) {
                        mFilePresenter.updateFile(mUpdateImages.get(update_img_num), IMG_UPDATE);
                        update_img_num++;
                    } else {
                        mBody.getImages().clear();
                        for (String url : mImgUrl) {
                            mBody.getImages().add(url);
                        }
                        updateProgram();
                    }
                    break;
            }
        }
    };
    private FilePresenter mFilePresenter;
    private ArrayList<String> mUpdateImages;
    private ArrayList<String> mImgUrl = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");

    }

    @Override
    public void showProgress(int requestTag) {
    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        LogUtil.d(TAG, "loadDataSuccess:" + requestTag);
        if (requestTag == PROGRAM_UPDATE) {
            UpdateProgramResponse response = (UpdateProgramResponse) data;
            mCompleteListener.updateSucceed(response.getData());
        } else if (requestTag == IMG_UPDATE) {//上传图片成功
            mImgIsUpdate = true;
            mImgUrl.add(data.getMsg());
            //下一张
            mHandler.sendEmptyMessage(UPDATE_IMG);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        mCompleteListener.updateDefeated();
    }

    public class MyBinder extends Binder {
        //监听上传结果
        public void sendCompleteListener(PushCompleteListener completeListener) {
            mCompleteListener = completeListener;
        }

        //传递上传数据
        public void sendPushData(ProgramBean body) {
            LogUtil.d(TAG, "sendPushData:" + body);
            mBody = body;
        }

        /**
         * 上传图片
         */
        public void startPush() {
            Gson gson = new Gson();
            String toJson = gson.toJson(mBody);
            LogUtil.d(TAG, "tojson:" + toJson);
            if (mBody.getImages().size() > 0) {
                mFilePresenter = new FilePresenter(ProgramPushService.this, ProgramPushService.this);
                mUpdateImages = mBody.getImages();
                mHandler.sendEmptyMessage(UPDATE_IMG);
            } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                updateProgram();
            }
        }
    }

    /**
     * 上传节目
     */
    public void updateProgram() {
        UploadProgramPresenter presenter = new UploadProgramPresenter(this, this);
        LogUtil.d(TAG, "updateProgram：" + mBody);
        presenter.uploadProgram(mBody, PROGRAM_UPDATE);
    }
    public interface PushCompleteListener {
        void updateSucceed(String programId);// 上传成功

        void updateDefeated();// 上传失败
    }
}
