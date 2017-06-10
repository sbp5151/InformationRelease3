package com.jld.InformationRelease.view.my_terminal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.util.LogUtil;

import java.util.List;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/18 14:30
 * <p>
 * 设备终端适配器
 */
public class TerminalAdapter extends RecyclerView.Adapter<TerminalAdapter.ViewHolder> {

    private static final String TAG = "TerminalAdapter";
    private List<TerminalBeanSimple> beans;
    private Context mContext;
    //是否处于编辑状态
    private boolean isCompile = false;

    public TerminalAdapter(List<TerminalBeanSimple> beans, Context context) {
        this.beans = beans;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.terminal_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onItemClick(view, holder.getLayoutPosition());
                if (isCompile) {
                    TerminalBeanSimple simple = beans.get(holder.getLayoutPosition());
                    simple.setCheck(!simple.getCheck());
                    notifyDataSetChanged();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemLongClick(view, holder.getLayoutPosition());
                }
                return true;
            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TerminalBeanSimple simple = beans.get(holder.getLayoutPosition());
                simple.setCheck(holder.mCheckBox.isChecked());
            }
        });
        TerminalBeanSimple terminal = beans.get(position);
        LogUtil.d(TAG, "terminal:" + terminal);
        LogUtil.d(TAG, "getCheck:" + terminal.getCheck());
        holder.mCheckBox.setChecked(terminal.getCheck());
        if (isCompile && "1".equals(terminal.getState()))//在线并处于编辑状态
            holder.mCheckBox.setVisibility(View.VISIBLE);
        else
            holder.mCheckBox.setVisibility(View.GONE);

        holder.tv_id.setText(terminal.getId());
        holder.tv_name.setText(terminal.getName());
        switch (terminal.getState()) {
            case "0"://离线
                holder.tv_state.setText(mContext.getResources().getString(R.string.terminal_state_offline));
                holder.iv_state.setImageResource(R.drawable.corners_offline);
                break;
            case "1"://在线
                holder.tv_state.setText(mContext.getResources().getString(R.string.terminal_state_online));
                holder.iv_state.setImageResource(R.drawable.corners_online);
                break;
            case "2"://关机
                holder.tv_state.setText(mContext.getResources().getString(R.string.terminal_state_showdown));
                holder.iv_state.setImageResource(R.drawable.corners_showdown);
                break;
            case "3"://故障
                holder.tv_state.setText(mContext.getResources().getString(R.string.terminal_state_fault));
                holder.iv_state.setImageResource(R.drawable.corners_fault);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_id;
        TextView tv_name;
        TextView tv_state;
        ImageView iv_state;
        CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_terminal_item_id);
            tv_name = (TextView) itemView.findViewById(R.id.tv_terminal_item_name);
            tv_state = (TextView) itemView.findViewById(R.id.tv_terminal_item_state);
            iv_state = (ImageView) itemView.findViewById(R.id.iv_terminal_item_state);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.terminal_check);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    OnRecyclerViewItemClickListener mClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mClickListener = listener;
    }

    public List<TerminalBeanSimple> getData() {
        return beans;
    }

    public void setCompile(boolean isCompile) {
        if (!isCompile) {
            for (TerminalBeanSimple beanSimple : beans)
                beanSimple.setCheck(false);
        }
        this.isCompile = isCompile;
    }

    public boolean isCompile() {
        return isCompile;
    }

    public void setDataChange(List<TerminalBeanSimple> beans) {
        this.beans = beans;
        LogUtil.d(TAG, "setDataChange:" + beans);
        notifyDataSetChanged();
    }
}
