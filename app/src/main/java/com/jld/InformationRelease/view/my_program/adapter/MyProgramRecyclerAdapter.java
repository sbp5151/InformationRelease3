package com.jld.InformationRelease.view.my_program.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseProgram;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/25 18:00
 */
public class MyProgramRecyclerAdapter extends RecyclerView.Adapter<MyProgramRecyclerAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<BaseProgram> mData;
    public static final String TAG = "MyProgramRecyclerAdapter";
    private boolean isCompileState = false;//是否处于编辑状态

    public MyProgramRecyclerAdapter(Context context, ArrayList<BaseProgram> data) {
        mContext = context;
        this.mData = data;
        LogUtil.d(TAG, "mData:" + data);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_program_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        BaseProgram programBean = mData.get(position);
        LogUtil.d(TAG, "programBean:" + programBean);
        holder.mTime.setText(programBean.getCreation_time());
        holder.mTab.setText(programBean.getTab());

        //上传情况
        if (TextUtils.isEmpty(programBean.getProgramId())) {//未上传 隐藏上传信息
            holder.mProgress.setVisibility(View.GONE);
            holder.load_state.setVisibility(View.GONE);
        } else if (programBean.getIsLoadSucceed().equals("1")) {//已上传 所有设备加载完成 显示加载完成
            holder.mProgress.setVisibility(View.GONE);
            holder.load_state.setVisibility(View.VISIBLE);
            holder.load_state.setText(mContext.getString(R.string.load_succeed));
        } else if (position < 3 && TimeUtil.toCurrentTimeGap(programBean.getCreation_time()) <= 1000 * 60 * 5) {//已上传 前三 不超过五分钟 显示上传进度
            holder.mProgress.setVisibility(View.VISIBLE);
            holder.load_state.setVisibility(View.GONE);
            int progress = 100 * programBean.getLoadDeviceMacs().size() / programBean.getDeviceMacs().size();
            holder.mProgress.setProgress(progress);
        } else {//未上传 后三 显示上传情况
            holder.mProgress.setVisibility(View.GONE);
            holder.load_state.setVisibility(View.VISIBLE);
            holder.load_state.setText(programBean.getLoadDeviceMacs().size() + "/" + programBean.getDeviceMacs().size());
        }
        holder.mProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemProgressClickListen(view, holder.getLayoutPosition());
            }
        });
        holder.load_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemProgressClickListen(view, holder.getLayoutPosition());
            }
        });
        //头像
        if (programBean.getType().equals("2"))
            holder.mIcon.setImageResource(R.mipmap.day_task);
        else
            Glide.with(mContext).load(programBean.getModel_img()).into(holder.mIcon);
        if (programBean.getUpload_state() != null && programBean.getUpload_state().equals("1")) {//上传成功
            holder.defeat.setVisibility(View.GONE);
            holder.mIcon.setBorderColor(mContext.getResources().getColor(R.color.backgroud_red));
        } else if (programBean.getUpload_state() != null && programBean.getUpload_state().equals("-1")) {//上传失败
            holder.defeat.setVisibility(View.VISIBLE);
            holder.mIcon.setBorderColor(mContext.getResources().getColor(R.color.border_grey));
        } else {//未上传
            holder.defeat.setVisibility(View.GONE);
            holder.mIcon.setBorderColor(mContext.getResources().getColor(R.color.border_grey));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemClickListen(view, holder.getLayoutPosition());
            }
        });
        if (isCompileState) {
            holder.program_delete.setVisibility(View.VISIBLE);
        } else
            holder.program_delete.setVisibility(View.GONE);
        holder.program_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemDeleteClickListen(view, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 改变节目编辑状态
     */
    public void changeCompileState() {
        if (isCompileState) {
            isCompileState = false;
            for (BaseProgram bean : mData)
                bean.setCheck(false);
        } else
            isCompileState = true;
        notifyDataSetChanged();
    }

    public boolean getCompileState() {
        return isCompileState;
    }

    public BaseProgram getData(int position) {
        return mData.get(position);
    }

    MyItemClick mOnItemSelectClick;

    public void setMyItemSelectClick(MyItemClick onItemSelectClick) {
        mOnItemSelectClick = onItemSelectClick;
    }

    public interface MyItemClick {
        void onItemClickListen(View view, int position);

        void onItemProgressClickListen(View view, int position);

        void onItemDeleteClickListen(View view, int position);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public com.makeramen.roundedimageview.RoundedImageView mIcon;
        public TextView mTime;
        public TextView mTab;
        public View mView;
        public ImageView defeat;
        public ImageView program_delete;
        public CircleProgress mProgress;
        public TextView load_state;


        public MyHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mIcon = (com.makeramen.roundedimageview.RoundedImageView) itemView.findViewById(R.id.iv_program_icon);
            mTime = (TextView) itemView.findViewById(R.id.tv_program_time);
            mTab = (TextView) itemView.findViewById(R.id.tv_program_tab);
            defeat = (ImageView) itemView.findViewById(R.id.iv_upload_defeat);
            program_delete = (ImageView) itemView.findViewById(R.id.iv_program_item_delete);
            mProgress = (CircleProgress) itemView.findViewById(R.id.program_item_progress);
            load_state = (TextView) itemView.findViewById(R.id.tv_load_state);
        }
    }

    public void update(ArrayList<BaseProgram> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}
