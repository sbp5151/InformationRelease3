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
    /**
     CENTER,  保持原图大小，显示在imageview中心，超过部分裁剪 保持纵横比
     CENTER_CROP, 以填满整个imageview为目的，等比例放大，显示在imageview中心，超过部分裁剪，
     CENTER_INSIDE,以原图完全显示为目的，若大缩小宽或高，若小则不作处理
     FIT_CENTER,把原图缩大或缩小居中显示 保持纵横比
     FIT_END,把原图按比例扩大(缩小)到ImageView的高度，显示在ImageView的下部分位置
     FIT_START,把原图按比例扩大(缩小)到ImageView的高度，显示在ImageView的上部分位置
     FIT_XY,把原图按照指定的大小在View中显示，拉伸显示图片，不保持原比例，填满ImageView.
     MATRIX;从图片左上角开始绘制，多余部分裁剪
     */
    public VpImg1Adapter(ArrayList<String> imgUlrs, Context context) {
        this.mImgUlrs = imgUlrs;
        mContext = context;
        for (String url : imgUlrs) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
