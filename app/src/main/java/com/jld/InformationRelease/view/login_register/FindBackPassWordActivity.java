package com.jld.InformationRelease.view.login_register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.VerifyCodeRequestBean;
import com.jld.InformationRelease.bean.response_bean.VerifyCodeResponseBean;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.VerifyCodePresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * 忘记密码1
 */
public class FindBackPassWordActivity extends BaseActivity implements OnClickListener, IViewListen<VerifyCodeResponseBean> {
    private static final String TAG = "FindBackPassWordActivity";
    public static final String NUMBER = "number";
    public static final String CODE = "code";
    private static final int GET_VERIFY = 0x21;
    private static final int GET_CODE_TAG = 666;


    private EditText et_find_password_number, et_security_code;
    private TextView tv_find_password_country;
    private Button bt_findpassword_get_code, bt_findpassword_sure;
    private SharedPreferences sp;
    private TimeCount time;
    private ImageView backImageView;
    private View titleView;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findback_password);
        titleView = findViewById(R.id.find_back_title_rl);
        sp = getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        initView();
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.imagev_find_password_back);
        backImageView.setOnClickListener(this);

        tv_find_password_country = (TextView) findViewById(R.id.tv_find_password_country);
        tv_find_password_country.setOnClickListener(this);

        et_find_password_number = (EditText) findViewById(R.id.et_find_password_number);

        et_security_code = (EditText) findViewById(R.id.et_security_code);
        et_security_code.addTextChangedListener(new MyTextWatcher() {
                                                    @Override
                                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                        String s = charSequence.toString();
                                                        if (!TextUtils.isEmpty(s)) {
                                                            bt_findpassword_sure.setEnabled(true);
                                                        } else
                                                            bt_findpassword_sure.setEnabled(false);
                                                    }
                                                }
        );
        bt_findpassword_get_code = (Button) findViewById(R.id.bt_findpassword_get_code);
        bt_findpassword_get_code.setOnClickListener(this);
        bt_findpassword_sure = (Button) findViewById(R.id.bt_findpassword_sure);
        bt_findpassword_sure.setOnClickListener(this);
        //没获取验证码之前,"确定按钮不能被点击"
        bt_findpassword_sure.setEnabled(false);
//		if(!TextUtils.isEmpty(sp.getString(UserInfo.LOGIN_ACCOUT, ""))){
//			et_find_password_number.setText(sp.getString(UserInfo.LOGIN_ACCOUT, ""));
//		}
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imagev_find_password_back://返回
                finish();
                break;
            case R.id.bt_findpassword_get_code://获取验证码
                String number = et_find_password_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtil.showToast(this, R.string.t_find_back_pwd_phone, 2000);
                    break;
                }
                VerifyCodePresenter presenter = new VerifyCodePresenter(this, this);
                VerifyCodeRequestBean body = new VerifyCodeRequestBean();
                body.setMobile(number);
                body.setDa("86");
                CharSequence text = tv_find_password_country.getText();
                LogUtil.d(TAG, "password_country:" + text);
                body.setSign(MD5Util.getMD5(Constant.S_KEY + number));
                presenter.getVerifyCode2(body, GET_VERIFY);
                break;
            case R.id.bt_findpassword_sure://确定
                String number_2 = et_find_password_number.getText().toString().trim();
                String code = et_security_code.getText().toString().trim();
                if (TextUtils.isEmpty(number_2) || TextUtils.isEmpty(code)) {
                    ToastUtil.showToast(this, R.string.t_find_back_pwd_text_info, 3000);
                    break;
                }
                if ((scode + "").equals(code)) {
                    Intent intent = new Intent();
                    intent.setClass(FindBackPassWordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra(NUMBER, number_2);
                    intent.putExtra(CODE, code);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                } else {
                    ToastUtil.showToast(this, R.string.t_find_back_pwd_code_info, 3000);
                }
                break;
            case R.id.tv_find_password_country:
                Intent intent = new Intent(FindBackPassWordActivity.this, CountryPageActivity.class);
                startActivityForResult(intent, GET_CODE_TAG);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            default:
                break;
        }
    }

    private String countryCode = "86";

    private String countryName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data && requestCode == GET_CODE_TAG) {
            countryCode = data.getExtras().getString("CountryCode");
            countryName = data.getExtras().getString("CountryName");
            if (!TextUtils.isEmpty(countryCode)
                    && !TextUtils.isEmpty(countryName)) {
                tv_find_password_country.setText(countryName + "(+" + countryCode + ")");
            }
        }
    }

    /**
     * 从服务器获取到的验证码
     */
    private String scode;

    @Override
    public void showProgress(int requestTag) {
        time.start();
    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(VerifyCodeResponseBean data, int requestTag) {
        String code = data.getCode();
        if (!TextUtils.isEmpty(code)) {
            scode = code;
            LogUtil.d("scode:" + scode);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        String message = e.getMessage();
        ToastUtil.showToast(this, message, 3000);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            bt_findpassword_get_code.setText(R.string.t_find_back_pwd_re_code_info);
            bt_findpassword_get_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            bt_findpassword_get_code.setClickable(false);
            bt_findpassword_get_code.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
