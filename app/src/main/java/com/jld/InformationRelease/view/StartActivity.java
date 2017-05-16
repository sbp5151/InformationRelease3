package com.jld.InformationRelease.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;

public class StartActivity extends BaseActivity {


    public static final int TO_LOGIN_ACTIVITY = 0x01;
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
        setContentView(R.layout.activity_start);
        mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 1500);
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
    }
}
