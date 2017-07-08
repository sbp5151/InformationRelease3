package com.jld.InformationRelease.view.my_program.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.DayTaskBean;

import java.util.ArrayList;
/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/6 15:03
 */
public class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.MyHolder> {

    public ArrayList<DayTaskBean.DayTaskItem> datas;
    private Context mContext;

    public DayTaskAdapter(ArrayList<DayTaskBean.DayTaskItem> datas, Context context) {
        this.datas = datas;
        mContext = context;
    }

    @Override
    public DayTaskAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_day_task, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final DayTaskAdapter.MyHolder holder, int position) {
        DayTaskBean.DayTaskItem data = datas.get(position);
        if (!TextUtils.isEmpty(data.getProgramName()))
            holder.name.setText(data.getProgramName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListen != null)
                    mOnItemClickListen.onNameClickListen(view,holder.getLayoutPosition());
            }
        });

        if (!TextUtils.isEmpty(data.getStateTime()))
            holder.stateTime.setText(data.getStateTime());
        holder.stateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListen != null)
                    mOnItemClickListen.onStartClickListen(view,holder.getLayoutPosition());
            }
        });

        if (!TextUtils.isEmpty(data.getStopTime()))
            holder.stopTime.setText(data.getStopTime());
        holder.stopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListen != null)
                    mOnItemClickListen.onStopClickListen(view,holder.getLayoutPosition());
            }
        });
    }

    public void addItem(DayTaskBean.DayTaskItem data) {
        datas.add(data);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeItem(int position) {
        datas.remove(position);
        notifyItemRemoved(position);
    }

    OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen listen) {
        mOnItemClickListen = listen;
    }

    public interface OnItemClickListen {

        void onNameClickListen(View view,int position);

        void onStartClickListen(View view,int position);

        void onStopClickListen(View view,int position);
    }

    @Override
    public int getItemCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        Button name;
        Button stateTime;
        Button stopTime;

        public MyHolder(View view) {
            super(view);
            name = (Button) view.findViewById(R.id.btn_day_task_program_name);
            stateTime = (Button) view.findViewById(R.id.btn_day_task_start_time);
            stopTime = (Button) view.findViewById(R.id.btn_day_task_stop_time);
        }
    }

}
