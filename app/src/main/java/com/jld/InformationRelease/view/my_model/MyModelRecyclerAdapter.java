package com.jld.InformationRelease.view.my_model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseRecyclerViewAdapterClick;
import com.jld.InformationRelease.bean.ProgramBean;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/25 18:00
 */
public class MyModelRecyclerAdapter extends RecyclerView.Adapter<MyModelRecyclerAdapter.MyHolder> implements BaseRecyclerViewAdapterClick {

    private Context mContext;
    private ArrayList<ProgramBean> data;

    public MyModelRecyclerAdapter(Context context, ArrayList<ProgramBean> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.my_model_item, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        ProgramBean programBean = data.get(position);
        holder.mTime.setText(programBean.getCreation_time());
        if (programBean.getIsLoad().equals("1"))
            holder.mIcon.setImageResource(R.mipmap.model_icon_update);
        else
            holder.mIcon.setImageResource(R.mipmap.model_icon);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemClick(view,holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    MyItemClick mOnItemSelectClick;
    @Override
    public void setMyItemSelectClick(MyItemClick onItemSelectClick) {
        mOnItemSelectClick = onItemSelectClick;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTime;
        public View mView;

        public MyHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mIcon = (ImageView) itemView.findViewById(R.id.iv_model_icon);
            mTime = (TextView) itemView.findViewById(R.id.tv_model_time);
        }
    }

    public void update(ArrayList<ProgramBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
