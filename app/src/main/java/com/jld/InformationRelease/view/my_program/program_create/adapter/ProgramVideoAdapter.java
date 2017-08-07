package com.jld.InformationRelease.view.my_program.program_create.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/19 17:33
 * <p>
 * 视频广告 节目编辑适配器
 */
public class ProgramVideoAdapter extends RecyclerView.Adapter<ProgramVideoAdapter.MyHolder> {

    private static final String TAG = "ProgramVideoAdapter";
    private ArrayList<String> mDada;
    private Context mContext;

    public ProgramVideoAdapter(ArrayList<String> paths, Context context) {
        this.mDada = paths;
        this.mContext = context;
    }

    @Override
    public ProgramVideoAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.program_compile_video_item, parent, false);
//        MyHolder myHolder = new MyHolder(view);
//        return myHolder;
        return new ProgramVideoAdapter.MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.program_compile_video_item, parent, false));

    }

    @Override
    public void onBindViewHolder(final ProgramVideoAdapter.MyHolder holder, int position) {
        holder.mPath.setText(mDada.get(position).replace("http://admsgimg.torsun.cn/",""));
    }

    @Override
    public int getItemCount() {
        LogUtil.d(TAG, "getItemCount:" + mDada);
        if (mDada == null) {
            return 0;
        }
        return mDada.size();
    }

    public void addItem() {
        if (mDada != null) {
            mDada.add("");
            notifyItemInserted(mDada.size() - 1);
        }
    }

    public String getItem(int position) {
        if (mDada != null) {
            return mDada.get(position);
        }
        return "";

    }

    public void removeItem(int position) {
        if (mDada != null) {
            mDada.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setPath(int position, String path) {
        if (mDada != null && position < mDada.size()) {
            mDada.set(position, path);
            notifyDataSetChanged();
        }
    }

    OnProgramVideoItemClickListen mMyItemClick;

    public void setMyOnClickListener(OnProgramVideoItemClickListen myItemClick) {
        mMyItemClick = myItemClick;
    }

    public interface OnProgramVideoItemClickListen {

        void onSelectItemClick(View view, int position);

        void onDeleteItemClick(View view, int position);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mPath;
        private Button mSelect;
        private Button mdelete;

        public MyHolder(View itemView) {
            super(itemView);
            mPath = (TextView) itemView.findViewById(R.id.tv_video_item_path);
            mSelect = (Button) itemView.findViewById(R.id.btn_video_item_path_select);
            mdelete = (Button) itemView.findViewById(R.id.btn_video_item_path_delete);
            mSelect.setOnClickListener(this);
            mdelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_video_item_path_delete:
                    mMyItemClick.onDeleteItemClick(view, getAdapterPosition());
                    break;
                case R.id.btn_video_item_path_select:
                    mMyItemClick.onSelectItemClick(view, getAdapterPosition());
                    break;
            }

        }
    }
}
