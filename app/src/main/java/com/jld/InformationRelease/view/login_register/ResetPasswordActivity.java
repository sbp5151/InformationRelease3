package com.jld.InformationRelease.view.login_register;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.request_bean.RetrievePWRequestBean;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.UserPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * 忘记密码2
 */
public class ResetPasswordActivity extends BaseActivity implements
        OnClickListener, IViewListen<BaseResponse> {
    private static final int CHANGE_PW = 0x21;
    // private static final String TAG = "ResetPasswordActivity";

    private TextView titleTextView;
    private View mBackView;

    private EditText et_reset_password, et_reset_password_sure;
    private Button bt_resetpassword_sure;
    /**
     * 手机号
     */
    private String number;
    private String newpasswd;
    private String newpasswd2;
    private View titleView;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_layout);
        initView();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.loading));
    }

    private void initView() {
        if (null != this.getIntent()) {
            number = this.getIntent().getStringExtra(
                    FindBackPassWordActivity.NUMBER);
        }
        titleView = findViewById(R.id.layout_reste_password_title);
        titleTextView = (TextView) titleView.findViewById(R.id.title_center);
        titleTextView.setText(R.string.t_reset_pwd_info);

        mBackView = titleView.findViewById(R.id.title_back);
        mBackView.setOnClickListener(this);
        titleView.findViewById(R.id.title_right).setVisibility(View.GONE);

        et_reset_password = (EditText) findViewById(R.id.et_reset_password);
        et_reset_password_sure = (EditText) findViewById(R.id.et_reset_password_sure);
        bt_resetpassword_sure = (Button) findViewById(R.id.bt_resetpassword_sure);
        bt_resetpassword_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_view_title_back:
                finish();
                break;
            case R.id.bt_resetpassword_sure:
                // LogUtil.i(TAG, "number:"+number);
                if (null == number) {
                    break;
                }
                String original_newpasswd = et_reset_password.getText().toString()
                        .trim();
                String original_newpasswd2 = et_reset_password_sure.getText()
                        .toString().trim();

                newpasswd = MD5Util.getMD5(Constant.S_KEY + original_newpasswd);
                newpasswd2 = MD5Util.getMD5(Constant.S_KEY + original_newpasswd2);

                if (TextUtils.isEmpty(newpasswd) || TextUtils.isEmpty(newpasswd2)) {
                    ToastUtil.showToast(this, R.string.t_reset_pwd_new_pwd, 2000);
                    break;
                }
                // 找回密码的需提交的加密字符串
                String sign = MD5Util.getMD5(Constant.S_KEY + number + newpasswd);
                RetrievePWRequestBean body = new RetrievePWRequestBean();
                body.setMobile(number);
                body.setNewpasswd(newpasswd);
                body.setNewpasswd2(newpasswd2);
                body.setSign(sign);

                UserPresenter presenter = new UserPresenter(this, this);
                presenter.retrievePW(body, CHANGE_PW);
                break;
        }
    }

    @Override
    public void showProgress(int requestTag) {
        mDialog.show();
    }

    @Override
    public void hideProgress(int requestTag) {
        mDialog.dismiss();
    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        ToastUtil.showToast(this, getString(R.string.change_pw_sure), 3000);
        toActivity(LoginActivity.class);
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        if (e != null)
            ToastUtil.showToast(this, e.getMessage().toString(), 3000);
    }
}
