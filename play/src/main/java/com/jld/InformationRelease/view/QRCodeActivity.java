package com.jld.InformationRelease.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.jld.InformationRelease.MyApplication;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.CreateCode;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.L;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MacUtil;

import java.io.File;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.jld.InformationRelease.MyApplication.network_is_connect;

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
    private String mDecIMEI;
    private int jpush_again_num = 0;
    private SharedPreferences mSp;
    private NetworkReceiver mNetworkReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
        setContentView(R.layout.activity_qrcode);
        mSp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        mQr_code = (ImageView) findViewById(R.id.iv_qrcode);
        mDecIMEI = MacUtil.getMac();
        L.d(TAG, "设备IMEI:" + mDecIMEI);
        JPushReceiver.sendListener(this);
        //注册网络改变广播接收者
        mNetworkReceiver = new NetworkReceiver();
        //设置极光推送别名
        jpushSetAlias();
    }


    private void jpushSetAlias() {
        final String mdt_mac = MD5Util.getMD5(mDecIMEI);
        L.d(TAG, "设置别名：" + mdt_mac);
        JPushInterface.setAlias(this, mdt_mac, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                final SharedPreferences.Editor edit = mSp.edit();
                edit.putInt(Constant.JPush_alias_set, i).apply();
                if (0 == i) {//成功
                    L.d(TAG, "设置别名成功");
                    Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_succeed), Toast.LENGTH_SHORT).show();
                    MyApplication.JPush_Alias_Succeed = true;
                    mHandler.removeMessages(JPUSH_AGAIN);
                    showCode();
                } else if (6002 == i) {//超时
                    L.d(TAG, "设置别名超时：" + jpush_again_num);
                    jpush_again_num++;
                    if (jpush_again_num <= 10)
                        mHandler.sendEmptyMessageDelayed(JPUSH_AGAIN, 1000 * 10);//10s
                    Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_net_timeout), Toast.LENGTH_SHORT).show();
                } else {
                    L.d(TAG, "设置别名失败");
                    Toast.makeText(QRCodeActivity.this, getString(R.string.set_alias_defeat), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 生成唯一标识二维码
     */
    private void showCode() {
        try {
            L.d(TAG, "生成唯一标识二维码：" + mDecIMEI);
            mBitmap = CreateCode.encodeAsBitmap(mDecIMEI, GeneralUtil.dip2px(QRCodeActivity.this, 500));
        } catch (WriterException e) {
            L.e(TAG, "生成唯一标识二维码失败：" + e.getMessage());
            e.printStackTrace();
        }
        mQr_code.setImageBitmap(mBitmap);
    }

    class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {
            L.d(TAG, "注册网络改变广播接收者");
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            L.d(TAG, "网络变化:" + intent.getAction());
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                network_is_connect = true;
                L.d(TAG, "网络已连接:" + MyApplication.JPush_Alias_Succeed);
                if (!MyApplication.JPush_Alias_Succeed) {
                    jpushSetAlias();
                }
            } else {
                network_is_connect = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(JPUSH_AGAIN);
        if (mNetworkReceiver != null)
            unregisterReceiver(mNetworkReceiver);
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
