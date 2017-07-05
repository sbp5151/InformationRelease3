package com.jld.InformationRelease.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.WriterException;
import com.jld.InformationRelease.JPushReceiver;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.CreateCode;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MacUtil;

import java.io.File;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class QRCodeActivity extends BaseActivity implements JPushReceiver.JPushListener, GetScreenModel.GetScreenListen {

    private static final String TAG = "QRCodeActivity";
    private ImageView mQr_code;
    private static final int JPUSH_AGAIN = 0x12;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case JPUSH_AGAIN:
                    Log.d(TAG, "JPUSH_AGAIN:" + jpush_again_num);
                    jpushSetAlias();
                    break;
            }
        }
    };
    private Bitmap mBitmap;
    private ProgressDialog mLoading;
    private String mMac;
    private boolean isMacNull = false;
    public static Boolean JPush_Alias_Succeed = false;
    private int jpush_again_num = 0;
    private SharedPreferences mSp;
    private int mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.activity_qrcode);
        mQr_code = (ImageView) findViewById(R.id.iv_qrcode);
        mMac = MacUtil.getMac();
        JPushReceiver.sendListener(this);
        mSp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        mResult = mSp.getInt(Constant.JPush_alias_set, -1);
        initWifi();
    }

    /**
     * 判断是否可以获取Mac地址
     */
    private void initWifi() {
        openWifi();//WiFi没打开获取不到Mac地址
        Log.d(TAG, "mac:" + mMac);
        if (mMac == null) {
            isMacNull = true;//监听WiFi状态改变广播，获取MAC地址
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            NetworkReceiver networkReceiver = new NetworkReceiver();
            registerReceiver(networkReceiver, filter);
        } else {
            isMacNull = false;
            if (mResult != 0)
                jpushSetAlias();
            else
                showCode();
        }
    }

    private void jpushSetAlias() {
        final String mdt_mac = MD5Util.getMD5(mMac);
        Log.d(TAG, "mdt_mac:" + mdt_mac);
        JPushInterface.setAlias(this, mdt_mac, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                final SharedPreferences.Editor edit = mSp.edit();
                Log.d(TAG, "i:" + i);
                Log.d(TAG, "s:" + s);
                edit.putInt(Constant.JPush_alias_set, i).apply();
                if (0 == i) {//成功
                    Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_succeed), Toast.LENGTH_SHORT).show();
                    JPush_Alias_Succeed = true;
                    showCode();
                } else if (6002 == i) {//超时
                    jpush_again_num++;
                    if (jpush_again_num <= 10)
                        mHandler.sendEmptyMessageDelayed(JPUSH_AGAIN, 1000 * 10);//10s
                    else
                        Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_net_timeout), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_defeat), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCode() {
        try {
            mBitmap = CreateCode.encodeAsBitmap(mMac, GeneralUtil.dip2px(QRCodeActivity.this, 500));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mQr_code.setImageBitmap(mBitmap);
        if (mLoading != null && mLoading.isShowing())
            mLoading.dismiss();
    }

    //打开wifi
    public void openWifi() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
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
                    if (mResult != 0)
                        jpushSetAlias();
                    else {
                        showCode();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
            case Constant.PUSH_BINDING_SUCCEED://绑定成功:
                Log.d(TAG, "绑定成功:");
                SharedPreferences.Editor edit = this.getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
                edit.putBoolean(Constant.DEVICE_ISBINDING, true);
                edit.putString(Constant.DEVICE_ID, response.getDeviceID());
                edit.apply();
                //保存绑定状态
                toActivity(MainActivity.class);
                finish();
                break;
            case Constant.GET_SCREEN://获取截屏
                String savePath = this.getExternalCacheDir().getAbsolutePath() + File.separator + GeneralUtil.getTimeStr() + "111.png";
                new GetScreenPresenter().getScreen(QRCodeActivity.this, savePath, this);
                break;
        }
    }

    /**
     * 截屏失败
     */
    @Override
    public void onScreenError() {

    }

    /**
     * 截屏成功
     */
    @Override
    public void onScreenComplete() {

    }
}
