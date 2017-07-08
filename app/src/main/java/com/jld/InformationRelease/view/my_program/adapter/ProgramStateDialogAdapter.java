package com.jld.InformationRelease.view.my_program.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramStateDialogItem;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 19:43
 */
public class ProgramStateDialogAdapter extends RecyclerView.Adapter<ProgramStateDialogAdapter.MyHolder> {

    public ArrayList<ProgramStateDialogItem> datas;
    private Context mContext;

    public ProgramStateDialogAdapter(ArrayList<ProgramStateDialogItem> datas, Context context) {
        this.datas = datas;
        mContext = context;
    }

    @Override
    public ProgramStateDialogAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.program_load_state_item, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(ProgramStateDialogAdapter.MyHolder holder, int position) {

        ProgramStateDialogItem item = datas.get(position);
        holder.mName.setText(item.getName());
        holder.mId.setText("ID: " + item.getId());
        if (item.isLoad())
            holder.mLoad.setImageResource(R.mipmap.check_yes);
        else
            holder.mLoad.setImageResource(R.mipmap.check_no);
    }

    @Override
    public int getItemCount() {
        if (datas == null)
            return 0;
        else return datas.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mId;
        private final ImageView mLoad;

        public MyHolder(View view) {
            super(view);
            mId = (TextView) view.findViewById(R.id.tv_program_load_item_id);
            mName = (TextView) view.findViewById(R.id.tv_program_load_item_name);
            mLoad = (ImageView) view.findViewById(R.id.cb_program_load_item);
        }
    }
}
