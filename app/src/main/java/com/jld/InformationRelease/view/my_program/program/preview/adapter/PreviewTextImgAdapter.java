package com.jld.InformationRelease.view.my_program.program.preview.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 16:34
 */
public class PreviewTextImgAdapter extends PagerAdapter {

    public static final String TAG = "PreviewTextImgAdapter";
    ArrayList<ImageView> mViews = new ArrayList<>();
    ArrayList<String> mImgUlrs;
    Context mContext;

    public PreviewTextImgAdapter(ArrayList<String> imgUlrs, Context context) {
        this.mImgUlrs = imgUlrs;
        mContext = context;
        for (String url : imgUlrs) {
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext).load(url).into(imageView);
            mViews.add(imageView);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LogUtil.d(TAG,"instantiateItem:"+position);
        ViewGroup parent = (ViewGroup) mViews.get(position % mViews.size()).getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(mViews.get(position % mViews.size()));
        return mViews.get(position % mViews.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
