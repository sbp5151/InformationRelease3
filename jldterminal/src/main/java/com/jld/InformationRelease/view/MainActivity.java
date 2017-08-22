package com.jld.InformationRelease.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import com.jld.InformationRelease.bean.request.CheckBindRequest;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.CheckBindPresenter;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.presenter.ProgramLoadSucceedPresenter;
import com.jld.InformationRelease.presenter.UploadScreenPresenter;
import com.jld.InformationRelease.service.DayTaskService;
import com.jld.InformationRelease.service.SpotsService;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.DeviceUtil;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MacUtil;
import com.jld.InformationRelease.util.ModelIds;
import com.jld.InformationRelease.util.VolumeUtil;
import com.jld.InformationRelease.view.fragment.DefaultFragment;
import com.jld.InformationRelease.view.fragment.IjkVideoFragment;
import com.jld.InformationRelease.view.fragment.ProgramImageFragment;
import com.jld.InformationRelease.view.fragment.ProgramTextFragment;

import java.io.File;

public class MainActivity extends BaseActivity implements JPushReceiver.JPushListener, IViewToPresenter<BaseResponse>, SpotsService.OnSportStopListen {

    private static final String TAG = "MainActivity";
    //加载节目请求标识
    private static final int LOAD_PROGRAM_TAG = 0x11;
    //节目加载成功反馈请求标识
    private static final int LOAD_PROGRAM_BACK = 0x13;
    private static final int UPLOAD_SCREEN_REQUESTID = 0x21;
    private static final int GET_SCREEN_URL_REQUESTTAG = 0x22;
    //节目替换
    public static final int REPLACE_FRAGMENT = 0x24;
    //隐藏系统UI
    public static final int HIED_SYSTEM_UI = 0x25;
    //检查设备是否被绑定
    public static final int CHECK_BIND_REQEST = 0x26;
    //在线时间更新
    public static final int UPDATE_TIME_REQUEST = 0x27;
    //当前节目ID
    private String mProgramID;
    //当前加载的节目ID
    private String mLoadProgramID;

    //每日任务服务是否处于绑定状态
    private boolean mTaskServiceIsBind = false;
    //插播是否处于绑定状态
    private boolean mSpotsServiceIsBind = false;
    //activity是否处于stop状态
    private boolean mIsStop = false;

    //当前节目数据
    ProgramResponseBean mProgramData;

    //当应用返回到桌面 此时加载的数据
    ProgramResponseBean mSaveProgramData;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REPLACE_FRAGMENT://节目替换
                    ProgramResponseBean data = (ProgramResponseBean) msg.obj;
                    if (data != null) {
                        replaceFragment(data);
                    }
                    break;
                case HIED_SYSTEM_UI:
                    hiedSystemUi();
                    break;
            }
        }
    };
    private DayTaskService.MyBinder mMyBinder;
    private NetworkReceiver mNetworkReceiver;
    private SharedPreferences mSp;
    private SpotsService.SpotsBinder mSpotsBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        hiedSystemUi();
        setContentView(R.layout.activity_main);
        //注册极光推送监听
        JPushReceiver.sendListener(this);
        dataSave(null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkReceiver = new NetworkReceiver();
        registerReceiver(mNetworkReceiver, filter);
        mSp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        //闹钟测试
//        DayTask.alarmTask(MainActivity.this);
    }

    public void hiedSystemUi() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY    //点击不显示系统栏
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
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
                loadProgram(response.getProgramID());
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
     * 根据节目ID加载节目
     *
     * @param programID 节目ID
     */
    private void loadProgram(String programID) {
        if (!TextUtils.isEmpty(programID)) {
            LogUtil.d(TAG, "加载节目:" + programID);
            LoadProgramPresenter presenter = new LoadProgramPresenter(this, MainActivity.this);
            mLoadProgramID = programID;
            presenter.LoadProgram(programID, LOAD_PROGRAM_TAG);
        } else
            LogUtil.e(TAG, "节目ID为空");
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

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage());
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program_error), Toast.LENGTH_SHORT).show();
        } else if (requestTag == LOAD_PROGRAM_BACK) {
            LogUtil.d(TAG, "反馈失败");
        }
    }

    /**
     * 节目加载成功
     */
    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            ProgramResponseBean programResponseBean = (ProgramResponseBean) data;
            LogUtil.d(TAG, "加载节目成功并反馈:" + mLoadProgramID);
            Toast.makeText(this, getString(R.string.load_program_succeed), Toast.LENGTH_SHORT).show();
            //数据处理
            dataSave(programResponseBean);
            //加载成功数据反馈
            SharedPreferences sp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
            String deviceId = sp.getString(Constant.DEVICE_ID, "");
            ProgramLoadSucceedPresenter presenter = new ProgramLoadSucceedPresenter(this, MainActivity.this);
            presenter.programLoadSucceedBack(deviceId, mLoadProgramID, LOAD_PROGRAM_BACK);
        } else if (requestTag == LOAD_PROGRAM_BACK) {
            LogUtil.d(TAG, "反馈成功");
        } else if (requestTag == CHECK_BIND_REQEST) {//绑定检查
            String result = data.getResult();
            if (result.equals("0")) {//已绑定
                mSp.edit().putBoolean(Constant.DEVICE_ISBINDING, true).apply();
            } else {//未绑定
                mSp.edit().putBoolean(Constant.DEVICE_ISBINDING, false).apply();
                toActivity(QRCodeActivity.class);
                finish();
            }
        }
    }

    /**
     * 数据保存
     *
     * @param data
     */
    private void dataSave(ProgramResponseBean data) {
        LogUtil.d(TAG, "数据处理:" + data);
        if (data != null) {//保存数据
            if (!data.getItem().getType().equals(Constant.PROGRAM_TYPE_URGENCY)) {//插播节目不用保存数据
                String play_data = new Gson().toJson(data);
                SharedPreferences.Editor sp_edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
                sp_edit.putString(Constant.PLAY_DATA, play_data);
                sp_edit.apply();
            }
        } else if (data == null) {//展示历史数据
            String play_data = getSharedPreferences(Constant.share_key, MODE_PRIVATE).getString(Constant.PLAY_DATA, "");
            LogUtil.d(TAG, "展示历史数据:" + play_data);
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
        dataDispose(data);
    }

    /**
     * 节目数据处理
     *
     * @param data
     */
    private void dataDispose(ProgramResponseBean data) {
        mProgramData = data;
        mProgramID = mLoadProgramID;
        //停止每日服务
        stopTaskService();
        //停止插播服务
        stopSpotsService();
        if (data.getItem().getType().equals(Constant.PROGRAM_TYPE_DAY)) {//每日任务
            LogUtil.d(TAG, "每日任务数据加载完毕:" + mProgramData);
            startTaskService();
        } else if (data.getItem().getType().equals(Constant.PROGRAM_TYPE_COMMON)) {//普通任务
            LogUtil.d(TAG, "普通任务加载完毕:" + mProgramData);
            replaceFragment(data);
        } else if (data.getItem().getType().equals(Constant.PROGRAM_TYPE_URGENCY)) {//插播任务
            LogUtil.d(TAG, "插播任务加载完毕:" + mProgramData);
            //开启插播service
            Intent intent = new Intent(MainActivity.this, SpotsService.class);
            startService(intent);
            bindService(intent, mConn, BIND_AUTO_CREATE);
        }
    }

    //插播节目 停止播放
    @Override
    public void onProgramStop() {
        LogUtil.d(TAG, "onProgramStop");
        dataSave(null);//插播节目结束，继续播放原来数据
        stopSpotsService();//停止插播服务
    }

    //插播节目 开始播放
    @Override
    public void onProgramStart(ProgramResponseBean data) {
        LogUtil.d(TAG, "onProgramStart:" + data);
        replaceFragment(data);
    }

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSpotsBinder = (SpotsService.SpotsBinder) iBinder;
            mSpotsServiceIsBind = true;
            DayTaskItem SpotsData = mProgramData.getItem().getDayProgram().get(0);
            mSpotsBinder.setOnSportStopListen(MainActivity.this);
            mSpotsBinder.setData(SpotsData);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpotsServiceIsBind = false;
        }
    };

    /**
     * 停止插播服务
     */
    private void stopSpotsService() {
        if (mSpotsBinder != null) {
            mSpotsBinder.stopService();
            if (mSpotsServiceIsBind) {
                unbindService(mConn);
                mSpotsServiceIsBind = false;
            }
        }
    }

    /**
     * 停止每日任务服务
     */
    private void stopTaskService() {
        if (mMyBinder != null) {
            mMyBinder.stopLoop();
            if (mTaskServiceIsBind) {
                unbindService(con);
                mTaskServiceIsBind = false;
            }
        }
    }

    //节目替换
    private synchronized void replaceFragment(ProgramResponseBean data) {
        if (mIsStop) {
            mSaveProgramData = data;
            return;
        }
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
                    IjkVideoFragment videoFragment = IjkVideoFragment.getInstance(data.getItem().getVideos());
                    ft.replace(R.id.framelayout_main, videoFragment);
                    MainActivity.this.setRequestedOrientation(//通过程序改变屏幕显示的方向
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR);//横屏
                    break;
            }
        }
        ft.commit();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        LogUtil.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtil.d(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG, "onStop");
        mIsStop = true;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSaveProgramData != null) {
            replaceFragment(mSaveProgramData);
            mSaveProgramData = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG, "dispatchTouchEvent");
        mHandler.removeMessages(HIED_SYSTEM_UI);
        mHandler.sendEmptyMessageDelayed(HIED_SYSTEM_UI, 3000);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG, "onRestart");
        mIsStop = false;
    }

    /**
     * 开启时间循环服务，定时启动节目任务
     */
    public void startTaskService() {
        Intent intent = new Intent(this, DayTaskService.class);
        startService(intent);
        bindService(intent, con, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMyBinder = (DayTaskService.MyBinder) iBinder;
            mMyBinder.sendHandler(mHandler);
            mMyBinder.sendTaskTimes(mProgramData.getItem().getDayProgram());
            mMyBinder.startLoop();
            mTaskServiceIsBind = true;
            LogUtil.d(TAG, "绑定服务");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mTaskServiceIsBind = false;
            LogUtil.d(TAG, "服务解绑");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        if (mTaskServiceIsBind && mMyBinder != null)
            unbindService(con);
        if (mNetworkReceiver != null)
            unregisterReceiver(mNetworkReceiver);
        if (mSpotsBinder != null)
            unbindService(mConn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkBind() {
        //检查设备是否被绑定
        CheckBindPresenter checkBindPresenter = new CheckBindPresenter(this, this);
        String mDecIMEI = MacUtil.getIMEI(this);
        String sign = MD5Util.getMD5(Constant.S_KEY + mDecIMEI);
        CheckBindRequest body = new CheckBindRequest(mDecIMEI, sign);
        checkBindPresenter.deviceCheckBind(body, CHECK_BIND_REQEST);
    }

    class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG, "onReceive:" + intent.getAction());
            checkBind();
        }
    }
}
