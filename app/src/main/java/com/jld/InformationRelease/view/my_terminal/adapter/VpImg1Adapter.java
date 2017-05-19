package com.jld.InformationRelease.view.my_terminal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 16:34
 */
public class VpImg1Adapter extends PagerAdapter {


    ArrayList<ImageView> mViews = new ArrayList<>();
    ArrayList<String> mImgUlrs;
    Context mContext;

    public VpImg1Adapter(ArrayList<String> imgUlrs, Context context) {
        this.mImgUlrs = imgUlrs;
        mContext = context;
        for (String url : imgUlrs) {
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext).load(url).into(imageView);
            mViews.add(imageView);
        }
    }

    public VpImg1Adapter(Context context) {
        mContext = context;
        for (String img : mImgUlrs) {
            ImageView imageView1 = new ImageView(mContext);
            imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViews.add(imageView1);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
