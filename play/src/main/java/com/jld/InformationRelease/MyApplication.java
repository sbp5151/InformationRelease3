package com.jld.InformationRelease;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import com.danikula.videocache.HttpProxyCacheServer;
import com.jld.InformationRelease.util.L;

import cn.jpush.android.api.JPushInterface;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/21 11:07
 */
public class MyApplication extends Application {
    /**
     * 极光推送服务别名设置是否成功
     */
    public static boolean JPush_Alias_Succeed = false;
    /**
     * 网络是否可以访问
     */
    public static boolean network_is_connect = false;
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d("onCreate");
        JPushInterface.init(this);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            network_is_connect = true;
        } else{
            openWifi();
            network_is_connect = false;
            Toast.makeText(this,getString(R.string.network_error),Toast.LENGTH_SHORT).show();
        }
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memorySize = activityManager.getMemoryClass();
        L.d(TAG, "memorySize:" + memorySize);
    }
    //打开wifi
    public void openWifi() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

}
