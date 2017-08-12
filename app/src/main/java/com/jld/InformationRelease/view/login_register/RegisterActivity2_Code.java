package com.jld.InformationRelease.view.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.VerifyCodePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * 注册2
 */
public class RegisterActivity2_Code extends BaseActivity implements IViewListen<VerifyCodeResponseBean> {

    private ProgressDialog mProgressDialog;
    public static final int COUNT_DOWN = 0x01;
    private int countDown = 60;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COUNT_DOWN:
                    if (mBtn_again != null) {
                        if (countDown == 0) {//60s倒计时结束
                            mBtn_again.setText(getResources().getString(R.string.register_code_again));
                            mBtn_again.setClickable(true);
                            mBtn_again.setEnabled(true);
                        } else {//倒计时
                            mBtn_again.setText(getResources().getString(R.string.register_code_again) + "(" + countDown + ")");
                            countDown--;
                            mHandler.sendEmptyMessageDelayed(COUNT_DOWN, 1000);
                        }
                    }
                    break;
            }
        }
    };
    private Button mBtn_again;
    private String mVerityCode;
    private EditText mEt_code;
    private VerifyCodePresenter mPresenter;
    private String mPhone;
    private int mRequestId = 0x21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_code);
        //取值
        Intent intent = getIntent();
        mVerityCode = intent.getStringExtra("code");
        mPhone = intent.getStringExtra("phone");

        //dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("loading...");

        initView();
        //网络请求
        mPresenter = new VerifyCodePresenter(this, this);
        mHandler.sendEmptyMessage(COUNT_DOWN);
    }

    public void initView() {
        //title
        View title = findViewById(R.id.register_number_title_code);
        LinearLayout title_back = (LinearLayout) title.findViewById(R.id.title_back);
        TextView title_center = (TextView) title.findViewById(R.id.title_center);
        title_center.setText(getString(R.string.verify_number));
        TextView title_right = (TextView) title.findViewById(R.id.title_right);
        title_right.setVisibility(View.GONE);
        //重新发送
        mBtn_again = (Button) findViewById(R.id.btn_code_again);
        //验证码
        mEt_code = (EditText) findViewById(R.id.et_input_code);
        //下一步
        final Button btn_next = (Button) findViewById(R.id.bt_register_code_next);

        title_back.setOnClickListener(mOnClickListener);
        mBtn_again.setOnClickListener(mOnClickListener);
        mBtn_again.setClickable(false);
        mBtn_again.setEnabled(false);
        btn_next.setOnClickListener(mOnClickListener);
        btn_next.setEnabled(false);// 非激活状态
        mEt_code.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(charSequence.toString()))
                    btn_next.setEnabled(true);
                else
                    btn_next.setEnabled(false);
            }
        });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int id = view.getId();
            switch (id) {
                case R.id.title_back://返回
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                    break;
                case R.id.btn_code_again://重新发送
                    VerifyCodeRequestBean bean = new VerifyCodeRequestBean();
                    bean.setMobile(mPhone);
                    bean.setDa("86");
                    bean.setSign(MD5Util.getMD5(Constant.S_KEY + mPhone));
                    mPresenter.getVerifyCode1(bean, mRequestId);

                    mBtn_again.setEnabled(false);
                    mBtn_again.setClickable(false);
                    countDown = 60;
                    mHandler.sendEmptyMessage(COUNT_DOWN);
                    break;
                case R.id.bt_register_code_next://下一步
                    if (mVerityCode.equals(mEt_code.getText().toString())) {
                        toActivity(RegisterActivity3.class,"phone",mPhone);
                    } else {
                        ToastUtil.showToast(RegisterActivity2_Code.this, "验证码错误", 3000);
                    }
                    break;
            }
        }
    };

    @Override
    public void showProgress(int requestTag) {
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    @Override
    public void hideProgress(int requestTag) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void loadDataSuccess(VerifyCodeResponseBean data, int requestTag) {
        String code = data.getCode();
        if (!TextUtils.isEmpty(code)) {
            mVerityCode = code;
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        String s = e.getMessage().toString();
        if ("1004".equals(s))
            ToastUtil.showToast(this, getString(R.string.phone_password_error), 3000);
        else
            ToastUtil.showToast(this, s, 3000);
        hideProgress(requestTag);
    }
}
