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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.adapter.VpImg1Adapter;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramImageFragment extends Fragment {

    private static String TAG = "ProgramImageFragment";
    //图片切换
    private static final int CHANGE_IMG = 0x01;
    //切换时间
    private static final int IMG_CHANGE_TIME = 6000;
    private ViewPager mVp_img;
    Handler mHandler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        int i = mRandom.nextInt(16);
                        AnimationUtil.setAnimation(i + 1, mVp_img, mContext);
                        Log.d("random:", "" + i);
                        Log.d("currentItem:", "" + (mVp_img.getCurrentItem() + 1));
                        mVp_img.setCurrentItem(mVp_img.getCurrentItem() + 1);
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };
    private VpImg1Adapter mImgAdapter;
    private Spinner mSp_animation;
    private Random mRandom;

    private ProgramImageFragment() {
        super();
    }


    protected FragmentActivity mContext;
    protected ProgramResponseBean mData;

    public static ProgramImageFragment getInstance(ProgramResponseBean data) {
        //数据传递
        ProgramImageFragment fragment1 = new ProgramImageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        fragment1.setArguments(bundle);
        return fragment1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            mData = (ProgramResponseBean) bundle.getSerializable("data");
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
        View view = inflater.inflate(R.layout.fragment_brogram_fragment_2, container, false);
        mVp_img = (ViewPager) view.findViewById(R.id.program2_vp);

        mSp_animation = (Spinner) view.findViewById(R.id.sp_animation);
        mSp_animation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AnimationUtil.setAnimation(i + 1, mVp_img, mContext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        LogUtil.d(TAG, "mData:" + mData);
        mImgAdapter = new VpImg1Adapter(mData.getItem().getImages(),mContext);
        mVp_img.setAdapter(mImgAdapter);
        if (mData.getItem().getImages().size() > 1)
            mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
        return view;
    }
}
