package com.jld.InformationRelease.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jld.InformationRelease.util.animationlibrary.AccordionTransformer;
import com.jld.InformationRelease.util.animationlibrary.BackgroundToForegroundTransformer;
import com.jld.InformationRelease.util.animationlibrary.CubeInTransformer;
import com.jld.InformationRelease.util.animationlibrary.CubeOutTransformer;
import com.jld.InformationRelease.util.animationlibrary.DepthPageTransformer;
import com.jld.InformationRelease.util.animationlibrary.FlipHorizontalTransformer;
import com.jld.InformationRelease.util.animationlibrary.FlipVerticalTransformer;
import com.jld.InformationRelease.util.animationlibrary.ForegroundToBackgroundTransformer;
import com.jld.InformationRelease.util.animationlibrary.RotateDownTransformer;
import com.jld.InformationRelease.util.animationlibrary.ScaleInOutTransformer;
import com.jld.InformationRelease.util.animationlibrary.StackTransformer;
import com.jld.InformationRelease.util.animationlibrary.TabletTransformer;
import com.jld.InformationRelease.util.animationlibrary.ZoomInTransformer;
import com.jld.InformationRelease.util.animationlibrary.ZoomOutSlideTransformer;
import com.jld.InformationRelease.util.animationlibrary.ZoomOutTranformer;


/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/18 9:31
 */
public class AnimationUtil {

    private static ViewPagerScroller sScroller;
    /**
     * 按钮动画
     *
     * @param imageView
     * @param duration
     */
    public static void togetherRun(View imageView, int duration) {
        if (null == imageView) {
            return;
        }
        if (duration <= 0) {
            duration = 400;
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(imageView, "scaleX",
                1.0f, 0.8f, 1.1f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView, "scaleY",
                1.0f, 0.8f, 1.1f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(duration);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }
    public static void setAnimation(int a, ViewPager viewPager, Context context) {
        if (a == 17) {
            a = 1;
        }
        //慢速切换
        if (sScroller == null) {
            sScroller = new ViewPagerScroller(context);
            sScroller.setScrollDuration(2000);
        }
        sScroller.initViewPagerScroll(viewPager);//这个是设置切换过渡时间为2秒
        switch (a) {
            case 1://左右黏合滑动
                viewPager.setPageTransformer(true,
                        new AccordionTransformer());
                break;
            case 2:////3D向前飞出屏幕
                viewPager.setPageTransformer(true,
                        new BackgroundToForegroundTransformer());
                break;
            case 3://3D箱子旋转
                viewPager.setPageTransformer(true,
                        new CubeInTransformer());
                break;
            case 4://平移
                viewPager.setPageTransformer(true,
                        new CubeOutTransformer());
                break;
            case 5://卡片左右翻页
                viewPager.setPageTransformer(true,
                        new DepthPageTransformer());
                break;
            case 6://卡片上下翻页
                viewPager.setPageTransformer(true,
                        new FlipHorizontalTransformer());
                break;
            case 7://卡片上下翻页
                viewPager.setPageTransformer(true,
                        new FlipVerticalTransformer());
                break;
            case 8://左右带角度平移1
                viewPager.setPageTransformer(true,
                        new ForegroundToBackgroundTransformer());
                break;
            case 9://左右带角度平移2
                viewPager.setPageTransformer(true,
                        new RotateDownTransformer());
                break;
            case 11://遮盖翻页
                viewPager.setPageTransformer(true,
                        new ScaleInOutTransformer());
                break;
            case 12://内旋3D翻页
                viewPager.setPageTransformer(true,
                        new StackTransformer());
                break;
            case 13://不翻页中心缩小
                viewPager.setPageTransformer(true,
                        new TabletTransformer());
                break;
            case 14://左右半透明平移
                viewPager.setPageTransformer(true,
                        new ZoomInTransformer());
                break;
            case 15://改变透明度变换
                viewPager.setPageTransformer(true,
                        new ZoomOutSlideTransformer());
                break;
            case 16://左右黏贴平移
                viewPager.setPageTransformer(true,
                        new ZoomOutTranformer());
                break;
            default:
                break;
        }
    }
}
