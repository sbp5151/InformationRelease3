package com.jld.InformationRelease.view.my_program;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.LogUtil;

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
    private ArrayList<ProgramBean> mData;
    public static final String TAG = "MyProgramRecyclerAdapter";
    private boolean isCompileState = false;//是否处于编辑状态


    public MyProgramRecyclerAdapter(Context context, ArrayList<ProgramBean> data) {
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
        ProgramBean programBean = mData.get(position);
        holder.mTime.setText(programBean.getCreation_time());
        holder.mTab.setText(programBean.getTab());
        if (programBean.getState() != null && programBean.getState().equals("1")) {
            holder.mIcon.setImageResource(R.mipmap.model_icon_update);
            holder.defeat.setVisibility(View.GONE);
        } else if (programBean.getState() != null && programBean.getState().equals("-1")) {
            holder.mIcon.setImageResource(R.mipmap.model_icon);
            holder.defeat.setVisibility(View.VISIBLE);
        } else {
            holder.mIcon.setImageResource(R.mipmap.model_icon);
            holder.defeat.setVisibility(View.GONE);
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
            for (ProgramBean bean : mData)
                bean.setCheck(false);
        } else
            isCompileState = true;
        notifyDataSetChanged();
    }

    public boolean getCompileState() {
        return isCompileState;
    }

    public ProgramBean getData(int position) {
        return mData.get(position);
    }

    MyItemClick mOnItemSelectClick;

    public void setMyItemSelectClick(MyItemClick onItemSelectClick) {
        mOnItemSelectClick = onItemSelectClick;
    }

    public interface MyItemClick {
        void onItemClickListen(View view, int position);

        void onItemDeleteClickListen(View view, int position);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTime;
        public TextView mTab;
        public View mView;
        public ImageView defeat;
        public ImageView program_delete;

        public MyHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mIcon = (ImageView) itemView.findViewById(R.id.iv_program_icon);
            mTime = (TextView) itemView.findViewById(R.id.tv_program_time);
            mTab = (TextView) itemView.findViewById(R.id.tv_program_tab);
            defeat = (ImageView) itemView.findViewById(R.id.iv_upload_defeat);
            program_delete = (ImageView) itemView.findViewById(R.id.iv_program_item_delete);
        }
    }

    public void update(ArrayList<ProgramBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
}
