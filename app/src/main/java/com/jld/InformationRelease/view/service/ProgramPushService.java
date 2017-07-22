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
import com.jld.InformationRelease.model.FileModel;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.UploadProgramPresenter;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;

/**
 * 后台节目推送
 */
public class ProgramPushService extends Service implements IViewListen<BaseResponse> {

    private static final String TAG = "ProgramPushService";
    private static final int IMG_UPDATE = 0x11;//图片上传请求码
    private static final int PROGRAM_UPDATE = 0x12;//节目上传请求码
    private static final int UPLOAD_COVER_REQUEST = 0x13;//上传封面
    private static final int VIDEO_UPDATE = 0x14;//上传视频
    private static final int UPDATE_IMG = 0x01;//上传图片
    private static final int UPDATE_VIDEO = 0x02;//上传视频
    private static final int UPDATE_VIDEO_DEFEAT = 0x03;//上传视频失败
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
    private int update_video_num = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMG:
                    LogUtil.d(TAG, "UPDATE_IMG:" + mUpdateImages.size());
                    LogUtil.d(TAG, "UPDATE_IMG:" + update_img_num);
                    if (update_img_num < mUpdateImages.size()) {
                        String imgurl = mUpdateImages.get(update_img_num);
                        LogUtil.d(TAG, "imgurl:" + imgurl);
                        if (imgurl.contains("http://admsgimg.torsun.cn")) {//上传过不再上传
                            mImgUrl.add(imgurl);
                            update_img_num++;
                            mHandler.sendEmptyMessage(UPDATE_IMG);
                        } else {
                            update_img_num++;
                            mFilePresenter.updateFile(imgurl, IMG_UPDATE);
                        }
                    } else {
                        mBody.getImages().clear();
                        for (String url : mImgUrl) {
                            mBody.getImages().add(url);
                        }
                        updateProgram();
                    }
                    break;
                case UPDATE_VIDEO://上传视频
                    LogUtil.d(TAG, "UPDATE_VIDEO:" + update_video_num);
                    if (update_video_num < mUpdateVideos.size()) {
                        final String videoUrl = mUpdateVideos.get(update_video_num);
                        update_video_num++;
                        LogUtil.d(TAG, "videoUrl:" + videoUrl);
                        if (videoUrl.contains("http://admsgimg.torsun.cn")) {//上传过不再上传
                            mVideoUrl.add(videoUrl);
                            mHandler.sendEmptyMessage(UPDATE_VIDEO);
                        } else {
                            pushFile(videoUrl);
                        }
                    } else {
                        mBody.getVideos().clear();
                        for (String url : mVideoUrl) {
                            mBody.getVideos().add(url);
                        }
                        updateProgram();
                    }
                    break;
                case UPDATE_VIDEO_DEFEAT://视频上传失败
                    String defeat = (String) msg.obj;
                    ToastUtil.showToast(ProgramPushService.this, defeat, 3000);
                    mCompleteListener.updateDefeated();
                    break;
            }
        }
    };
    private FilePresenter mFilePresenter;
    private ArrayList<String> mUpdateImages;
    private ArrayList<String> mUpdateVideos;
    private ArrayList<String> mImgUrl = new ArrayList<>();
    private ArrayList<String> mVideoUrl = new ArrayList<>();

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
        if (requestTag == UPLOAD_COVER_REQUEST) {//封面上传完成，再上传轮播图
            mBody.setCover(data.getMsg());
            if (mBody.getImages().size() > 0) {
                mHandler.sendEmptyMessage(UPDATE_IMG);
            } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                updateProgram();
            }
        } else if (requestTag == PROGRAM_UPDATE) {
            UpdateProgramResponse response = (UpdateProgramResponse) data;
            mCompleteListener.updateSucceed(response.getData());
        } else if (requestTag == IMG_UPDATE) {//上传图片成功
            mImgUrl.add(data.getMsg());
            //下一张
            mHandler.sendEmptyMessage(UPDATE_IMG);
        } else if (requestTag == VIDEO_UPDATE) {
            mVideoUrl.add(data.getMsg());
            //下一张
            update_video_num++;
            mHandler.sendEmptyMessage(UPDATE_VIDEO);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage());
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
         * 开始上传
         */
        public void startPush(ProgramBean body) {
            LogUtil.d(TAG, "开始上传:" + body);
            mBody = body;
            Gson gson = new Gson();
            String toJson = gson.toJson(mBody);
            LogUtil.d(TAG, "tojson:" + toJson);
            mFilePresenter = new FilePresenter(ProgramPushService.this, ProgramPushService.this);
            mUpdateVideos = mBody.getVideos();
            LogUtil.d(TAG, "mUpdateVideos:" + mUpdateVideos);
            if (mUpdateVideos.size() > 0) {//上传视频文件
                mHandler.sendEmptyMessage(UPDATE_VIDEO);
                return;
            }
            mUpdateImages = mBody.getImages();
            //上传封面
            if (mBody.getCover().equals("") || mBody.getCover().contains("http://admsgimg.torsun.cn")) {//封面为空或者上传过则直接上传轮播图
                if (mBody.getImages().size() > 0) {
                    mHandler.sendEmptyMessage(UPDATE_IMG);
                } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                    updateProgram();
                }
            } else//否则上传
                mFilePresenter.updateFile(mBody.getCover(), UPLOAD_COVER_REQUEST);
        }
    }

    /**
     * 上传节目
     */
    public void updateProgram() {
        UploadProgramPresenter presenter = new UploadProgramPresenter(this, this);
        LogUtil.d(TAG, "updateProgram：" + mBody);
        mBody.setProgramId(null);
        presenter.uploadProgram(mBody, PROGRAM_UPDATE);
    }

    /**
     * 上传结果监听
     */
    public interface PushCompleteListener {
        void updateSucceed(String programId);// 上传成功
        void updateDefeated();// 上传失败
    }

    /**
     * 上传视频文件
     *
     * @param path
     */
    public void pushFile(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileModel.uploadFile2(path, new FileModel.PushFileListener() {
                    @Override
                    public void pushSucceed(String fileUrl) {
                        LogUtil.d(TAG, "pushSucceed:" + fileUrl);
                        mVideoUrl.add(fileUrl);
                        mHandler.sendEmptyMessage(UPDATE_VIDEO);
                    }

                    @Override
                    public void pushDefeat(String defeat) {
                        LogUtil.d(TAG, "pushDefeat:" + defeat);
                        Message message = new Message();
                        message.obj = defeat;
                        message.what = UPDATE_VIDEO_DEFEAT;
                        mHandler.sendMessage(message);
                    }
                });
            }
        }).start();
    }
}
