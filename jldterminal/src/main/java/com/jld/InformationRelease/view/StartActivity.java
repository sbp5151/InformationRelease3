package com.jld.InformationRelease.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.request.CheckBindRequest;
import com.jld.InformationRelease.bean.request.GetDevIdRequest;
import com.jld.InformationRelease.bean.response.CheckBindResponse;
import com.jld.InformationRelease.bean.response.GetDevIdResponse;
import com.jld.InformationRelease.presenter.CheckBindPresenter;
import com.jld.InformationRelease.presenter.GetDevIdPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MacUtil;

public class StartActivity extends BaseActivity implements IViewToPresenter<BaseResponse> {

    private static final String TAG = "StartActivity";
    public static final int TO_LOGIN_ACTIVITY = 0x01;
    private boolean mIsBinding;
    private static final int CHECK_BIND_REQEST = 0x10;
    private static final int GET_DEV_ID_REQUEST = 0x11;
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
    private long mTime_start;
    private long mTime_stop;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG,"onCreate");
        setContentView(R.layout.activity_start);
        mSp = getSharedPreferences(Constant.share_key, MODE_PRIVATE);
        mIsBinding = mSp.getBoolean(Constant.DEVICE_ISBINDING, false);
        checkBind();
        mTime_start = System.currentTimeMillis();
        Intent intent = new Intent(this, com.jld.InformationRelease.service.UpdateTimeService.class);
        startService(intent);
    }

    private void checkBind() {
        LogUtil.d(TAG,"checkBind");
        //检查设备是否被绑定
        CheckBindPresenter checkBindPresenter = new CheckBindPresenter(this, this);
        String mDecIMEI = MacUtil.getIMEI(this);
        String sign = MD5Util.getMD5(Constant.S_KEY + mDecIMEI);
        CheckBindRequest body = new CheckBindRequest(mDecIMEI, sign);
        checkBindPresenter.deviceCheckBind(body, CHECK_BIND_REQEST);
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        mTime_stop = System.currentTimeMillis();
        if (requestTag == CHECK_BIND_REQEST) {
            CheckBindResponse checkBindResponse = (CheckBindResponse)data;
            LogUtil.d(TAG, "CheckBindResponse:" + checkBindResponse);
            String result = checkBindResponse.getResult();
            if (result.equals("0")) {//已绑定
                String dev_id = mSp.getString(Constant.DEVICE_ID, "");
                mSp.edit().putBoolean(Constant.DEVICE_ISBINDING, true).apply();
                mIsBinding = true;
                LogUtil.d(TAG, "dev_id:" + dev_id);
                if (TextUtils.isEmpty(dev_id)) {
                    GetDevIdPresenter presenter = new GetDevIdPresenter(this, this);
                    String mDecIMEI = MacUtil.getIMEI(this);
                    String sign = MD5Util.getMD5(Constant.S_KEY + mDecIMEI);
                    GetDevIdRequest body = new GetDevIdRequest(mDecIMEI, sign);
                    presenter.getDevId(body, GET_DEV_ID_REQUEST);
                } else {
                    mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000 - (mTime_stop - mTime_start));
                }

            } else {//未绑定
                mSp.edit().putBoolean(Constant.DEVICE_ISBINDING, false).apply();
                mIsBinding = false;
                mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000 - (mTime_stop - mTime_start));
            }
        } else if (requestTag == GET_DEV_ID_REQUEST) {
            GetDevIdResponse getDevIdResponse = (GetDevIdResponse)data;
            String dev_id = getDevIdResponse.getId();
            mSp.edit().putString(Constant.DEVICE_ID, dev_id).apply();
            mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000 - (mTime_stop - mTime_start));
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage().toString());
        mTime_stop = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(TO_LOGIN_ACTIVITY, 2000 - (mTime_stop - mTime_start));
    }
}
