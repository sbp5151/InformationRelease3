package com.jld.InformationRelease.view.my_terminal.preview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.request_bean.ProgramRequestBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_terminal.adapter.RvNameAdapter;
import com.jld.InformationRelease.view.my_terminal.adapter.VpImg1Adapter;

import java.util.ArrayList;

/**
 * 节目预览1
 */
public class PreviewActivity_1 extends AppCompatActivity {
    private ViewPager mVp_img;
    private ProgramRequestBean mPreviewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_1);
        mPreviewData = (ProgramRequestBean) getIntent().getSerializableExtra("previewData");
        if (mPreviewData == null) {
            ToastUtil.showToast(this, getResources().getString(R.string.data_exception), Toast.LENGTH_SHORT);
            return;
        }
        initView();
    }

    public void initView() {
        //中间
        mVp_img = (ViewPager) findViewById(R.id.vp_img);
        //切换动画
        AnimationUtil.setAnimation(1, mVp_img, this);
        VpImg1Adapter imgAdapter = new VpImg1Adapter(mPreviewData.getImages(), this);
        mVp_img.setAdapter(imgAdapter);

        ArrayList<ProgramRequestBean.Commodity> commoditys = mPreviewData.getCommoditys();
        ArrayList<ProgramRequestBean.Commodity> leftCommodity = new ArrayList<>();
        ArrayList<ProgramRequestBean.Commodity> rightCommodity = new ArrayList<>();

        //左边给10个，其余给右边
        if (commoditys.size() > 10) {
            for (int i = 0; i < commoditys.size(); i++) {
                if (i < 10)
                    leftCommodity.add(commoditys.get(i));
                else
                    rightCommodity.add(commoditys.get(i));
            }
        }else
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
}
