package com.jld.InformationRelease.view.my_terminal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;

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
    private ArrayList<ProgramBean.Commodity> beans;
    private Context mContext;

    public RvNameAdapter(ArrayList<ProgramBean.Commodity> beans, Context context) {
        Log.d(TAG, "RvNameAdapter:"+beans);
        this.beans = beans;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder:");

        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_item, parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder:" + position);
        holder.tvPrice.setText(beans.get(position).getPrice());
        holder.tvName.setText(beans.get(position).getName());
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
