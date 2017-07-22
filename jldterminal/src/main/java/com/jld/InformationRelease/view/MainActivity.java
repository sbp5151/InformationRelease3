package com.jld.InformationRelease.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jld.InformationRelease.JPushReceiver;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.base.SimpleIViewToPresenter;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.presenter.ProgramLoadSucceedPresenter;
import com.jld.InformationRelease.presenter.UploadScreenPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.DeviceUtil;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ModelIds;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.VolumeUtil;
import com.jld.InformationRelease.view.fragment.DefaultFragment;
import com.jld.InformationRelease.view.fragment.ProgramImageFragment;
import com.jld.InformationRelease.view.fragment.ProgramTextFragment;
import com.jld.InformationRelease.view.fragment.ProgramVideoFragment;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements JPushReceiver.JPushListener, IViewToPresenter<BaseResponse> {

    //加载节目请求标识
    private static final int LOAD_PROGRAM_TAG = 0x12;
    //节目加载成功反馈请求标识
    private static final int LOAD_PROGRAM_BACK = 0x13;
    private static final String TAG = "MainActivity";
    private static final int UPLOAD_SCREEN_REQUESTID = 0x21;
    private static final int GET_SCREEN_URL_REQUESTTAG = 0x22;
    private String[] names1 = {"原味奶茶", "香芋奶茶", "芒果奶茶", "草莓奶茶", "哈密瓜奶茶", "珍珠奶茶"};
    private String[] names2 = {"芒果奶昔", "草莓奶昔", "芦荟奶昔", "蓝莓奶昔", "水蜜桃奶昔", "哈密瓜奶昔"};
    private ProgressDialog mPdialog;
    private FrameLayout mFrameLayout;
    private String mProgramID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout_main);

        //注册极光推送监听
        JPushReceiver.sendListener(this);
        mPdialog = new ProgressDialog(this);
        dataDispose(null);
    }

    /**
     * 极光推送返回监听
     *
     * @param pushMsg
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void pushMessage(String pushMsg) {
        Gson gson = new Gson();
        PushResponse response;
        try {
            response = gson.fromJson(pushMsg, PushResponse.class);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, e.toString());
            return;
        }
        if (response == null)
            throw new IllegalArgumentException("推送json异常 ");
        switch (response.getResult()) {
            case Constant.PUSH_UNBIND://解绑
                SharedPreferences.Editor edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
                edit.putBoolean(Constant.DEVICE_ISBINDING, false).apply();
                toActivity(QRCodeActivity.class);
                finish();
                break;
            case Constant.PUSH_PROGRAM://节目推送
                //加载节目更新
                //节目ID
                mProgramID = response.getProgramID();
                LogUtil.d(TAG, "加载节目:" + mProgramID);
                LoadProgramPresenter presenter = new LoadProgramPresenter(this, MainActivity.this);
                presenter.LoadProgram(mProgramID, LOAD_PROGRAM_TAG);
                break;
            case Constant.SHUTDOWN://关机
                DeviceUtil.showDown();
                break;
            case Constant.RESTART://重启
                DeviceUtil.restart();
                break;
            case Constant.VOLUME_SETTING://音量调节
                Log.d(TAG, "current:" + VolumeUtil.getCurrentVolume(this));
                Log.d(TAG, "max:" + VolumeUtil.getMaxVolume(this));
                String volume = response.getVolume();
                Log.d(TAG, "volume:" + volume);
                try {
                    int volumei = Integer.parseInt(volume);
                    DeviceUtil.volumeSetting(this, volumei);
                } catch (NumberFormatException e) {
                    Log.d(TAG, "NumberFormatException:" + e);
                }
                break;
            case Constant.GET_SCREEN://获取截屏
                String savePath = MainActivity.this.getExternalCacheDir().getAbsolutePath() + File.separator + GeneralUtil.getTimeStr() + "111.png";
                Log.e(TAG, "absolutePath:" + savePath);
                getScreen(savePath);
                break;
        }
    }

    /**
     * 获取截屏
     *
     * @param savePath 截屏保存地址
     */
    public void getScreen(final String savePath) {
        new GetScreenPresenter().getScreen(this, savePath, new GetScreenModel.GetScreenListen() {
            @Override
            public void onScreenError() {//获取失败
            }

            @Override
            public void onScreenComplete() {//获取成功
                //上传截屏文件获取截屏url
                getScreenUrl(savePath);
            }
        });
    }

    /**
     * 上传截屏文件获取截屏url
     *
     * @param screenPath 截屏文件目录
     */
    public void getScreenUrl(String screenPath) {
        //上传图片，获取图片链接
        FilePresenter filePresenter = new FilePresenter(MainActivity.this, new SimpleIViewToPresenter<FileResponseBean>() {

            @Override
            public void loadDataSuccess(FileResponseBean data, int requestTag) {

                String fileUrl = data.getFileUrl();
                if (!TextUtils.isEmpty(fileUrl)) {
                    //上传截屏
                    uploadScreen(fileUrl);
                }
            }

            @Override
            public void loadDataError(Throwable e, int requestTag) {

            }
        });
        filePresenter.updateFile(screenPath, GET_SCREEN_URL_REQUESTTAG);
    }

    /**
     * 上传截屏
     *
     * @param screenUrl 截屏url
     */
    public void uploadScreen(String screenUrl) {

        UploadScreenPresenter uploadScreenPresenter = new UploadScreenPresenter(new SimpleIViewToPresenter<BaseResponse>() {
            @Override
            public void loadDataSuccess(BaseResponse data, int requestTag) {
                Toast.makeText(MainActivity.this, data.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loadDataError(Throwable e, int requestTag) {
            }
        }, MainActivity.this);
        uploadScreenPresenter.uploadScreen(screenUrl, UPLOAD_SCREEN_REQUESTID);
    }

    @Override
    public void showProgress(int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void hideProgress(int requestTag) {
    }

    //节目加载成功
    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            ProgramResponseBean programResponseBean = (ProgramResponseBean) data;
            LogUtil.d(TAG, "加载节目成功并反馈:" + mProgramID);
            Toast.makeText(this, getString(R.string.load_program_succeed), Toast.LENGTH_SHORT).show();
            dataDispose(programResponseBean);
            //加载成功数据反馈
            SharedPreferences sp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
            String deviceId = sp.getString(Constant.DEVICE_ID, "");
            ProgramLoadSucceedPresenter presenter = new ProgramLoadSucceedPresenter(this, MainActivity.this);
            presenter.programLoadSucceedBack(deviceId, mProgramID, LOAD_PROGRAM_BACK);
        } else if (requestTag == LOAD_PROGRAM_BACK) {
            LogUtil.d(TAG, "反馈成功");
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage());
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program_error), Toast.LENGTH_SHORT).show();
        } else if (requestTag == LOAD_PROGRAM_BACK) {
            LogUtil.d(TAG, "反馈失败");
        }
    }

    private void dataDispose(ProgramResponseBean data) {
        LogUtil.d(TAG, "replaceFragment:" + data);
        if (data != null) {//保存数据
            String play_data = new Gson().toJson(data);
            SharedPreferences.Editor sp_edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
            sp_edit.putString(Constant.PLAY_DATA, play_data);
            sp_edit.apply();
        } else {//展示历史数据
            String play_data = getSharedPreferences(Constant.share_key, MODE_PRIVATE).getString(Constant.PLAY_DATA, "");
            LogUtil.d(TAG, "展示历史数据:"+play_data);
            if (!TextUtils.isEmpty(play_data)) {
                data = new Gson().fromJson(play_data, ProgramResponseBean.class);
                LogUtil.d(TAG, "data:" + data);
                if (data == null) {//没有数据加载默认布局
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DefaultFragment defaultFragment = new DefaultFragment();
                    ft.replace(R.id.framelayout_main, defaultFragment);
                    ft.commit();
                    return;
                }
            } else {
                replaceFragment(data);
                return;
            }
        }
        if (data.getItem().getType().equals(Constant.PROGRAM_TYPE_DAY)) {//每日任务
            LogUtil.d(TAG, "加载每日任务:");
            DayTask.createDayTask(MainActivity.this, data.getItem().getDayProgram());
            dayItem = data.getItem().getDayProgram();
        } else if (data.getItem().getType().equals(Constant.PROGRAM_TYPE_COMMON)) {//普通任务
            LogUtil.d(TAG, "加载普通任务:");
            replaceFragment(data);
        }
    }

    ArrayList<DayTaskItem> dayItem;

    //节目替换
    private void replaceFragment(ProgramResponseBean data) {
        LogUtil.d(TAG, "节目替换：" + data);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MainActivity.this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        if (data == null) {//等待接收节目
            DefaultFragment defaultFragment = new DefaultFragment();
            ft.replace(R.id.framelayout_main, defaultFragment);
        } else {
            //展示数据
            String modelId = data.getItem().getModelId();
            switch (modelId) {
                case ModelIds.NAICHA_MODEL_1://text
                    ProgramTextFragment fragment1 = ProgramTextFragment.getInstance(data);
                    ft.replace(R.id.framelayout_main, fragment1);
                    break;
                case ModelIds.IMAGE_MODEL://image
                    ProgramImageFragment fragment2 = ProgramImageFragment.getInstance(data);
                    ft.replace(R.id.framelayout_main, fragment2);
                    break;
                case ModelIds.VIDEO_MODEL://video
                    ProgramVideoFragment videoFragment = new ProgramVideoFragment(data.getItem().getVideos());
                    ft.replace(R.id.framelayout_main, videoFragment);
                    MainActivity.this.setRequestedOrientation(//通过程序改变屏幕显示的方向
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR);//横屏
                    break;
            }
        }
        ft.commit();
    }

    public class DayTaskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "DayTaskReceiver:" + action);
            if (action.equals("load_program")) {
                long cuTime = System.currentTimeMillis();
                for (DayTaskItem item : dayItem) {
                    if (Long.parseLong(TimeUtil.dateBack(item.getStateTime())) <= cuTime &&
                            cuTime < Long.parseLong(TimeUtil.dateBack(item.getStopTime()))) {
                        String programId = item.getProgramLocalId();
                        LogUtil.d(TAG, "加载节目:" + programId);
                        LoadProgramPresenter presenter = new LoadProgramPresenter(MainActivity.this, MainActivity.this);
                        presenter.LoadProgram(programId, LOAD_PROGRAM_TAG);
                        return;
                    }
                }
            }
        }
    }
}
