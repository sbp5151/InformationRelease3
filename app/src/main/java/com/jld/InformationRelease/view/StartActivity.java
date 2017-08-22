package com.jld.InformationRelease.view;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.CheckVersionRequest;
import com.jld.InformationRelease.bean.response_bean.CheckVersionResponse;
import com.jld.InformationRelease.dialog.AlertTextDialog;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.CheckVersionUpdatePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.FileDownloadUtil;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class StartActivity extends BaseActivity implements IViewListen<CheckVersionResponse> {


    private static final int TO_LOGIN_ACTIVITY = 0x01;
    private static final int CHECK_VERSION_REQUEST_TAG = 0x11;
    private static final String TAG = "StartActivity";
    private long mStartTime;
    private long mStopTime;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TO_LOGIN_ACTIVITY:
                    boolean isLogin = mSp.getBoolean(UserConstant.IS_LOGIN, false);
                    if (isLogin)
                        toActivity(MainActivity.class);
                    else
                        toActivity(LoginActivity.class);
                    finish();
                    break;
            }
        }
    };
    private SharedPreferences mSp;
    private NotificationManager mNotification;
    private NotificationCompat.Builder mNotifyBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY    //点击不显示系统栏
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        setContentView(R.layout.activity_start);
//        mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000);
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        mStartTime = System.currentTimeMillis();
        CheckVersionUpdatePresenter updatePresenter = new CheckVersionUpdatePresenter(this, this);
        updatePresenter.checkVersionUpdate(new CheckVersionRequest(), CHECK_VERSION_REQUEST_TAG);
    }

    @Override
    public void loadDataSuccess(CheckVersionResponse data, int requestTag) {
        LogUtil.d(TAG, "loadDataSuccess:" + data);
        String version = data.getVersion();
        final String url = data.getUrl();
        if (!TextUtils.isEmpty(url) && !version.equals(GeneralUtil.getVersionCode(this) + "")) {
            AlertTextDialog alertTextDialog = new AlertTextDialog(StartActivity.this, getString(R.string.version_update_hint), new AlertTextDialog.OnAlertTextListen() {
                @Override
                public void onConfirm() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            StartActivityPermissionsDispatcher.downloadApkWithCheck(StartActivity.this, url);
                        }
                    }).start();
                    setNotification();
                    mHandler.sendEmptyMessage(TO_LOGIN_ACTIVITY);
                }

                @Override
                public void onCancel() {
                    toMainActivity();
                }
            });
            alertTextDialog.show(getFragmentManager(), "version_update_dialog");
        } else {
            toMainActivity();
        }
    }

    int currentProgress = 0;

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void downloadApk(String url) {
        LogUtil.d(TAG, "downloadApk:" + url);
        FileDownloadUtil downloadUtil = FileDownloadUtil.getInstance();
        downloadUtil.FileDownload(url, this, new FileDownloadUtil.OnFileDownloadListen() {
            @Override
            public void onFailure() {
                LogUtil.d(TAG, "onFailure");
                StartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(StartActivity.this, StartActivity.this.getString(R.string.version_update_fail), 3000);
                    }
                });
                mNotification.cancel(111);
            }

            @Override
            public void onSuccess(String filePath) {
                mHandler.removeMessages(TO_LOGIN_ACTIVITY);
                LogUtil.d(TAG, "onSuccess：" + filePath);
                GeneralUtil.installApp(StartActivity.this, filePath);
                mNotification.cancel(111);
            }

            @Override
            public void onProgress(int progress) {
                if (currentProgress != progress) {
                    LogUtil.d(TAG, "onProgress：" + progress);
                    currentProgress = progress;
                    mNotifyBuilder.setProgress(100, currentProgress, false);
                    mNotification.notify(111, mNotifyBuilder.build());
                }
            }
        });
    }

    /**
     * 设置notification
     */
    private void setNotification() {
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction("downloadNotification");
        int id = (int) (System.currentTimeMillis() / 1000);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //点击的意图ACTION是跳转到Intent
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        /*设置large icon*//*设置small icon*//*设置title*//*设置发出通知的时间为发出通知时的系统时间*//*设置发出通知时在status bar进行提醒*///                .setTicker("来自问月的祝福")
        /*setOngoing(boolean)设为true,notification将无法通过左右滑动的方式清除
        * 可用于添加常驻通知，必须调用cancle方法来清除
        *//*设置点击后通知消失*//*取消时间显示*/
        mNotifyBuilder = new NotificationCompat.Builder(this)
            /*设置large icon*/
                .setLargeIcon(bitmap)
             /*设置small icon*/
                .setSmallIcon(R.mipmap.ic_launcher)
            /*设置title*/
                //.setContentTitle(getResources().getString(R.string.download_ing) + "...0%")
                .setProgress(100, 0, false)
             /*设置发出通知的时间为发出通知时的系统时间*/
                .setWhen(System.currentTimeMillis())
             /*设置发出通知时在status bar进行提醒*/
                //.setTicker("来自问月的祝福")
            /*setOngoing(boolean)设为true,notification将无法通过左右滑动的方式清除
            * 可用于添加常驻通知，必须调用cancle方法来清除
            */
                .setOngoing(false)
             /*设置点击后通知消失*/
                .setAutoCancel(false)
                /*取消时间显示*/
                .setShowWhen(true).setDeleteIntent(pendingIntent);
        mNotification.notify(111, mNotifyBuilder.build());
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        toMainActivity();
    }

    private void toMainActivity() {
        if ((System.currentTimeMillis() - mStartTime) >= 2000)
            mHandler.sendEmptyMessage(TO_LOGIN_ACTIVITY);
        else
            mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000 - (System.currentTimeMillis() - mStartTime));
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }
}
