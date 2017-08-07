package com.jld.InformationRelease.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.UpdateProgramResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.model.FileModel;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.UploadProgramPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;
import java.util.jar.Manifest;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 后台节目推送
 */
public class ProgramPushService extends Service implements IViewListen<BaseResponse> {

    private static final String TAG = "ProgramPushService";
    private static final int IMG_UPDATE = 0x11;//图片上传请求码
    private static final int PROGRAM_UPDATE = 0x12;//节目上传请求码
    private static final int UPLOAD_COVER_REQUEST = 0x13;//上传封面
    private static final int VIDEO_UPDATE = 0x14;//上传视频
    private static final int DAY_PROGRAM_UPDATE = 0x15;//每日任务上传请求码
    private static final int UPDATE_IMG = 0x01;//上传图片
    private static final int UPLOAD_VIDEO = 0x02;//上传视频
    private static final int UPDATE_VIDEO_DEFEAT = 0x03;//上传视频失败
    private static final int NEXT_VIDEO_PATH = 0x04;//挑选下一个视频文件
    private static final int UPLOAD_DAY_TASK_PROGRAM = 0x05;//每日任务节目上传
    /**
     * 上传完成监听
     */
    PushCompleteListener mCompleteListener;
    /**
     * 上传数据
     */
    ProgramBean mUploadData;
    ProgramBean mUploadDayData;
    private MyBinder mMyBinder;
    private int update_img_num = 0;
    private int update_video_num = 0;
    private int upload_day_program_num = 0;
    /**
     * 是否在上传"每日任务"
     */
    private boolean isLoadDayProgram = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMG://图片上传
                    nextImageUpload();
                    break;
                case NEXT_VIDEO_PATH://上传下一个视频文件
                    nextVideoPath();
                    break;
                case UPDATE_VIDEO_DEFEAT://视频上传失败
                    String defeat = (String) msg.obj;
                    ToastUtil.showToast(ProgramPushService.this, defeat, 3000);
                    mCompleteListener.pushDefeated();
                    break;
                case UPLOAD_DAY_TASK_PROGRAM:
                    if (mDayUploadProgramItem.size() > upload_day_program_num) {
                        ProgramBean dayUploadProgram = mDayUploadProgramItem.get(upload_day_program_num);
                        mLoad_table_id = dayUploadProgram.getTable_id();
                        parseUploadProgram(dayUploadProgram);
                        upload_day_program_num++;
                    } else {//上传完成
                        updateDayTask();
                    }
                    break;
            }
        }
    };
    private FilePresenter mFilePresenter;
    private ArrayList<String> mUpdateImages;
    private ArrayList<String> mUpdateVideos;
    private ArrayList<String> mImgUrl = new ArrayList<>();
    private ArrayList<String> mVideoUrl = new ArrayList<>();
    private Handler mFileUploadHandler;
    private ArrayList<ProgramBean> mDayUploadProgramItem;
    private int mLoad_table_id;

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
            mUploadData.setCover(data.getMsg());
            if (mUploadData.getImages().size() > 0) {
                mHandler.sendEmptyMessage(UPDATE_IMG);
            } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                updateProgram();
            }
        } else if (requestTag == PROGRAM_UPDATE) {//节目上传成功
            UpdateProgramResponse response = (UpdateProgramResponse) data;
            LogUtil.d(TAG, "上传节目成功：" + response.getData());
            if (isLoadDayProgram) {
                mDayUploadProgramItem.get(upload_day_program_num - 1).setProgramId(response.getData());
                mHandler.sendEmptyMessage(UPLOAD_DAY_TASK_PROGRAM);
                mCompleteListener.uploadSucceed(mDayUploadProgramItem.get(upload_day_program_num - 1));
            } else
                mCompleteListener.pushSucceed(response.getData());
        } else if (requestTag == IMG_UPDATE) {//上传图片成功
            mImgUrl.add(data.getMsg());
            //下一张
            mHandler.sendEmptyMessage(UPDATE_IMG);
        } else if (requestTag == VIDEO_UPDATE) {//视频上传成功
            mVideoUrl.add(data.getMsg());
            //下一张
            update_video_num++;
            mHandler.sendEmptyMessage(NEXT_VIDEO_PATH);
        } else if (requestTag == DAY_PROGRAM_UPDATE) {//每日任务上传成功
            UpdateProgramResponse response = (UpdateProgramResponse) data;
            mCompleteListener.pushSucceed(response.getData());
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage());
        mCompleteListener.pushDefeated();
    }

    public class MyBinder extends Binder {
        //监听上传结果
        public void sendCompleteListener(PushCompleteListener completeListener) {
            mCompleteListener = completeListener;
        }

        /**
         * 上传每日任务
         *
         * @param body
         */
        public void uploadDayTask(ProgramBean body, ArrayList<ProgramBean> programBeens) {
            LogUtil.d(TAG, "上传每日任务：" + body);
            isLoadDayProgram = true;
            upload_day_program_num = 0;
            mUploadDayData = body;
            ArrayList<DayTaskItem> dayProgramItem = body.getDayProgram();
            if (mDayUploadProgramItem == null)
                mDayUploadProgramItem = new ArrayList<>();
            else
                mDayUploadProgramItem.clear();

            //提取所有未上传的节目
            for (DayTaskItem item : dayProgramItem) {
                if (TextUtils.isEmpty(item.getProgramLocalId())) {
                    for (ProgramBean program : programBeens) {
                        if (item.getProgramTabId().equals(program.getTable_id() + "")) {
                            program.setSign(MD5Util.getMD5(Constant.S_KEY + program.getUserid()));
                            mDayUploadProgramItem.add(program);
                        }
                    }
                }
            }
            LogUtil.d(TAG, "未上传节目数据：" + mDayUploadProgramItem);
            for (ProgramBean programBean : mDayUploadProgramItem) {
                LogUtil.d(TAG, "未上传节目数据：" + programBean.getTab());
            }
            //如果有未上传的节目先上传
            if (mDayUploadProgramItem.size() > 0) {
                mHandler.sendEmptyMessage(UPLOAD_DAY_TASK_PROGRAM);
            } else {//否则上传“每日任务”
                updateDayTask();
            }
        }
        /**
         * 开始上传
         */
        public void startPush(ProgramBean body) {
            LogUtil.d(TAG, "开始上传:" + body);
            initData();
            parseUploadProgram(body);
        }

        private void initData() {
            update_img_num = 0;
            update_video_num = 0;
            isLoadDayProgram = false;
            mImgUrl.clear();
            mVideoUrl.clear();
        }
    }

    /**
     * 解析并上传节目文件
     */
    public void parseUploadProgram(ProgramBean body) {
        mUploadData = body;
        Gson gson = new Gson();
        String toJson = gson.toJson(mUploadData);
        LogUtil.d(TAG, "单个节目上传_tab:" + mUploadData.getTab());
        LogUtil.d(TAG, "单个节目上传_toJson:" + toJson);
        mFilePresenter = new FilePresenter(ProgramPushService.this, ProgramPushService.this);
        mUpdateVideos = mUploadData.getVideos();
        if (mUpdateVideos.size() > 0) {//上传视频文件
            videoUpload();
            mHandler.sendEmptyMessage(NEXT_VIDEO_PATH);
            return;
        }
        mUpdateImages = mUploadData.getImages();
        //上传封面
        if (mUploadData.getCover().equals("") || mUploadData.getCover().contains("http://admsgimg.torsun.cn")) {//封面为空或者上传过则直接上传轮播图
            if (mUploadData.getImages().size() > 0) {
                mHandler.sendEmptyMessage(UPDATE_IMG);
            } else {//如果图片为空，或者图片上传过（上传失败重新上传），则直接上传节目
                updateProgram();
            }
        } else//否则上传轮播图
            mFilePresenter.updateFile(mUploadData.getCover(), UPLOAD_COVER_REQUEST);
    }

    /**
     * 上传节目
     */
    public void updateProgram() {
        UploadProgramPresenter presenter = new UploadProgramPresenter(this, this);
        mUploadData.setProgramId(null);
        if (isLoadDayProgram)
            mUploadData.setType("-1");
        else
            mUploadData.setType("1");
        LogUtil.d(TAG, "updateProgram：" + mUploadData);
        presenter.uploadProgram(mUploadData, PROGRAM_UPDATE);
    }

    public void updateDayTask() {
        LogUtil.d(TAG, "被加载节目ID的数据：" + mDayUploadProgramItem);
        ArrayList<DayTaskItem> dayProgram = mUploadDayData.getDayProgram();
        for (int i = 0; i < dayProgram.size(); i++) {
            for (ProgramBean programBean : mDayUploadProgramItem) {
                if (dayProgram.get(i).getProgramTabId().equals(programBean.getTable_id() + "")) {
                    dayProgram.get(i).setProgramLocalId(programBean.getProgramId());
                }
            }
        }
        mUploadDayData.setDayProgram(dayProgram);
        UploadProgramPresenter presenter = new UploadProgramPresenter(this, this);
        LogUtil.d(TAG, "上传每日任务2：" + mUploadDayData);
        mUploadDayData.setProgramId(null);
        mUploadDayData.setType("2");
        presenter.uploadProgram(mUploadDayData, DAY_PROGRAM_UPDATE);
    }

    /**
     * 上传结果监听
     */
    public interface PushCompleteListener {
        /**
         * 上传推送成功
         * @param programId
         */
        void pushSucceed(String programId);// 上传成功

        void pushDefeated();// 上传失败

        /**
         * 上传成功
         * @param programBean
         */
        void uploadSucceed(ProgramBean programBean);
    }

    //需要上传的视频文件
    private String uploadVideoPath = "";

    /**
     * 图片文件上传
     */
    private void nextImageUpload() {
        LogUtil.d(TAG, "UPDATE_IMG:" + mUpdateImages.size());
        LogUtil.d(TAG, "UPDATE_IMG:" + update_img_num);
        if (update_img_num < mUpdateImages.size()) {
            String imgurl = mUpdateImages.get(update_img_num);
            LogUtil.d(TAG, "图片上传:" + imgurl);
            if (imgurl.contains("http://admsgimg.torsun.cn")) {//上传过不再上传
                mImgUrl.add(imgurl);
                update_img_num++;
                mHandler.sendEmptyMessage(UPDATE_IMG);
            } else {
                update_img_num++;
                mFilePresenter.updateFile(imgurl, IMG_UPDATE);
            }
        } else {
            mUploadData.getImages().clear();
            for (String url : mImgUrl) {
                mUploadData.getImages().add(url);
            }
            updateProgram();
        }
    }

    /**
     * 上传视频文件
     */
    private void videoUpload() {
        HandlerThread fileUploadThread = new HandlerThread("file_upload");
        fileUploadThread.start();
        mFileUploadHandler = new Handler(fileUploadThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPLOAD_VIDEO://视频文件上传
                        if (!TextUtils.isEmpty(uploadVideoPath)) {
                            uploadFile(uploadVideoPath);
                        }
                        break;
                }
            }
        };
    }

    /**
     * 上传视频文件
     * @param uploadVideoPath
     */
    private void uploadFile(String uploadVideoPath){
        FileModel.uploadFile2(uploadVideoPath, new FileModel.PushFileListener() {
            @Override
            public void pushSucceed(String fileUrl) {
                LogUtil.d(TAG, "pushSucceed:" + fileUrl);
                mVideoUrl.add(fileUrl);
                mHandler.sendEmptyMessage(NEXT_VIDEO_PATH);
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
    /**
     * 获取下一张需要上传的视频文件路径
     */
    private void nextVideoPath() {
        LogUtil.d(TAG, "NEXT_VIDEO_PATH:" + update_video_num);
        if (update_video_num < mUpdateVideos.size()) {
            uploadVideoPath = mUpdateVideos.get(update_video_num);
            update_video_num++;
            LogUtil.d(TAG, "视频文件上传:" + uploadVideoPath);
            if (uploadVideoPath.contains("http://admsgimg.torsun.cn")) {//上传过不再上传
                mVideoUrl.add(uploadVideoPath);
                mHandler.sendEmptyMessage(NEXT_VIDEO_PATH);
            } else {//上传
                mFileUploadHandler.sendEmptyMessage(UPLOAD_VIDEO);
            }
        } else {//视频上传完成
            mUploadData.getVideos().clear();
            for (String url : mVideoUrl) {
                mUploadData.getVideos().add(url);
            }
            updateProgram();
        }
    }


}
