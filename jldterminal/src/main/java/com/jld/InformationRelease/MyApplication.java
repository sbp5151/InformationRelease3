package com.jld.InformationRelease;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/21 11:07
 */
public class MyApplication extends Application {

    public static Boolean JPush_Alias_Succeed = false;
    private static final String TAG = "MyApplication";
    private String mMac;
    private static final int JPUSH_AGAIN = 0x01;
    private int jpush_again_num = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JPUSH_AGAIN:
                    //jpushInit();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        JPushInterface.init(this);

    }

}
