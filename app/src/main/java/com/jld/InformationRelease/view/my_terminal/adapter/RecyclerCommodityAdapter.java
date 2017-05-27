package com.jld.InformationRelease.view.my_terminal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MyTextWatcher;
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
 * 商品的名称和价格添加
 */
public class RecyclerCommodityAdapter extends RecyclerView.Adapter<RecyclerCommodityAdapter.ViewHolder> {

    private static final String TAG = "RecyclerCommodityAdapter";
    private ArrayList<ProgramBean.Commodity> mCommoditys;
    private Context mContext;

    public RecyclerCommodityAdapter(ArrayList<ProgramBean.Commodity> commoditys, Context context) {
        mCommoditys = commoditys;
        mContext = context;

    }

    @Override
    public int getItemViewType(int position) {


        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_commodity_add, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ProgramBean.Commodity bean = mCommoditys.get(position);
        holder.name.setText(bean.getName());
        holder.price.setText(bean.getPrice());
        LogUtil.d(TAG,"position:"+position);
        LogUtil.d(TAG,"mCommoditys.size():"+mCommoditys.size());
        if(position==(mCommoditys.size()-1)){
            holder.name.requestFocus();
        }

        holder.name.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LogUtil.d(TAG, "holder.getLayoutPosition():" + holder.getLayoutPosition());
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    //最后一个名称和价格都不为空 ，允许添加下一栏
                    mCommoditys.get(holder.getLayoutPosition()).setName(charSequence.toString());
                    if (holder.getLayoutPosition() == (mCommoditys.size() - 1) && !TextUtils.isEmpty(mCommoditys.get(holder.getLayoutPosition()).getPrice())) {
                        mTextChangedListener.onTextChanged();
                    }
                }
            }
        });
        holder.price.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LogUtil.d(TAG, "holder.getLayoutPosition():" + holder.getLayoutPosition());
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    //最后一个名称和价格都不为空 ，允许添加下一栏
                    mCommoditys.get(holder.getLayoutPosition()).setPrice(charSequence.toString());
                    if (holder.getLayoutPosition() == (mCommoditys.size() - 1) && !TextUtils.isEmpty(mCommoditys.get(holder.getLayoutPosition()).getName())) {
                        mTextChangedListener.onTextChanged();
                    }
                }
            }
        });
    }

    MyTextChangedListener mTextChangedListener;

    public void addMyTextChangedListener(MyTextChangedListener textChangedListener) {
        mTextChangedListener = textChangedListener;
    }

    public interface MyTextChangedListener {
        public void onTextChanged();
    }

    @Override
    public int getItemCount() {
        return mCommoditys.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        EditText name;
        EditText price;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (EditText) itemView.findViewById(R.id.et_commodity_name);
            price = (EditText) itemView.findViewById(R.id.et_commodity_price);
        }
    }

    public void addData(int position, ProgramBean.Commodity data) {
        if (mCommoditys.size() >= 20) {
            ToastUtil.showToast(mContext, "最多只能添加20张轮播图", 3000);
            return;
        }
        mCommoditys.add(position, data);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mCommoditys.size());
    }

    public void removeData(int position) {
        mCommoditys.remove(position);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mCommoditys.size());
    }

    public ArrayList<ProgramBean.Commodity> getData() {
        return mCommoditys;
    }
}
