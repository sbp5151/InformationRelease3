package com.jld.InformationRelease.view.my_program.program_create.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.LogUtil;

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
public class ProgramCompileImgItemAdapter extends RecyclerView.Adapter<ProgramCompileImgItemAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<String> mDatas;
    public static final String TAG = "ProgramCompileImgItemAdapter";

    public ProgramCompileImgItemAdapter(Context context, ArrayList<String> datas, String cover) {
        mContext = context;
        this.mDatas = datas;
        LogUtil.d(TAG, "cover:" + cover);
        mDatas.add(0, cover);//封面
    }

    @Override
    public ProgramCompileImgItemAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.program_compile_img_item, null);//解决条目显示不全
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final ProgramCompileImgItemAdapter.MyHolder holder, int position) {
        if (position == 0) {//封面
            LogUtil.d(TAG, "cover:" + mDatas.get(position));
            holder.mCover.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mDatas.get(position))
                    .crossFade()//谈出效果
                    .into(holder.mImg);
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListen.onItemCoverClickListen();
                }
            });
        } else {
            Glide.with(mContext)
                    .load(mDatas.get(position))
                    .crossFade()
                    .into(holder.mImg);
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListen.onItemClickListen(view, holder.getLayoutPosition() - 1);//封面
                }
            });
            holder.mImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mClickListen.onItemLongClickListen(view, holder.getLayoutPosition() - 1);//封面
                    return true;
                }
            });
            holder.mCover.setVisibility(View.GONE);
        }
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

    public ArrayList<String> getDatas() {
        LogUtil.d(TAG, "------mDatas:" + mDatas);
        ArrayList<String> imgs = new ArrayList<>();//去除封面
        for (int i = 1; i < mDatas.size(); i++) {
            imgs.add(mDatas.get(i));
        }
        LogUtil.d(TAG, "------imgs:" + imgs);
        return imgs;
    }

    public String getdata(int position) {
        return mDatas.get(position);
    }

    public void addData(String imgPath) {
        mDatas.add(imgPath);
        notifyDataSetChanged();
    }

    public void addCover(String coverPath) {
        mDatas.remove(0);
        mDatas.add(0, coverPath);
        LogUtil.d(TAG, "mDatas:" + mDatas.get(0));
        notifyDataSetChanged();
    }

    public String getCover() {
        LogUtil.d(TAG, "mDatas:" + mDatas);
        return mDatas.get(0);
    }

    public void refreshData(ArrayList<String> imgPaths) {
        imgPaths.add(0, mDatas.get(0));//保留封面
        mDatas.clear();
        mDatas.addAll(imgPaths);//保证adapter和activity指的是同一对象
        notifyDataSetChanged();
    }

    public interface MyItemClickListen {

        void onItemClickListen(View view, int position);

        void onItemLongClickListen(View view, int position);

        void onItemCoverClickListen();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private ImageView mImg;
        private ImageView mCover;

        public MyHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_program_compile_img_item);
            mCover = (ImageView) itemView.findViewById(R.id.iv_program_compile_img_item_cover);
        }
    }
}
