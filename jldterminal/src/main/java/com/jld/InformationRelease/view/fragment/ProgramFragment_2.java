package com.jld.InformationRelease.view.fragment;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgramFragment;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.view.adapter.VpImg2Adapter;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment_2 extends BaseProgramFragment {

    //图片切换
    private static final int CHANGE_IMG = 0x01;
    //切换时间
    private static final int IMG_CHANGE_TIME = 20000;
    private ViewPager mVp_img;
    private Context mContext;
    private ProgramResponseBean mData;
    Handler mHandler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        int currentItem = mVp_img.getCurrentItem();
                        if (currentItem == (mImgAdapter.imgs.size() - 1))
                            currentItem = 0;
                        else
                            currentItem++;
                        int i = mRandom.nextInt(16);
                        AnimationUtil.setAnimation(i + 1,mVp_img,mContext);
                        Log.d("random:",""+i);
                        mVp_img.setCurrentItem(currentItem,true);
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };
    private VpImg2Adapter mImgAdapter;
    private Spinner mSp_animation;
    private Random mRandom;

    private ProgramFragment_2() {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brogram_fragment_2, container, false);
        mVp_img = (ViewPager) view.findViewById(R.id.program2_vp);

        mSp_animation = (Spinner) view.findViewById(R.id.sp_animation);
        mSp_animation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AnimationUtil.setAnimation(i + 1,mVp_img,mContext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mImgAdapter = new VpImg2Adapter(mContext,mData.getImages());
        mVp_img.setAdapter(mImgAdapter);
        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
        return view;
    }
}
