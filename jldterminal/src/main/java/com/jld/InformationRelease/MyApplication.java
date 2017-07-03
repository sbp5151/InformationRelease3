package com.jld.InformationRelease;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MacUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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
    private boolean isMacNull = false;
    private static final int JPUSH_AGAIN = 0x01;
    private int jpush_again_num = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JPUSH_AGAIN:
                    jpushInit();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        openWifi();//WiFi没打开获取不到Mac地址
        mMac = MacUtil.getMac();
        Log.d(TAG, "mac:" + mMac);
        if (mMac == null) {
            isMacNull = true;//监听WiFi状态改变广播，获取MAC地址
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            NetworkReceiver networkReceiver = new NetworkReceiver();
            registerReceiver(networkReceiver, filter);
        } else {
            isMacNull = false;
            jpushInit();
        }
    }

    private void jpushInit() {
        JPushInterface.init(this);
        final SharedPreferences sp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        int is_alias = sp.getInt(Constant.JPush_alias_set, 1);
        Log.d(TAG, "is_alias:" + is_alias);
        if (is_alias == 0)//设置过就不再设置
            return;
        final String mdt_mac = MD5Util.getMD5(mMac);
        Log.d(TAG, "mdt_mac:" + mdt_mac);
        JPushInterface.setAlias(this, mdt_mac, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                final SharedPreferences.Editor edit = sp.edit();
                Toast.makeText(MyApplication.this, s, Toast.LENGTH_SHORT).show();
                Log.d("JPush", "i:" + i);
                Log.d("JPush", "s:" + s);
                edit.putInt(Constant.JPush_alias_set, i).apply();
                if (0 == i) {//成功
                    Toast.makeText(MyApplication.this, getString(R.string.set_alias_succeed), Toast.LENGTH_SHORT).show();
                    JPush_Alias_Succeed = true;
                } else if (6002 == i) {//超时
                    jpush_again_num++;
                    if (jpush_again_num <= 5)
                        mHandler.sendEmptyMessageDelayed(JPUSH_AGAIN, 1000 * 10);//10s
                    else
                        Toast.makeText(MyApplication.this, getString(R.string.set_alias_net_timeout), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MyApplication.this, getString(R.string.set_alias_defeat), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //打开wifi
    public void openWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG, "onReceive:" + intent.getAction());
            if (isMacNull) {
                mMac = MacUtil.getMac();
                if (mMac == null)
                    isMacNull = true;
                else {
                    isMacNull = false;
                    jpushInit();
                }
            }
        }
    }
}
