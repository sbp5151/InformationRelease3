package com.jld.InformationRelease.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.jld.InformationRelease.R;
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
public class VpImg1Adapter extends PagerAdapter {

    public static final String TAG = "VpImg1Adapter";
    ArrayList<ImageView> mViews = new ArrayList<>();
    ArrayList<String> mImgUlrs;
    Context mContext;

    public VpImg1Adapter(ArrayList<String> imgUlrs, Context context) {
        this.mImgUlrs = imgUlrs;
        mContext = context;
        for (String url : imgUlrs) {
            ImageView imageView = new ImageView(mContext);
            Request request = Glide.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.img_load_error)
                    .into(imageView).getRequest();
            LogUtil.d(TAG, "request:" + request.toString());
            mViews.add(imageView);
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LogUtil.d(TAG,"instantiateItem:"+position);
        position %= mViews.size();
        LogUtil.d(TAG,"instantiateItem:"+position);
        if (position < 0) {
            position += mViews.size();
        }
        ViewGroup parent = (ViewGroup) mViews.get(position).getParent();
        ImageView view = mViews.get(position);
        if (parent != null) {
            parent.removeView(view);
        }
        container.addView(view);
        return mViews.get(position % mViews.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
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
