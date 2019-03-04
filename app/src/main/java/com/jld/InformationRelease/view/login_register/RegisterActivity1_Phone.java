package com.jld.InformationRelease.view.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.VerifyCodePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * 注册1
 */
public class RegisterActivity1_Phone extends BaseActivity implements IViewListen<VerifyCodeResponseBean> {

    public static final String TAG = "RegisterActivity1_Phone";
    private static final int GET_CODE_TAG = 666;
    private ProgressDialog mProgressDialog;
    private ImageView mImg_back;
    private EditText mEt_input_nike;
    private EditText mEt_input_name;
    private Button mBtn_next;
    private VerifyCodePresenter mPresenter;
    private EditText mEt_number;
    private int mRequestId = 0x11;
    private String mPhoneNum;
    private String countryCode = "86";
    private Button mBtn_num_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("loading...");
        initView();

        mPresenter = new VerifyCodePresenter(this, this);
    }

    public void initView() {
        //title
        View title = findViewById(R.id.register_number_title_phone);
        LinearLayout title_back = (LinearLayout) title.findViewById(R.id.title_back);
        TextView title_center = (TextView) title.findViewById(R.id.title_center);
        title_center.setText(getString(R.string.verify_number));
        TextView title_right = (TextView) title.findViewById(R.id.title_right);
        title_right.setVisibility(View.GONE);
        // 用户协议
        TextView tv_protocol = (TextView) findViewById(R.id.tv_register_protocol);
        //区号
        mBtn_num_code = (Button) findViewById(R.id.btn_number_code);
        //电话号码
        mEt_number = (EditText) findViewById(R.id.et_input_number);
        //下一步
        final Button btn_next = (Button) findViewById(R.id.bt_register_number_next);

        title_back.setOnClickListener(mOnClickListener);
        tv_protocol.setOnClickListener(mOnClickListener);
        mBtn_num_code.setOnClickListener(mOnClickListener);
        btn_next.setOnClickListener(mOnClickListener);
        btn_next.setEnabled(false);// 非激活状态

        mEt_number.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(charSequence.toString()) && GeneralUtil.isPhoneNum(charSequence.toString()))
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
                case R.id.tv_register_protocol://用户协议
                    //// TODO: 2017/4/13 用户协议
                    ToastUtil.showToast(RegisterActivity1_Phone.this, "霸王条约不让看!", 3000);
                    break;
                case R.id.btn_number_code://区号
                    Intent intent = new Intent(RegisterActivity1_Phone.this, CountryPageActivity.class);
                    startActivityForResult(intent, GET_CODE_TAG);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    break;
                case R.id.bt_register_number_next://下一步
                    LogUtil.d(TAG,"下一步");
                    VerifyCodeRequestBean bean = new VerifyCodeRequestBean();
                    mPhoneNum = mEt_number.getText().toString();
                    bean.setMobile(mPhoneNum);
                    bean.setDa(countryCode);
                    bean.setSign(MD5Util.getMD5(Constant.S_KEY + mPhoneNum));
                    mPresenter.getVerifyCode1(bean, mRequestId);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG,"onActivityResult:"+requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data && requestCode == GET_CODE_TAG) {
            countryCode = data.getExtras().getString("CountryCode");
            String countryName = data.getExtras().getString("CountryName");
            if (!TextUtils.isEmpty(countryCode)
                    && !TextUtils.isEmpty(countryName)) {
                mBtn_num_code.setText(countryName + "(+" + countryCode + ")");
            }
        }
    }

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
        LogUtil.d(TAG,"code:"+code);
        if (!TextUtils.isEmpty(code)) {
            Intent intent = new Intent(this, RegisterActivity2_Code.class);
            intent.putExtra("code", code);
            intent.putExtra("phone", mPhoneNum);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        String s = e.getMessage().toString();
        if ("1004".equals(s))
            ToastUtil.showToast(this, getString(R.string.user_already_exists), 3000);
        else
            ToastUtil.showToast(this, s, 3000);
        hideProgress(requestTag);
    }
}
