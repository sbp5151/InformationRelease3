package com.jld.InformationRelease.view.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.request_bean.FeedBackRequest;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.UserPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;

import static com.jld.InformationRelease.R.id.checkb_feedback_complaints;
import static com.jld.InformationRelease.R.id.checkb_feedback_suggest;
import static com.jld.InformationRelease.R.id.et_feedback;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener, IViewListen {

    private EditText mFeedback;
    private CheckBox mCb_complaints;
    private CheckBox mCb_suggestion;
    private SharedPreferences mSp;

    public static final int FEED_BACK_REQUEST = 0x10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initView();
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
    }

    private void initView() {
        //内容
        mFeedback = (EditText) findViewById(et_feedback);
        //建议
        RelativeLayout rl_suggestion = (RelativeLayout) findViewById(R.id.feed_back_suggestion_rl);
        rl_suggestion.setOnClickListener(this);
        mCb_suggestion = (CheckBox) findViewById(checkb_feedback_suggest);
        mCb_suggestion.setChecked(true);
        mCb_suggestion.setClickable(false);
        //投诉
        RelativeLayout rl_complaints = (RelativeLayout) findViewById(R.id.feed_back_complaints_rl);
        rl_complaints.setOnClickListener(this);
        mCb_complaints = (CheckBox) findViewById(checkb_feedback_complaints);
        mCb_complaints.setClickable(false);

        //标题
        View title = findViewById(R.id.feed_back_title);
        title.findViewById(R.id.title_back).setOnClickListener(this);
        TextView tv_right = (TextView) title.findViewById(R.id.title_right);
        tv_right.setText(getString(R.string.sure));
        tv_right.setOnClickListener(this);
        TextView tv_center = (TextView) title.findViewById(R.id.title_center);
        tv_center.setText(getString(R.string.feed_back));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.title_back:
                finish();
                break;
            case R.id.feed_back_complaints_rl://投诉
                if (!mCb_complaints.isChecked()) {
                    mCb_complaints.setChecked(true);
                    mCb_suggestion.setChecked(false);
                }
                break;
            case R.id.feed_back_suggestion_rl://建议
                if (!mCb_suggestion.isChecked()) {
                    mCb_complaints.setChecked(false);
                    mCb_suggestion.setChecked(true);
                }
                break;
            case R.id.title_right://确定
                String content = mFeedback.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(FeedBackActivity.this, getString(R.string.feedback_not_null), 3000);
                    return;
                }
                UserPresenter userPresenter = new UserPresenter(FeedBackActivity.this, FeedBackActivity.this);
                String phone = mSp.getString(UserConstant.USER_PHONE, "");
                String type;
                if (mCb_complaints.isChecked())
                    type = "1";
                else
                    type = "0";
                String sign = MD5Util.getMD5(Constant.S_KEY + phone);
                FeedBackRequest body = new FeedBackRequest(phone, type, content, sign);
                userPresenter.FeedBack(body, FEED_BACK_REQUEST);
                break;
        }
    }

    @Override
    public void showProgress(int requestTag) {

    }

    @Override
    public void hideProgress(int requestTag) {

    }

    @Override
    public void loadDataSuccess(Object data, int requestTag) {

        if (FEED_BACK_REQUEST == requestTag) {
            ToastUtil.showToast(this, getString(R.string.feed_back_success), 3000);
            finish();
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        if (FEED_BACK_REQUEST == requestTag) {
            ToastUtil.showToast(this, getString(R.string.feed_back_defeated), 3000);
        }
    }
}
