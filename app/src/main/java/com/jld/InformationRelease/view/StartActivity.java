package com.jld.InformationRelease.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MacUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;

public class StartActivity extends BaseActivity {


    private static final int TO_LOGIN_ACTIVITY = 0x01;
    private static final String TAG = "StartActivity";
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
        mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000);
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor edit = mSp.edit();
        String mac = MacUtil.getMac();
        LogUtil.d(TAG, "mac:" + mac);
        if (mac == null) {
            edit.putString(UserConstant.USER_ID, "111");
            edit.putBoolean(UserConstant.IS_LOGIN, true).apply();
        }
        Intent intent = new Intent("");
        sendBroadcast(intent);
    }
}
