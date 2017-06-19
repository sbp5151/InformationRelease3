package com.jld.InformationRelease.view.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.NamePriceBean;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 17:29
 */
public class RvNameAdapter extends RecyclerView.Adapter<RvNameAdapter.MyViewHolder> {

    private static final String TAG = "RvNameAdapter";
    private ArrayList<NamePriceBean> beans;
    private Context mContext;

    public RvNameAdapter(ArrayList<NamePriceBean> beans, Context context) {
        Log.d(TAG, "RvNameAdapter:" + beans);
        this.beans = beans;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder:");

        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder:" + position);
        holder.tvPrice.setText(beans.get(position).getPrice());
        holder.tvName.setText(beans.get(position).getName());

        if (holder.tvPrice.getText().toString().equals("15")) {
            togetherRun(holder.tvPrice, 2000);
            togetherRun(holder.tvName, 2000);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.tvPrice.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }
    }

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
                1.0f, 0.8f, 1.2f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView, "scaleY",
                1.0f, 0.8f, 1.2f, 1.0f);
        anim1.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        anim2.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(duration);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount:" + beans.size());
        return beans.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }
}
