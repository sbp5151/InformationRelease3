package com.jld.InformationRelease.view.fragment;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgramFragment;
import com.jld.InformationRelease.bean.NamePriceBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.view.adapter.RvNameAdapter;
import com.jld.InformationRelease.view.adapter.VpImg1Adapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment_1 extends BaseProgramFragment {

    //图片切换
    private static final int CHANGE_IMG = 0x01;
    //切换时间
    private static final int IMG_CHANGE_TIME = 10000;
    private ViewPager mVp_img;
    private ArrayList<NamePriceBean> mNamePriceBeen1 = new ArrayList<>();
    private ArrayList<NamePriceBean> mNamePriceBeen2 = new ArrayList<>();
    private ArrayList<String> imgUlrs = new ArrayList<>();
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        int currentItem = mVp_img.getCurrentItem();
                        if (currentItem == (imgUlrs.size() - 1))
                            currentItem = 0;
                        else
                            currentItem++;
                        mVp_img.setCurrentItem(currentItem);
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };
    private ProgramFragment_1() {
        super();
    }
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brogram_fragment_1, container, false);
        initData();
        initView(view);
        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
        return view;
    }

    private void initData() {
        //图片url
        imgUlrs = mData.getImages();
        ArrayList<NamePriceBean> namePriceBeen = mData.getCommoditys();
        for (int i = 0; i < namePriceBeen.size(); i++) {
            if (i < 10) {//左边装10
                mNamePriceBeen1.add(namePriceBeen.get(i));
            } else {//剩下给右边
                mNamePriceBeen2.add(namePriceBeen.get(i));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void initView(View view) {
        //中间
        mVp_img = (ViewPager) view.findViewById(R.id.vp_img);
        //切换动画
        AnimationUtil.setAnimation(1,mVp_img,mContext);
        VpImg1Adapter imgAdapter = new VpImg1Adapter(mContext);
        mVp_img.setAdapter(imgAdapter);

        //左边
        RecyclerView commodity1 = (RecyclerView) view.findViewById(R.id.rv_commodity_1);
        RvNameAdapter nameAdapter1 = new RvNameAdapter(mNamePriceBeen1, mContext);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext);
        commodity1.setLayoutManager(layoutManager1);
        commodity1.setAdapter(nameAdapter1);

        //右边
        RecyclerView commodity2 = (RecyclerView) view.findViewById(R.id.rv_commodity_2);
        RvNameAdapter nameAdapter2 = new RvNameAdapter(mNamePriceBeen2, mContext);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext);
        commodity2.setLayoutManager(layoutManager2);
        commodity2.setAdapter(nameAdapter2);
    }

}
