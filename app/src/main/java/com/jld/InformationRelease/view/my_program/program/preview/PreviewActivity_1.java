package com.jld.InformationRelease.view.my_program.program.preview;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_terminal.adapter.RvNameAdapter;
import com.jld.InformationRelease.view.my_terminal.adapter.VpImg1Adapter;

import java.util.ArrayList;

/**
 * 节目预览1
 */
public class PreviewActivity_1 extends AppCompatActivity {
    private static final int CHANGE_IMG = 0x01;
    private static final long IMG_CHANGE_TIME = 3000;
    private ViewPager mVp_img;
    private ProgramBean mPreviewData;
    public static final String TAG = "PreviewActivity_1";
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        mVp_img.setCurrentItem(mVp_img.getCurrentItem() + 1);
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_1);
        mPreviewData = (ProgramBean) getIntent().getSerializableExtra("previewData");
        LogUtil.d(TAG, "mPreviewData:" + mPreviewData);
        if (mPreviewData == null) {
            ToastUtil.showToast(this, getResources().getString(R.string.data_exception), Toast.LENGTH_SHORT);
            return;
        }
        if (mPreviewData.getModelId().equals("001"))
            initView1();
        else if (mPreviewData.getModelId().equals("002"))
            initView2();
        if (mPreviewData.getImages().size() > 1)//多张图片才轮播
            mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
    }

    public void initView2() {
        //中间
        mVp_img = (ViewPager) findViewById(R.id.vp_img);
        //切换动画
        if (mPreviewData.getImages().size() > 0) {
            AnimationUtil.setAnimation(1, mVp_img, this);
            VpImg1Adapter imgAdapter = new VpImg1Adapter(mPreviewData.getImages(), this);
            mVp_img.setAdapter(imgAdapter);
        }
        //背景图片
        ImageView lv_back = (ImageView) findViewById(R.id.iv_preview_back);
        Glide.with(this)
                .load(mPreviewData.getCover())
                .into(lv_back).getRequest();

        //隐藏左边
        LinearLayout commodity1 = (LinearLayout) findViewById(R.id.ll_preview_left);
        commodity1.setVisibility(View.GONE);

        //隐藏右边
        LinearLayout commodity2 = (LinearLayout) findViewById(R.id.ll_preview_right);
        commodity2.setVisibility(View.GONE);

    }

    public void initView1() {
        //中间
        mVp_img = (ViewPager) findViewById(R.id.vp_img);
        //切换动画
        if (mPreviewData.getImages().size() > 0) {
            AnimationUtil.setAnimation(1, mVp_img, this);
            VpImg1Adapter imgAdapter = new VpImg1Adapter(mPreviewData.getImages(), this);
            mVp_img.setAdapter(imgAdapter);
        }
        ArrayList<ProgramBean.Commodity> commoditys = mPreviewData.getTexts();
        ArrayList<ProgramBean.Commodity> leftCommodity = new ArrayList<>();
        ArrayList<ProgramBean.Commodity> rightCommodity = new ArrayList<>();

        ImageView lv_back = (ImageView) findViewById(R.id.iv_preview_back);
        Glide.with(this)
                .load(mPreviewData.getCover())
                .into(lv_back);
        //左边给10个，其余给右边
        if (commoditys.size() > 8) {
            for (int i = 0; i < commoditys.size(); i++) {
                if (i < 8)
                    leftCommodity.add(commoditys.get(i));
                else
                    rightCommodity.add(commoditys.get(i));
            }
        } else
            leftCommodity = commoditys;

        //左边
        RecyclerView commodity1 = (RecyclerView) findViewById(R.id.rv_commodity_1);
        RvNameAdapter nameAdapter1 = new RvNameAdapter(leftCommodity, this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        commodity1.setLayoutManager(layoutManager1);
        commodity1.setAdapter(nameAdapter1);

        //右边
        RecyclerView commodity2 = (RecyclerView) findViewById(R.id.rv_commodity_2);
        RvNameAdapter nameAdapter2 = new RvNameAdapter(rightCommodity, this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        commodity2.setLayoutManager(layoutManager2);
        commodity2.setAdapter(nameAdapter2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreviewData.getImages().size() > 1)//多张图片才轮播
            mHandler.removeMessages(CHANGE_IMG);
    }
}