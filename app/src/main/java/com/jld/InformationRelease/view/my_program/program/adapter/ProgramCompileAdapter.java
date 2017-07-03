package com.jld.InformationRelease.view.my_program.program.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MyTextWatcher;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_program.program.ProgramTextActivity;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/19 17:33
 * <p>
 * 节目编辑适配器
 */
public class ProgramCompileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ProgramCompileAdapter";
    private ProgramBean mDatas;
    private ProgramTextActivity mContext;
    //item tag
    public static final int ITEM_TAG_IMG_HEAD = 1;
    public static final int ITEM_TAG_TEXT_HEAD = 2;
    public static final int ITEM_TAG_IMG = 3;
    public static final int ITEM_TAG_TEXT = 4;
    public ProgramCompileImgItemAdapter mImgAdapter;
    private String[] names = {
            "草莓奶茶", "巧克力奶茶", "香芋奶茶", "哈密瓜奶茶", "木瓜奶茶", "椰子奶茶"
            , "咖啡奶茶", "西瓜奶茶", "香蕉奶茶", "柳橙奶茶", "葡萄奶茶", "双皮奶"
            , "烧仙草", "抹茶奶昔", "草莓奶昔", "香芋奶昔"};

    // item类型
    private enum ITEM_TYPE {
        ITEM_TYPE_TEXT,//文字
        ITEM_TYPE_IMG,//图片
        ITEM_TYPE_TEXT_HEAD,//文字head
        ITEM_TYPE_IMG_HEAD,//图片head
    }

    public ProgramCompileAdapter(ProgramBean bean, Context context, boolean isNew) {
        mDatas = bean;
        LogUtil.d(TAG, "ProgramBean:" + bean);
        mContext = (ProgramTextActivity) context;
        mImgAdapter = new ProgramCompileImgItemAdapter(mContext, mDatas.getImages(), mDatas.getCover());
        if (isNew) {//假数据
            ArrayList<ProgramBean.Commodity> texts = mDatas.getTexts();
            texts.remove(0);
            for (int i = 0; i < names.length; i++) {

                ProgramBean.Commodity commodity = new ProgramBean.Commodity();
                commodity.setName(names[i]);
                if (i < 10) {
                    commodity.setPrice("8");
                } else {
                    commodity.setPrice("10");
                }
                texts.add(commodity);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)//图片head
            return ITEM_TYPE.ITEM_TYPE_IMG_HEAD.ordinal();
        else if (position == 1)//图片
            return ITEM_TYPE.ITEM_TYPE_IMG.ordinal();
        else if (position == 2)//文字head
            return ITEM_TYPE.ITEM_TYPE_TEXT_HEAD.ordinal();
        else //文字
            return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == ITEM_TYPE.ITEM_TYPE_IMG_HEAD.ordinal()) {//图片head
            View contentView = inflater.inflate(R.layout.item_head_add, parent, false);////解决宽度不能铺满
            viewHolder = new ImgHeadHolder(contentView);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_IMG.ordinal()) {//图片
            View contentView = inflater.inflate(R.layout.item_img_add, parent, false);
            viewHolder = new ImgHolder(contentView);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_TEXT_HEAD.ordinal()) {//文字head
            View contentView = inflater.inflate(R.layout.item_head_add, parent, false);
            viewHolder = new TextHeadHolder(contentView);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_TEXT.ordinal()) {//文字
            View contentView = inflater.inflate(R.layout.item_text_add, parent, false);
            viewHolder = new TextHolder(contentView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ImgHeadHolder) {//图片head
            ImgHeadHolder headHolder = (ImgHeadHolder) holder;
            headHolder.view.setTag(ITEM_TAG_IMG_HEAD);
            headHolder.head_text.setText(mContext.getResources().getString(R.string.add_imgs));
        } else if (holder instanceof TextHeadHolder) {//文字 head
            TextHeadHolder headHolder = (TextHeadHolder) holder;
            headHolder.view.setTag(ITEM_TAG_TEXT_HEAD);
            headHolder.head_text.setText(mContext.getResources().getString(R.string.add_commoditys));
            //添加文字
            headHolder.head_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMyItemClick.onTextHeadClickListen(view, holder.getLayoutPosition());
                }
            });
        } else if (holder instanceof ImgHolder) {//图片选择
            ImgHolder imgHolder = (ImgHolder) holder;
            imgHolder.view.setTag(ITEM_TAG_IMG);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
            imgHolder.rv_img.setLayoutManager(layoutManager);

            imgHolder.rv_img.setAdapter(mImgAdapter);
        } else if (holder instanceof TextHolder) {//文字
            TextHolder comHolder = (TextHolder) holder;
            comHolder.view.setTag(ITEM_TAG_TEXT);
//            int size = mBean.getImages().size();
//            //减去imagpath和两个head 获得真实位置
//            int realPosition = position - size - 2;
            int realPosition = getRealPositionImg(position);
            ProgramBean.Commodity commodity = mDatas.getTexts().get(realPosition);
            comHolder.name.setText(commodity.getName());
            comHolder.price.setText(commodity.getPrice());

            if (realPosition == (mDatas.getTexts().size() - 1) && TextUtils.isEmpty(comHolder.name.getText())) {
                //最底下item获得焦点
                comHolder.name.requestFocus();
            }
            //text改变监听
            comHolder.name.addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        mDatas.getTexts().get(getRealPositionImg(holder.getLayoutPosition())).setName(charSequence.toString());
                    }
                }
            });
            comHolder.price.addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence != null) {
                        mDatas.getTexts().get(getRealPositionImg(holder.getLayoutPosition())).setPrice(charSequence.toString());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.getTexts().size() + 3;
    }

    /**
     * 获取真实position
     *
     * @param position
     * @return
     */
    public int getRealPositionImg(int position) {
        //减去imagpath和两个head 获得真实位置
        return position - 3;
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
        /**
         * 文字“添加”点击监听
         *
         * @param view
         * @param position
         */
        void onTextHeadClickListen(View view, int position);
    }

    /**
     * 添加文字item
     */
    public void addTextItem() {
        ArrayList<ProgramBean.Commodity> commoditys = mDatas.getTexts();
        if (commoditys.size() > 20) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.most_add_commoditys), 3000);
            return;
        }
        commoditys.add(new ProgramBean.Commodity("", ""));
        mDatas.setTexts(commoditys);
        notifyDataSetChanged();
    }

    /**
     * 添加图片item
     *
     * @param imgPath
     */
    public void addImgItem(String imgPath) {
        ArrayList<String> imgs = mImgAdapter.getDatas();
        if (imgs.size() > 20) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.most_add_commoditys), 3000);
            return;
        }
        mImgAdapter.addData(imgPath);
    }

    public ProgramBean getDatas() {
        return mDatas;
    }

    public void refreshImgs(ArrayList<String> photos) {
        if (photos != null && photos.size() > 20) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.most_add_commoditys), 3000);
            return;
        }
        if (photos != null) {
            mImgAdapter.refreshData(photos);
        }
    }

    /**
     * 获取text
     *
     * @param isClear 是否清除空值
     * @return
     */
    public ArrayList<ProgramBean.Commodity> getTextDatas(boolean isClear) {
        ArrayList<ProgramBean.Commodity> commoditys = mDatas.getTexts();
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
            Log.d(TAG, "texts:" + commoditys);
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
     * 获取img
     *
     * @param isClear 是否清除空值
     * @return
     */
    public ArrayList<String> getImgDatas(boolean isClear) {
        ArrayList<String> images = mImgAdapter.getDatas();
        Log.d(TAG, "imagessize:" + images.size());
        if (isClear) {
            for (int i = 0; i < images.size(); i++) {//封面不算
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

    /**
     * 获取封面
     */
    public String getCover() {
        return mImgAdapter.getCover();
    }

    public String getImgData(int position) {
        return mImgAdapter.getdata(position);
    }

    class TextHolder extends RecyclerView.ViewHolder {

        EditText name;
        EditText price;
        View view;

        public TextHolder(View itemView) {
            super(itemView);
            name = (EditText) itemView.findViewById(R.id.et_commodity_name);
            price = (EditText) itemView.findViewById(R.id.et_commodity_price);
            view = itemView;
        }
    }

    class ImgHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_img;
        View view;

        public ImgHolder(View itemView) {
            super(itemView);
            rv_img = (RecyclerView) itemView.findViewById(R.id.gv_program_compile_img_item);
            view = itemView;
        }
    }

    class ImgHeadHolder extends RecyclerView.ViewHolder {
        TextView head_text;
        ImageButton head_add;
        View view;

        public ImgHeadHolder(View itemView) {
            super(itemView);
            head_text = (TextView) itemView.findViewById(R.id.tv_compile_head_text);
            head_add = (ImageButton) itemView.findViewById(R.id.bt_compile_head_add);
            view = itemView;
        }
    }

    class TextHeadHolder extends RecyclerView.ViewHolder {
        TextView head_text;
        ImageButton head_add;
        View view;

        public TextHeadHolder(View itemView) {
            super(itemView);
            head_text = (TextView) itemView.findViewById(R.id.tv_compile_head_text);
            head_add = (ImageButton) itemView.findViewById(R.id.bt_compile_head_add);
            view = itemView;
        }
    }


}
