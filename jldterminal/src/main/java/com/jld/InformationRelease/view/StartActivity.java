package com.jld.InformationRelease.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.Constant;

public class StartActivity extends BaseActivity {

    public static final int TO_LOGIN_ACTIVITY = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TO_LOGIN_ACTIVITY:
                    if (mIsBinding)
                        toActivity(MainActivity.class);
                    else
                        toActivity(QRCodeActivity.class);
                    finish();
                    break;
            }
        }
    };
    private boolean mIsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000);
        SharedPreferences sp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        mIsBinding = sp.getBoolean(Constant.DEVICE_ISBINDING, false);
    }
}
