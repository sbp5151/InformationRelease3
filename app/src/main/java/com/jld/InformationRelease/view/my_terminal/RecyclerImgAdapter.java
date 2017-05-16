package com.jld.InformationRelease.view.my_terminal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/19 17:33
 * <p>
 * 轮播图添加
 */
public class RecyclerImgAdapter extends RecyclerView.Adapter<RecyclerImgAdapter.ViewHolder> {

    private ArrayList<String> mImgs;
    private Context mContext;

    public RecyclerImgAdapter(ArrayList<String> commoditys, Context context) {
        mImgs = commoditys;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_img_add, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String str = mImgs.get(position);
        holder.img_path.setText(str);
        holder.img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemSelectClick.onItemClick(view, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImgs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView img_path;
        Button img_select;

        public ViewHolder(View itemView) {
            super(itemView);
            img_path = (TextView) itemView.findViewById(R.id.tv_img_path);
            img_select = (Button) itemView.findViewById(R.id.bt_img_select);
        }
    }

    OnItemSelectClick mOnItemSelectClick;

    public void setOnItemSelectClick(OnItemSelectClick onItemSelectClick) {
        this.mOnItemSelectClick = onItemSelectClick;
    }

    public interface OnItemSelectClick {

        public void onItemClick(View view, int position);
    }

    public void addData(int position, String data) {
        if (mImgs.size() >= 6) {
            ToastUtil.showToast(mContext, "最多只能添加6张轮播图", 3000);
            return;
        }
        mImgs.add(position, data);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mImgs.size());
    }

    public void removeData(int position) {
        mImgs.remove(position);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mImgs.size());
    }

    public ArrayList<String> getData() {
        return mImgs;
    }

}
