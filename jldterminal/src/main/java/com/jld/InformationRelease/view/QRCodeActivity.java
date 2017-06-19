package com.jld.InformationRelease.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

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
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MacUtil;

import java.io.File;

public class QRCodeActivity extends BaseActivity implements JPushReceiver.JPushListener, GetScreenModel.GetScreenListen {

    private static final String TAG = "QRCodeActivity";
    private ImageView mQr_code;
    public static final int SHOW_CODE = 0x11;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MyApplication.JPush_Alias_Succeed && mBitmap != null) {
                mQr_code.setImageBitmap(mBitmap);
                if (mLoading != null && mLoading.isShowing())
                    mLoading.dismiss();
                mHandler.removeMessages(SHOW_CODE);
            }
            mHandler.sendEmptyMessageDelayed(SHOW_CODE,500);
        }
    };
    private Bitmap mBitmap;
    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG,"onCreate");
        setContentView(R.layout.activity_qrcode);
        mQr_code = (ImageView) findViewById(R.id.iv_qrcode);
        String mac = MacUtil.getMac();
        Log.d(TAG, "mac:" + mac);
        try {
            mBitmap = CreateCode.encodeAsBitmap(mac, GeneralUtil.dip2px(this, 500));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        JPushReceiver.sendListener(this);

        SharedPreferences sp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        int anInt = sp.getInt(Constant.JPush_alias_set, -1);
        if(anInt!=0){//没有设置别名，等待设置
            mLoading = new ProgressDialog(this);
            mLoading.setMessage(getResources().getString(R.string.connect_service));
            mLoading.show();
            mHandler.sendEmptyMessageDelayed(SHOW_CODE,1000);
        }else{
            mQr_code.setImageBitmap(mBitmap);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(SHOW_CODE);
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
