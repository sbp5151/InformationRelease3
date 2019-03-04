package com.jld.InformationRelease.view.my_program.program_create.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/12 19:38
 * <p>
 * 节目编辑图片适配器
 */
public class ProgramImageAdapter extends RecyclerView.Adapter<ProgramImageAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<String> mDatas;
    public static final String TAG = "ProgramImageAdapter";

    public ProgramImageAdapter(Context context, ArrayList<String> datas) {
        mContext = context;
        this.mDatas = datas;
    }

    @Override
    public ProgramImageAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.program_compile_img_item, null);//解决条目显示不全
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final ProgramImageAdapter.MyHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: url:" + mDatas.get(position));
        Glide.with(mContext)
                .load(mDatas.get(position))
                .crossFade()
                .into(holder.mImg);
//        Picasso.with(mContext)
//                .load(mDatas.get(position))
//                .into(holder.mImg);
        holder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListen.onItemClickListen(view, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDatas == null)
            return 0;
        return mDatas.size();
    }

    MyItemClickListen mClickListen;

    public void setItemClickListen(MyItemClickListen clickListen) {
        mClickListen = clickListen;
    }

    public String getdata(int position) {
        return mDatas.get(position);
    }

    public void addData(String imgPath) {
        mDatas.add(imgPath);
        notifyDataSetChanged();
    }

    public ArrayList<String> getImgDatas() {
        return mDatas;
    }

    public void refreshDatas(ArrayList<String> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public interface MyItemClickListen {
        void onItemClickListen(View view, int position);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private ImageView mImg;

        public MyHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_program_compile_img_item);
        }
    }
}
