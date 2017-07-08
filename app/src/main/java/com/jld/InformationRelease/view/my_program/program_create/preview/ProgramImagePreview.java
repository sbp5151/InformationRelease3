package com.jld.InformationRelease.view.my_program.program_create.preview;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.my_program.program_create.preview.adapter.PreviewTextImgAdapter;

import java.util.ArrayList;

/**
 * 图片预览
 */
public class ProgramImagePreview extends AppCompatActivity {
    private static final int CHANGE_IMG = 0x01;
    private static final long IMG_CHANGE_TIME = 3000;
    private static final String TAG = "ProgramImagePreview";
    private ViewPager mVp;
    private ArrayList<String> imagePath;
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_IMG://图片自动切换
                    if (mVp != null) {
                        mVp.setCurrentItem(mVp.getCurrentItem() + 1);
                        mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_image_preview);
        imagePath = getIntent().getStringArrayListExtra("imagePath");
        LogUtil.d(TAG,"imagePath:"+imagePath);
        initView();
        if (imagePath.size() > 1)//多张图片才轮播
            mHandler.sendEmptyMessageDelayed(CHANGE_IMG, IMG_CHANGE_TIME);
    }

    private void initView() {
        mVp = (ViewPager) findViewById(R.id.vp_image_preview);
        AnimationUtil.setAnimation(2, mVp, this);
        PreviewTextImgAdapter adapter = new PreviewTextImgAdapter(imagePath, this);
        mVp.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagePath!=null&&imagePath.size() > 1)//多张图片才轮播
            mHandler.removeMessages(CHANGE_IMG);
    }
}
