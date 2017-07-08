package com.jld.InformationRelease.view.my_program.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response_bean.GetModelResponse;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/18 14:30
 * <p>
 * 模板适配器
 */
public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {

    private static final String TAG = "ModelAdapter";
    private ArrayList<GetModelResponse.Model> beans = new ArrayList<>();
    private Context mContext;

    public ModelAdapter(ArrayList<GetModelResponse.Model> beans, Context context) {
        this.beans = beans;
        LogUtil.d(TAG, "beans:" + beans);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.model_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        GetModelResponse.Model modelResponse = beans.get(position);
        Glide.with(mContext)
                .load(modelResponse.getModelimg())
                .into(holder.iv_model);
        holder.tv_model.setText(modelResponse.getIntro());
        holder.cv_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, holder.getLayoutPosition());
            }
        });
        holder.cv_model.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, holder.getLayoutPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_model;
        ImageView iv_model;
        CardView cv_model;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_model = (TextView) itemView.findViewById(R.id.tv_model_item);
            iv_model = (ImageView) itemView.findViewById(R.id.iv_model_item);
            cv_model = (CardView) itemView.findViewById(R.id.cv_model_item);
        }
    }

    public interface ModeItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    ModeItemClickListener mClickListener;

    public void setModeItemClickListener(ModeItemClickListener listener) {
        mClickListener = listener;
    }

    public List<GetModelResponse.Model> getData() {
        return beans;
    }

    public GetModelResponse.Model getModel(int position) {
        return beans.get(position);
    }

    public void setDataChange(ArrayList<GetModelResponse.Model> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }
}
