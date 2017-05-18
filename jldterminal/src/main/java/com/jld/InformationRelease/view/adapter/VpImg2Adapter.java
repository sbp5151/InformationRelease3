package com.jld.InformationRelease.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
public class VpImg2Adapter extends PagerAdapter {

    private static final String TAG = "VpImg2Adapter";
//    public String[] imgs = {
//            "http://img5.imgtn.bdimg.com/it/u=1468260657,247068170&fm=23&gp=0.jpg",
//            "http://img0.imgtn.bdimg.com/it/u=451958898,471147024&fm=23&gp=0.jpg",
//            "http://img3.imgtn.bdimg.com/it/u=132629542,1898058257&fm=23&gp=0.jpg",
//            "http://img5.imgtn.bdimg.com/it/u=111513702,2478467957&fm=23&gp=0.jpg",
//            "http://img2.imgtn.bdimg.com/it/u=1375571120,3184739842&fm=23&gp=0.jpg",
//            "http://img5.imgtn.bdimg.com/it/u=1252360603,2690701606&fm=23&gp=0.jpg",
//            "http://img4.imgtn.bdimg.com/it/u=954349616,2203639406&fm=23&gp=0.jpg",
//    };
//    public int[] mImgs = {R.mipmap.img1, R.mipmap.img2, R.mipmap.img3,
//            R.mipmap.img4, R.mipmap.img5};

    public ArrayList<ImageView> mViews = new ArrayList<>();
    public Context mContext;
    public ArrayList<String> imgs;

    public VpImg2Adapter(Context context, ArrayList<String> imgs) {
        mContext = context;
        this.imgs = imgs;
        for (String srt : imgs) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(layoutParams);
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
            mViews.add(imageView);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem" + position);
        ImageView imageView = mViews.get(position);
        Glide.with(mContext)
                .load(imgs.get(position))
                .into(imageView);
        container.addView(imageView);
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem" + position);
        ImageView imageView = mViews.get(position);
        container.removeView(imageView);
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
