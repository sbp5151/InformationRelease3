package com.jld.InformationRelease.view.fragment;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.L;
import com.jld.InformationRelease.view.adapter.VpImg1Adapter;
import com.jld.InformationRelease.widget.MyViewPager;

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
    private MyViewPager mVp_img;
    public int pagerCurrentItem = 0;
    Handler mHandler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp_img != null) {
                        if (!isPagerStop) {
                            pagerCurrentItem++;
                            mVp_img.setCurrentItem(pagerCurrentItem);
                        }
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                        L.d(TAG, "pagerCurrentItem:" + pagerCurrentItem);
                    }
                    break;
            }
        }
    };
    private VpImg1Adapter mImgAdapter;
    private Spinner mSp_animation;
    private Random mRandom;
    protected FragmentActivity mContext;
    protected ProgramResponseBean mData;
    private boolean isPagerStop = false;

    /**
     * 1、官方推荐Fragment.setArguments(Bundle bundle)这种方式来传递参数，而不推荐通过构造方法直接来传递参数，why？
     * 2、当一个fragment重新创建的时候，系统会再次调用 Fragment中的默认构造函数
     * 3、什么时候会重新创建fragment，when设备配置参数发生变化（如：横竖屏切换），
     * 4、既然是调用默认构造函数，那么通过重载传递的参数将全部丢失
     * 5、而使用系统推荐的 Fragment.setArguments（Bundle）来传递参数。就可以有效的避免这一个问题，当你的Fragment销毁的时候，其中的Bundle会保存下来，当要重新创建的时候会检查Bundle是否为null，如果不为null，就会使用bundle作为参数来重新创建fragment.
     * 6、setArguments can only be called before the Fragment is attached to the Activity.
     * setArguments方法的调用必须要在Fragment与Activity关联之前，即setArgument方法的使用必须要在FragmentTransaction 的commit之前使用
     *
     * @param data
     * @return
     */
    public static ProgramImageFragment getInstance(ProgramResponseBean data) {
        //数据传递
        ProgramImageFragment fragment1 = new ProgramImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment1.setArguments(bundle);
        return fragment1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
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
        mRandom = new Random(16);
        mContext = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        L.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_brogram_fragment_2, container, false);
        mVp_img = (MyViewPager) view.findViewById(R.id.program2_vp);
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
        L.d(TAG, "mData:" + mData);
        mImgAdapter = new VpImg1Adapter(mData.getItem().getImages(), mContext);
        mVp_img.setAdapter(mImgAdapter);
        if (mData.getItem().getImages().size() > 1) {
            //动画
            int random_index = mRandom.nextInt(16);
            L.d(TAG, "mRandom:" + (random_index + 1));
            AnimationUtil.setAnimation(random_index + 1, mVp_img, mContext);
            mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        isPagerStop = true;
        L.d(TAG, "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        L.d(TAG, "onStart");
        isPagerStop = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
        if (mData.getItem().getImages().size() > 1)
            mHandler.removeMessages(CHANGE_IMG);
    }
}
