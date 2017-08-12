package com.jld.InformationRelease.view.fragment;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.NamePriceBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.adapter.RvNameAdapter;
import com.jld.InformationRelease.view.adapter.VpImg1Adapter;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramTextFragment extends Fragment {

    public static final String TAG = "ProgramTextFragment";
    //图片切换
    private static final int CHANGE_IMG = 0x01;
    //切换时间
    private static final int IMG_CHANGE_TIME = 3000;
    private ViewPager mVp_img;
    private ArrayList<NamePriceBean> mNamePriceBeen1 = new ArrayList<>();
    private ArrayList<NamePriceBean> mNamePriceBeen2 = new ArrayList<>();
    private ArrayList<String> imgUlrs = new ArrayList<>();
    private int mCurrentItem = 0;
    private boolean isPagerStop = false;
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        if (!isPagerStop) {
                            mCurrentItem++;
                            mVp_img.setCurrentItem(mCurrentItem);
                        }
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };
    private Random mRandom;

    private ProgramTextFragment() {
    }

    protected FragmentActivity mContext;
    protected ProgramResponseBean mData;

    public static ProgramTextFragment getInstance(ProgramResponseBean data) {
        //数据传递
        ProgramTextFragment fragment1 = new ProgramTextFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment1.setArguments(bundle);
        return fragment1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            mData = bundle.getParcelable("data");
        } else {
            try {
                throw new Exception("ProgramFragment需要传入参数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mRandom = new Random();
        mContext = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brogram_fragment_1, container, false);
        LogUtil.d(TAG, "onCreateView:" + mData);
        if (mData == null)
            return view;
        initData();
        initView(view);
        if (imgUlrs.size() > 1) {
            mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
            int i = mRandom.nextInt(16);
            AnimationUtil.setAnimation(i + 1, mVp_img, mContext);
        }
        return view;
    }

    private void initData() {
        //图片url
        imgUlrs = mData.getItem().getImages();
        LogUtil.d(TAG, "imgUlrs:" + imgUlrs);
        ArrayList<NamePriceBean> namePriceBeen = mData.getItem().getTexts();
        LogUtil.d(TAG, "namePriceBeen:" + namePriceBeen);
        for (int i = 0; i < namePriceBeen.size(); i++) {
            if (i < 8) {//左边装10
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
        if (imgUlrs.size() > 0) {
            AnimationUtil.setAnimation(1, mVp_img, mContext);
            VpImg1Adapter imgAdapter = new VpImg1Adapter(imgUlrs, mContext);
            mVp_img.setAdapter(imgAdapter);
        }
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

        //背景封面
        ImageView back = (ImageView) view.findViewById(R.id.iv_preview_back);
        Glide.with(mContext).load(mData.getItem().getCover()).into(back);
    }


    @Override
    public void onStart() {
        super.onStart();
        isPagerStop = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isPagerStop = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (imgUlrs.size() > 1)
            mHandler.removeMessages(CHANGE_IMG);
    }

}
