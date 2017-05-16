package com.jld.InformationRelease.view;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

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
import com.jld.InformationRelease.util.MacUtil;

import java.io.File;

public class QRCodeActivity extends BaseActivity implements JPushReceiver.JPushListener, GetScreenModel.GetScreenListen {

    private static final String TAG = "QRCodeActivity";
    private ImageView mQr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        mQr_code = (ImageView) findViewById(R.id.iv_qrcode);
        String mac = MacUtil.getMac();
        try {
            Bitmap bitmap = CreateCode.encodeAsBitmap(mac, GeneralUtil.dip2px(this, 500));
            mQr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        JPushReceiver.sendListener(this);
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
                Log.d(TAG, "绑定成功: .");
                SharedPreferences.Editor edit = this.getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
                edit.putBoolean(Constant.DEVICE_ISBINDING, true).apply();
                //保存绑定状态
                toActivity(MainActivity.class);
                finish();
                break;
            case Constant.GET_SCREEN://获取截屏
                String savePath = this.getExternalCacheDir().getAbsolutePath()+ File.separator+GeneralUtil.getTimeStr()+"111.png";
                new GetScreenPresenter().getScreen(QRCodeActivity.this,savePath,this);
                break;
        }
    }


    /**
     *  截屏失败
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
