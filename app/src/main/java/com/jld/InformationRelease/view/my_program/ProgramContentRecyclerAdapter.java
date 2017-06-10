package com.jld.InformationRelease.view.my_program;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseRecyclerViewAdapterClick;
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
public class ProgramContentRecyclerAdapter extends RecyclerView.Adapter<ProgramContentRecyclerAdapter.MyHolder> implements BaseRecyclerViewAdapterClick {

    private Context mContext;
    ProgramBean data;
    private String[] headData = {"发布状态", "轮播图片", "商品价格", "推送的设备ID", "创建时间", "模板ID", "标签"};
    private ArrayList<String> tailData = new ArrayList<>();

    public ProgramContentRecyclerAdapter(Context context, ProgramBean data) {
        mContext = context;
        this.data = data;
        //发布状态
        if ("1".equals(data.getState()))
            tailData.add("已发布");
        else if ("-1".equals(data.getState()))
            tailData.add("上传失败");
        else
            tailData.add("未发布");
        //轮播图片
        if (data.getImages() != null&&data.getImages().size()>0){
            StringBuffer buffer = new StringBuffer();
            for (String img : data.getImages()) {
                buffer.append(img + System.getProperty("line.separator"));
            }
            LogUtil.d("buffer", "" + buffer);
            buffer.deleteCharAt(buffer.length() - 1);
            LogUtil.d("buffer", "" + buffer);
            tailData.add(buffer.toString());
        }
        else
            tailData.add(mContext.getString(R.string.NULL));
        //商品价格
        if (data.getTexts() != null)
            tailData.add(data.getTexts().toString());
        else
            tailData.add(mContext.getString(R.string.NULL));
        //推送的设备ID
        if (data.getDeviceMacs() != null && data.getDeviceMacs().size() > 0) {
            LogUtil.d("getDeviceMacs", "" + data.getDeviceMacs());
            StringBuffer buffer = new StringBuffer();
            for (String mac : data.getDeviceMacs()) {
                buffer.append(mac + System.getProperty("line.separator"));
            }
            LogUtil.d("buffer", "" + buffer);
            buffer.deleteCharAt(buffer.length() - 1);
            LogUtil.d("buffer", "" + buffer);
            tailData.add(buffer.toString());
        } else
            tailData.add(mContext.getString(R.string.NULL));

        //创建时间
        if (!TextUtils.isEmpty(data.getCreation_time()))
            tailData.add(data.getCreation_time());
        else
            tailData.add(mContext.getString(R.string.NULL));
        //模板ID
        if (!TextUtils.isEmpty(data.getModelId()))
            tailData.add(data.getModelId());
        else
            tailData.add(mContext.getString(R.string.NULL));
        //标签
        if (!TextUtils.isEmpty(data.getTab()))
            tailData.add(data.getTab());
        else
            tailData.add(mContext.getString(R.string.NULL));

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.model_content_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        holder.head.setText(headData[position]);
        holder.tail.setText(tailData.get(position));
    }

    @Override
    public int getItemCount() {
        return tailData.size();
    }

    MyItemClick mOnItemSelectClick;

    @Override
    public void setMyItemSelectClick(MyItemClick onItemSelectClick) {
        mOnItemSelectClick = onItemSelectClick;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView head;
        public TextView tail;
        public View mView;

        public MyHolder(View itemView) {
            super(itemView);
            mView = itemView;
            head = (TextView) itemView.findViewById(R.id.tv_model_content_head);
            tail = (TextView) itemView.findViewById(R.id.tv_model_content_tail);
        }
    }

}
