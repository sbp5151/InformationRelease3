package com.jld.InformationRelease.view.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.UserRequest;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.UserPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;

public class LoginActivity extends BaseActivity implements IViewListen<UserResponse> {

    private static final String TAG = "LoginActivity";
    private static final int GET_CODE_TAG = 666;
    private ProgressDialog mProgressDialog;
    private UserPresenter mUserPresenter;
    private static final int LOG_REQUEST_TAG = 0x01;
    private boolean mIsInputPhoneNull = true;
    private boolean mIsInputPasswordNull = true;
    private EditText mEt_login_phone;
    private EditText mEt_login_password;
    private String mPhone;
    private String mMdt_password;
    private SharedPreferences mSp;
    private String mCountryCode = "86";
    private TextView mTv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.login_loading));
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        initView();
        mUserPresenter = new UserPresenter(this, this);
    }

    private void initView() {
        mTv_code = (TextView) findViewById(R.id.tv_login_country_code);
        mTv_code.setOnClickListener(mOnClickListener);
        //登录
        final Button btn_login = (Button) findViewById(R.id.bt_login_login);
        btn_login.setEnabled(false);

        //输入账号
        mEt_login_phone = (EditText) findViewById(R.id.et_login_number);
        String user_id = mSp.getString(UserConstant.USER_ID, "");
        if (!TextUtils.isEmpty(user_id)) {
            Log.d(TAG, "user_id:" + user_id);
            mEt_login_phone.setText(user_id);
            mIsInputPhoneNull = false;
        }
        //输入密码
        mEt_login_password = (EditText) findViewById(R.id.et_login_password);
        String user_password = mSp.getString(UserConstant.USER_PASSWORD, "");
        if (!TextUtils.isEmpty(user_password)) {
            Log.d(TAG, "user_password:" + user_password);
            mEt_login_password.setText(user_password);
            mIsInputPasswordNull = false;
            if (!mIsInputPhoneNull && !btn_login.isEnabled())
                btn_login.setEnabled(true);
        }
        //忘记密码
        TextView tv_login_forget_password = (TextView) findViewById(R.id.tv_login_forget_password);
        //注册
        TextView tv_new_user_register = (TextView) findViewById(R.id.tv_new_user_register);

        //点击监听
        btn_login.setOnClickListener(mOnClickListener);
        tv_login_forget_password.setOnClickListener(mOnClickListener);
        tv_new_user_register.setOnClickListener(mOnClickListener);

        mEt_login_phone.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {//输入不为空
                    mIsInputPhoneNull = false;
                    if (!mIsInputPasswordNull && !btn_login.isEnabled())//密码不为空并且登录未激活
                        btn_login.setEnabled(true);//激活登录按钮
                } else {
                    mIsInputPhoneNull = true;
                    if (btn_login.isEnabled() && btn_login.isEnabled())
                        btn_login.setEnabled(false);
                }
            }
        });
        mEt_login_password.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    mIsInputPasswordNull = false;
                    if (!mIsInputPhoneNull && !btn_login.isEnabled())
                        btn_login.setEnabled(true);
                } else {
                    mIsInputPasswordNull = true;
                    if (btn_login.isEnabled() && btn_login.isEnabled())
                        btn_login.setEnabled(false);
                }
            }
        });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.bt_login_login://登录
                    mPhone = mEt_login_phone.getText().toString();
                    mMdt_password = mEt_login_password.getText().toString();
                    String lockPassword = MD5Util.getMD5(Constant.S_KEY + mMdt_password);
                    UserRequest bean = new UserRequest();
                    bean.setMobile(mPhone);
                    bean.setPasswd(lockPassword);
                    bean.setCode(mCountryCode);
                    bean.setSign(MD5Util.getMD5(Constant.S_KEY + mPhone + lockPassword));
                    mUserPresenter.loginRequest(bean, LOG_REQUEST_TAG);
                    break;
                case R.id.tv_login_forget_password://忘记密码
//                    ToastUtil.showToast(LoginActivity.this, "怎么没忘记吃饭!", 3000);
                    toActivity(FindBackPassWordActivity.class);
                    break;
                case R.id.tv_new_user_register://注册
                    toActivity(RegisterActivity_Phone.class);
                    break;
                case R.id.tv_login_country_code://选择国家编码
                    Intent intent = new Intent(LoginActivity.this, CountryPageActivity.class);
                    startActivityForResult(intent, GET_CODE_TAG);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data && requestCode == GET_CODE_TAG) {
            mCountryCode = data.getExtras().getString("CountryCode");
            String countryName = data.getExtras().getString("CountryName");
            if (!TextUtils.isEmpty(mCountryCode)
                    && !TextUtils.isEmpty(countryName)) {
                mTv_code.setText(countryName + "(+" + mCountryCode + ")");
            }
        }
    }

    @Override
    public void showProgress(int requestTag) {
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
        LogUtil.d("showProgress");
    }

    @Override
    public void hideProgress(int requestTag) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        LogUtil.d("hideProgress");
    }

    @Override
    public void loadDataSuccess(UserResponse data, int requestTag) {
        LogUtil.d("loadDataSuccess:" + data);
        ToastUtil.showToast(this, "登陆成功", 3000);
        //保存账号密码
        SharedPreferences.Editor edit = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE).edit();
        edit.putString(UserConstant.USER_ID, data.getItem().getUserid());
        edit.putString(UserConstant.USER_PASSWORD, mMdt_password);
        edit.putBoolean(UserConstant.IS_LOGIN, true);
        edit.apply();
        toActivity(MainActivity.class);
        finish();
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        hideProgress(requestTag);
        String message = e.getMessage();
        if (!TextUtils.isEmpty(message))
            ToastUtil.showToast(this, message, 3000);
    }
}
