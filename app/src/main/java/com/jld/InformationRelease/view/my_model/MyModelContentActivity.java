package com.jld.InformationRelease.view.my_model;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_terminal.ProgramCompileActivity2;

import java.util.ArrayList;

import static com.jld.InformationRelease.R.id.title_preview;
import static com.jld.InformationRelease.R.id.title_right;

public class MyModelContentActivity extends BaseActivity {

    private ProgramBean mData;
    private ModelContentRecyclerAdapter mAdapter;
    ArrayList<String> mImgs = new ArrayList();
    ArrayList<ProgramBean.Commodity> mDatas;

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
        tvTitle.setText(getResources().getString(R.string.myModel));
        //返回
        LinearLayout back = (LinearLayout) titlebar.findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //再编辑
        TextView compile = (TextView) titlebar.findViewById(title_preview);
        compile.setVisibility(View.VISIBLE);
        compile.setText(getResources().getString(R.string.completer));
        compile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toActivity(ProgramCompileActivity2.class, mData, "data");
                finish();
            }
        });
        //推送
        TextView push = (TextView) titlebar.findViewById(title_right);
        push.setText(getResources().getString(R.string.push));
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE).getString(UserConstant.USER_ID, "");
                if (TextUtils.isEmpty(userID)) {//账号不能为空
                    ToastUtil.showToast(MyModelContentActivity.this, getResources().getString(R.string.please_login), 3000);
                    return;
                }
                mImgs = mData.getImages();
                if (mImgs.size() == 0) {//图片不能为空
                    ToastUtil.showToast(MyModelContentActivity.this, getString(R.string.please_set_img), 3000);
                    return;
                }
                ArrayList<String> macs = mData.getDeviceMacs();
                if (macs.size() == 0) {//图片不能为空
                    ToastUtil.showToast(MyModelContentActivity.this, getString(R.string.please_set_img), 3000);
                    return;
                }
                mDatas = mData.getCommoditys();
                if (mDatas.size() == 0) {//名称和价格不能为空
                    ToastUtil.showToast(MyModelContentActivity.this, getString(R.string.please_set_commodity), 3000);
                    return;
                }
                ProgramBean body = new ProgramBean();
                body.setCreation_time(GeneralUtil.getTimeStr());
                body.setCommoditys(mDatas);//名称和价格
                body.setImages(mImgs);//图片广告
                body.setDeviceMacs(macs);//需要推送终端的Mac地址
                body.setModelId(mData.getModelId());//模板ID
                body.setUserID(userID);//账号
                body.setSign(MD5Util.getMD5(Constant.S_KEY + userID));//加密字符串
                Intent intent = new Intent(MyModelContentActivity.this, MainActivity.class);
                intent.putExtra("body", body);
                setResult(0x11, intent);//编辑结果返回
                finish();
            }
        });
        //recyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_model_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ModelContentRecyclerAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
    }
}
