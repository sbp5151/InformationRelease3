package com.jld.InformationRelease.view.login_register;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.response_bean.UserResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.bean.request_bean.RegisterRequestBean;
import com.jld.InformationRelease.presenter.UserPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;

/**
 * 注册3
 */
public class RegisterActivity extends BaseActivity implements IViewListen<UserResponse> {

    private EditText mInput_name;
    private EditText mInput_companyName;
    private EditText mInput_password;
    private EditText mInput_passwordAgain;
    private Button mRegister_complete;
    private UserPresenter mPresenter;
    public static final int requestId = 0x31;
    private String mPhone;
    private ProgressDialog mDialog;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mPhone = getIntent().getStringExtra("phone");
        initView();
        mPresenter = new UserPresenter(this, this);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("loading....");
    }

    public void initView() {
        mInput_name = (EditText) findViewById(R.id.et_input_name);
        mInput_companyName = (EditText) findViewById(R.id.et_input_companyName);
        mInput_password = (EditText) findViewById(R.id.et_input_password);
        mInput_passwordAgain = (EditText) findViewById(R.id.et_input_passwordAgain);
        mRegister_complete = (Button) findViewById(R.id.bt_register_complete);
        mRegister_complete.setEnabled(false);

        mInput_password.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(charSequence.toString())
                        && !TextUtils.isEmpty(mInput_passwordAgain.getText().toString())
                        && !mRegister_complete.isEnabled()) {
                    mRegister_complete.setEnabled(true);
                } else if (TextUtils.isEmpty(charSequence.toString())
                        && mRegister_complete.isEnabled())
                    mRegister_complete.setEnabled(false);
            }
        });
        mInput_passwordAgain.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())
                        && !TextUtils.isEmpty(mInput_password.getText().toString())
                        && !mRegister_complete.isEnabled()) {
                    mRegister_complete.setEnabled(true);
                } else if (TextUtils.isEmpty(charSequence.toString())
                        && mRegister_complete.isEnabled())
                    mRegister_complete.setEnabled(false);
            }
        });
        mRegister_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPassword = mInput_password.getText().toString();
                RegisterRequestBean bean = new RegisterRequestBean();
                bean.setMobile(mPhone);
                bean.setPasswd(MD5Util.getMD5(Constant.S_KEY + mPassword));
                bean.setCode("86");
                bean.setImg("");
                bean.setNick(mInput_name.getText().toString());
                bean.setRealname(mInput_companyName.getText().toString());
                bean.setSign(MD5Util.getMD5(Constant.S_KEY + mPhone + MD5Util.getMD5(Constant.S_KEY + mPassword)));
                mPresenter.registerRequest(bean, requestId);
            }
        });
    }

    @Override
    public void showProgress(int requestTag) {
        if (mDialog != null && !mDialog.isShowing())
            mDialog.show();
    }

    @Override
    public void hideProgress(int requestTag) {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void loadDataSuccess(UserResponse data, int requestTag) {
        LogUtil.d(data.toString());
        ToastUtil.showToast(this, "注册成功", 3000);
        //保存账号密码
        SharedPreferences.Editor edit = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE).edit();
        edit.putString(UserConstant.USER_ID,mPhone);
        edit.putString(UserConstant.USER_PASSWORD,mPassword);
        edit.apply();
        toActivity(LoginActivity.class);
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        ToastUtil.showToast(this, e.getMessage().toString(), 3000);
        hideProgress(requestTag);
    }
}
