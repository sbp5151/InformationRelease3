package com.jld.InformationRelease.view.my_terminal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_terminal.ProgramCompileActivity2;

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
public class RecyclerCompileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerCommodityAdapter";
    private ProgramBean mBean;
    private ProgramCompileActivity2 mContext;
    public static final int ITEM_TAG_HEAD_1 = 1;
    public static final int ITEM_TAG_HEAD_2 = 2;
    public static final int ITEM_TAG_IMG = 3;
    public static final int ITEM_TAG_COMMODITY = 4;

    private static enum ITEM_TYPE {
        ITEM_TYPE_COMMODITY,//名字价格
        ITEM_TYPE_IMG,//图片
        ITEM_TYPE_HEAD,//img_head
    }

    public RecyclerCompileAdapter(ProgramBean bean, Context context) {
        mBean = bean;
        mContext = (ProgramCompileActivity2) context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ITEM_TYPE.ITEM_TYPE_HEAD.ordinal();
        else if (position <= mBean.getImages().size())
            return ITEM_TYPE.ITEM_TYPE_IMG.ordinal();
        else if (position == mBean.getImages().size() + 1)
            return ITEM_TYPE.ITEM_TYPE_HEAD.ordinal();
        else return ITEM_TYPE.ITEM_TYPE_COMMODITY.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEAD.ordinal()) {//head
            View contentView = inflater.inflate(R.layout.item_head_add, parent, false);
            viewHolder = new HeadHolder(contentView);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_IMG.ordinal()) {//img
            View contentView = inflater.inflate(R.layout.item_img_add, parent, false);
            viewHolder = new ImgHolder(contentView);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_COMMODITY.ordinal()) {//name
            View contentView = inflater.inflate(R.layout.item_commodity_add, parent, false);
            viewHolder = new ComHolder(contentView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeadHolder) {//head
            HeadHolder headHolder = (HeadHolder) holder;
            if (position == 0) {//图片head1
                headHolder.view.setTag(ITEM_TAG_HEAD_1);
                headHolder.head_text.setText(mContext.getResources().getString(R.string.add_imgs));
            } else if (mContext.modleId.equals("002")) {// "002"不添加 head2
                headHolder.view.setVisibility(View.GONE);
            } else {
                headHolder.view.setTag(ITEM_TAG_HEAD_2);
                headHolder.head_text.setText(mContext.getResources().getString(R.string.add_commoditys));
                headHolder.head_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        addCommodityItem();
                        mMyItemClick.onHead2ItemClick(view, holder.getLayoutPosition());
                    }
                });
            }
        } else if (holder instanceof ImgHolder) {//图片选择
            ImgHolder imgHolder = (ImgHolder) holder;
            imgHolder.view.setTag(ITEM_TAG_IMG);

            //减去头部item
            imgHolder.path.setText(mBean.getImages().get(position - 1));
            imgHolder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMyItemClick.onImgItemClick(view, getRealPositionCom(holder.getLayoutPosition()));
                }
            });
        } else if (holder instanceof ComHolder) {//名称价格
            ComHolder comHolder = (ComHolder) holder;
            comHolder.view.setTag(ITEM_TAG_COMMODITY);
//            int size = mBean.getImages().size();
//            //减去imagpath和两个head 获得真实位置
//            int realPosition = position - size - 2;
            int realPosition = getRealPositionImg(position);
            ProgramBean.Commodity commodity = mBean.getCommoditys().get(realPosition);
            comHolder.name.setText(commodity.getName());
            comHolder.price.setText(commodity.getPrice());
            if (realPosition == (mBean.getCommoditys().size() - 1)) {
                //最底下item获得焦点
                comHolder.name.requestFocus();
            }
            //text改变监听
            comHolder.name.addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        mBean.getCommoditys().get(getRealPositionImg(holder.getLayoutPosition())).setName(charSequence.toString());
                    }
                }
            });
            comHolder.price.addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        mBean.getCommoditys().get(getRealPositionImg(holder.getLayoutPosition())).setPrice(charSequence.toString());
                    }
                }
            });
        }
    }

    private MyItemClick mMyItemClick;

    /**
     * 添加点击监听
     *
     * @param onItemSelectClick
     */
    public void setMyItemSelectClick(MyItemClick onItemSelectClick) {
        this.mMyItemClick = onItemSelectClick;
    }

    public interface MyItemClick {
        public void onImgItemClick(View view, int position);

        public void onHead2ItemClick(View view, int position);
    }

    public void addImgItem() {
        ArrayList<String> images = mBean.getImages();
        if (images.size() > 10) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.most_add_imgs), 3000);
            return;
        }
        images.add("");
        notifyDataSetChanged();
    }

    public void addCommodityItem() {
        ArrayList<ProgramBean.Commodity> commoditys = mBean.getCommoditys();
        if (commoditys.size() > 20) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.most_add_commoditys), 3000);
            return;
        }
        commoditys.add(new ProgramBean.Commodity("", ""));
        mBean.setCommoditys(commoditys);
        notifyDataSetChanged();
    }

    /**
     * @param isClear 是否清除空值
     * @return
     */
    public ArrayList<ProgramBean.Commodity> getCommodityData(boolean isClear) {
        ArrayList<ProgramBean.Commodity> commoditys = mBean.getCommoditys();
        Log.d(TAG, "commodityssize:" + commoditys.size());
        if (isClear) {
            //去除空值
            for (int i = 0; i < commoditys.size(); i++) {
                Log.d(TAG, "commodityssize-i:" + i);
                ProgramBean.Commodity commodity = commoditys.get(i);
                if (TextUtils.isEmpty(commodity.getName()) || TextUtils.isEmpty(commodity.getPrice())) {
                    commoditys.remove(i);
                    i--;
                }
            }
            notifyDataSetChanged();
            Log.d(TAG, "commoditys:" + commoditys);
            return commoditys;
        } else {//不清除空值
            ArrayList<ProgramBean.Commodity> commoditys2 = new ArrayList<>();
            for (int i = 0; i < commoditys.size(); i++) {
                Log.d(TAG, "commodityssize-i:" + i);
                ProgramBean.Commodity commodity = commoditys.get(i);
                if (!TextUtils.isEmpty(commodity.getName()) && !TextUtils.isEmpty(commodity.getPrice())) {
                    commoditys2.add(commodity);
                }
            }
            return commoditys2;
        }
    }

    /**
     * @param isClear 是否清除空值
     * @return
     */
    public ArrayList<String> getImgData(boolean isClear) {
        ArrayList<String> images = mBean.getImages();
        Log.d(TAG, "imagessize:" + images.size());
        if (isClear) {
            for (int i = 0; i < images.size(); i++) {
                Log.d(TAG, "imagessize-i:" + i);
                String img = images.get(i);
                if (TextUtils.isEmpty(img)) {
                    images.remove(i);
                    i--;
                }
            }
            notifyDataSetChanged();
            Log.d(TAG, "images:" + images);
            return images;
        } else {
            ArrayList<String> images2 = new ArrayList<>();

            for (int i = 0; i < images.size(); i++) {
                String img = images.get(i);
                if (!TextUtils.isEmpty(img)) {
                    images2.add(img);
                }
            }
            return images2;
        }
    }

    @Override
    public int getItemCount() {
        return mBean.getImages().size() + mBean.getCommoditys().size() + 2;
    }

    class ComHolder extends RecyclerView.ViewHolder {

        EditText name;
        EditText price;
        View view;

        public ComHolder(View itemView) {
            super(itemView);
            name = (EditText) itemView.findViewById(R.id.et_commodity_name);
            price = (EditText) itemView.findViewById(R.id.et_commodity_price);
            view = itemView;
        }
    }

    class ImgHolder extends RecyclerView.ViewHolder {

        TextView path;
        Button select;
        View view;

        public ImgHolder(View itemView) {
            super(itemView);
            path = (TextView) itemView.findViewById(R.id.tv_img_path);
            select = (Button) itemView.findViewById(R.id.bt_img_select);
            view = itemView;
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        TextView head_text;
        ImageButton head_add;
        View view;

        public HeadHolder(View itemView) {
            super(itemView);
            head_text = (TextView) itemView.findViewById(R.id.tv_compile_head_text);
            head_add = (ImageButton) itemView.findViewById(R.id.bt_compile_head_add);
            view = itemView;
        }
    }

    /**
     * 获取真实position
     *
     * @param position
     * @return
     */
    public int getRealPositionImg(int position) {
        int size = mBean.getImages().size();
        //减去imagpath和两个head 获得真实位置
        return position - size - 2;
    }

    /**
     * 获取真实position
     *
     * @param position
     * @return
     */
    public int getRealPositionCom(int position) {
        //减去imagpath head 获得真实位置
        return position - 1;
    }
}
