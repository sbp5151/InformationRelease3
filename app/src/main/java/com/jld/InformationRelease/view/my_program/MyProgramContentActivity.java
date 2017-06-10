package com.jld.InformationRelease.view.my_program;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_terminal.ProgramCompileActivity2;
import com.jld.InformationRelease.view.my_terminal.preview.PreviewActivity_1;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * 我的节目
 */
public class MyProgramContentActivity extends BaseActivity {

    private static final String TAG = "MyProgramContentActivity";
    private ProgramBean mData;
    private ProgramContentRecyclerAdapter mAdapter;
    ArrayList<String> mImgs = new ArrayList();
    ArrayList<ProgramBean.Commodity> mDatas;
    private PopupWindow mPopupWindow;
    private ImageButton mIb_tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_model_content);
        Intent intent = getIntent();
        mData = (ProgramBean) intent.getSerializableExtra("data");
        initView();
    }

    private void initView() {
        //title
        View titlebar = findViewById(R.id.model_content_titlebar);
        TextView tvTitle = (TextView) titlebar.findViewById(R.id.title_center);
        tvTitle.setText(getResources().getString(R.string.myProgram));

        mIb_tool = (ImageButton) titlebar.findViewById(R.id.titlebar_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(mOnClickListener);

        //取消
        titlebar.findViewById(R.id.title_right).setVisibility(View.GONE);
        //返回
        LinearLayout back = (LinearLayout) titlebar.findViewById(R.id.title_back);
        back.setOnClickListener(mOnClickListener);
        //recyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_model_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProgramContentRecyclerAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.titlebar_tool:
                    showPopupwindow();
                    break;
                case R.id.title_back://返回
                    finish();
                    break;
                case R.id.pp_program_push://推送
                    push();
                    break;
                case R.id.pp_preview://预览
                    preview();
                    break;
                case R.id.pp_completer://再编辑
                    toActivity(ProgramCompileActivity2.class, mData, "data");
                    finish();
                    break;
            }
        }
    };

    /**
     * 预览
     */
    public void preview() {
        mPopupWindow.dismiss();
        LogUtil.d(TAG,"preview:"+mData);
        if (mData.getModelId().equals("001"))
            toActivity(PreviewActivity_1.class, mData, "previewData");
    }

    /**
     * 发布
     */
    public void push() {
        String userID = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE).getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(MyProgramContentActivity.this, getResources().getString(R.string.please_login), 3000);
            return;
        }
        mImgs = mData.getImages();
        if (mImgs.size() == 0) {//图片不能为空
            ToastUtil.showToast(MyProgramContentActivity.this, getString(R.string.please_set_img), 3000);
            return;
        }
        ArrayList<String> macs = mData.getDeviceMacs();
        if (macs.size() == 0) {//图片不能为空
            ToastUtil.showToast(MyProgramContentActivity.this, getString(R.string.please_set_img), 3000);
            return;
        }
        mDatas = mData.getTexts();
        if (mDatas.size() == 0) {//名称和价格不能为空
            ToastUtil.showToast(MyProgramContentActivity.this, getString(R.string.please_set_commodity), 3000);
            return;
        }
        ProgramBean body = new ProgramBean();
        body.setCreation_time(GeneralUtil.getTimeStr());
        body.setTexts(mDatas);//名称和价格
        body.setImages(mImgs);//图片广告
        body.setDeviceMacs(macs);//需要推送终端的Mac地址
        body.setModelId(mData.getModelId());//模板ID
        body.setUserid(userID);//账号
        body.setSign(MD5Util.getMD5(Constant.S_KEY + userID));//加密字符串
        Intent intent = new Intent(MyProgramContentActivity.this, MainActivity.class);
        intent.putExtra("body", body);
        setResult(mProgramResultCode, intent);//编辑结果返回
    }

    public void showPopupwindow() {
        mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.program_again_popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_preview).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_completer).setOnClickListener(mOnClickListener);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.showAsDropDown(mIb_tool, 0, GeneralUtil.dip2px(this, -22));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mIb_tool.setClickable(true);
                mIb_tool.setEnabled(true);
            }
        });
    }
}
