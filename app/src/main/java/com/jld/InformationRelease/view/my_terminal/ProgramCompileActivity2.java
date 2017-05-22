package com.jld.InformationRelease.view.my_terminal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.request_bean.ProgramRequestBean;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.presenter.BitmapUtilPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCompileAdapter;
import com.jld.InformationRelease.view.my_terminal.preview.PreviewActivity_1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.tag;
import static com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCompileAdapter.ITEM_TAG_COMMODITY;
import static com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCompileAdapter.ITEM_TAG_HEAD_1;
import static com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCompileAdapter.ITEM_TAG_HEAD_2;
import static com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCompileAdapter.ITEM_TAG_IMG;

/**
 * 三：节目编辑
 */
public class ProgramCompileActivity2 extends BaseActivity implements IViewToPresenter<BaseResponse> {

    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    private static final String TAG = "ProgramCompileActivity";
    private static final int REQUEST_TAG = 0x00;
    ArrayList<ProgramRequestBean.Commodity> mCommodities = new ArrayList();
    ArrayList<String> mImgs = new ArrayList();
    private RecyclerCompileAdapter mAdapter;
    private int mGetImgPath;
    private ProgressDialog mDialog;
    private ArrayList<String> mCheckMacs;
    private String modleId;
    private RecyclerView mRecyclerView;
    private View mHead_head;
    private TextView mTv_head;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_compile2);
        //获取mac 地址
        mCheckMacs = (ArrayList<String>) getIntent().getSerializableExtra("checkMacs");
        //获取节目模板ID
        modleId = (String) getIntent().getSerializableExtra("modelid");
        modleId = "001";
        LogUtil.d(TAG, "mCheckMacs:" + mCheckMacs);
        initView();
        mDialog = new ProgressDialog(this);
        LogUtil.d(TAG, "onCreate");
    }

    private void initView() {
        /***********************title***********************/
        View title = findViewById(R.id.program_compile_titlebar);
        LinearLayout back = (LinearLayout) title.findViewById(R.id.title_back);
        back.setOnClickListener(mOnClickListener);
        TextView tv_preview = (TextView) title.findViewById(R.id.title_preview);
        tv_preview.setVisibility(View.VISIBLE);
        tv_preview.setOnClickListener(mOnClickListener);
        TextView conter = (TextView) findViewById(R.id.title_center);
        conter.setText(getString(R.string.program_compile));
        TextView right = (TextView) findViewById(R.id.title_right);
        right.setText(getString(R.string.push));
        right.setOnClickListener(mOnClickListener);
        /***********************head***********************/
        //主head
        mHead_head = findViewById(R.id.program_compile_head);
        ImageButton add_item = (ImageButton) mHead_head.findViewById(R.id.bt_compile_head_add);
        add_item.setOnClickListener(mOnClickListener);
        mTv_head = (TextView) mHead_head.findViewById(R.id.tv_compile_head_text);
        add_item.setOnClickListener(mOnClickListener);
        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_program_compile);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//动画
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        //initData
        ProgramRequestBean bean = new ProgramRequestBean();
        mCommodities.add(new ProgramRequestBean.Commodity("", ""));
        mImgs.add("");
        bean.setCommoditys(mCommodities);
        bean.setImages(mImgs);
        mAdapter = new RecyclerCompileAdapter(bean, this);
        mRecyclerView.setAdapter(mAdapter);
        //滑动监听
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        //点击监听
        mAdapter.setMyItemSelectClick(mMyItemClick);
    }
    RecyclerCompileAdapter.MyItemClick mMyItemClick = new RecyclerCompileAdapter.MyItemClick() {
        @Override
        public void onImgItemClick(View view, int position) {
            onItemClick(view,position);
        }
        @Override
        public void onHead2ItemClick(View view, int position) {
            LogUtil.d(TAG,"onHead2ItemClick:"+position);
            mAdapter.addCommodityItem();
            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
        }
    };
    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //距离顶部五个像素的view
            View headView = recyclerView.findChildViewUnder(mHead_head.getMeasuredWidth() / 2, 1);
            //距离第一个item一个像素的view
            View transInfoView = recyclerView.findChildViewUnder(
                    mHead_head.getMeasuredWidth() / 2, mHead_head.getMeasuredHeight() + 1);
            int headView_tag = (int) headView.getTag();
            int transInfoView_tag = (int) transInfoView.getTag();
            Log.d(TAG, "itemTag--:" + tag);
            //head
            if (headView_tag == ITEM_TAG_HEAD_1 || headView_tag == ITEM_TAG_HEAD_2) {
                //设置tag 点击区分
                mHead_head.setTag(headView_tag);
                TextView tv_view = (TextView) headView.findViewById(R.id.tv_compile_head_text);
                mTv_head.setText(tv_view.getText().toString());
            } else {//非head  各自显示自己的head
                if (headView_tag == ITEM_TAG_IMG) {
                    mHead_head.setTag(ITEM_TAG_HEAD_1);
                    mTv_head.setText(getResources().getString(R.string.add_imgs));
                } else if (headView_tag == ITEM_TAG_COMMODITY) {
                    mHead_head.setTag(ITEM_TAG_HEAD_2);
                    mTv_head.setText(getResources().getString(R.string.add_commoditys));
                }
            }
            if (transInfoView_tag == ITEM_TAG_HEAD_2) {
                int transY = transInfoView.getTop() - mHead_head.getMeasuredHeight();
                Log.d(TAG, "transY:" + transY);
                if (transInfoView.getTop() > 0) {//当商品head不再最顶部是移动主head
                    mHead_head.setTranslationY(transY);
                } else {
                    mHead_head.setTranslationY(0);
                }
            } else//当把商品head移动到最顶端，恢复主head
                mHead_head.setTranslationY(0);
        }
    };

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_compile_head_add:
                    int tag = (int) mHead_head.getTag();
                    if (tag == ITEM_TAG_HEAD_1) {
                        mAdapter.addImgItem();
                    } else if (tag == ITEM_TAG_HEAD_2) {
                        mAdapter.addCommodityItem();
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    }
                    break;
                case R.id.title_back:
                    finish();
                    break;
                case R.id.title_right://节目推送
                    SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
                    String userID = sp.getString(UserConstant.USER_ID, "");
                    if (TextUtils.isEmpty(userID)) {//账号不能为空
                        ToastUtil.showToast(ProgramCompileActivity2.this, getResources().getString(R.string.please_login), 3000);
                        return;
                    }
                    ArrayList<String> imgs = mAdapter.getImgData(true);
                    if (imgs.size()==0) {//图片不能为空
                        ToastUtil.showToast(ProgramCompileActivity2.this, getString(R.string.please_set_img), 3000);
                        return;
                    }
                    ArrayList<ProgramRequestBean.Commodity> data = mAdapter.getCommodityData(true);
                    if (data.size()==0) {//名称和价格不能为空
                        ToastUtil.showToast(ProgramCompileActivity2.this, getString(R.string.please_set_commodity), 3000);
                        return;
                    }

                    ProgramRequestBean body = new ProgramRequestBean();
                    body.setCommoditys(data);//名称和价格
                    body.setImages(imgs);//图片广告
                    body.setDeviceMacs(mCheckMacs);//需要推送终端的Mac地址
                    body.setModelId(modleId);//模板ID
                    body.setUserID(userID);//账号
                    body.setSign(MD5Util.getMD5(Constant.S_KEY + userID));//加密字符串

                    Intent intent = new Intent(ProgramCompileActivity2.this, MainActivity.class);
                    intent.putExtra("body", body);
                    setResult(0x11, intent);//编辑结果返回
                    finish();
                    break;
                case R.id.title_preview://预览
                    ArrayList<String> preview_imgs = mAdapter.getImgData(false);
                    if (preview_imgs.size()==0) {//图片不能为空
                        ToastUtil.showToast(ProgramCompileActivity2.this, getString(R.string.please_set_img), 3000);
                        return;
                    }
                    ArrayList<ProgramRequestBean.Commodity> preview_data = mAdapter.getCommodityData(false);
                    if (preview_data.size()==0) {//名称和价格不能为空
                        ToastUtil.showToast(ProgramCompileActivity2.this, getString(R.string.please_set_commodity), 3000);
                        return;
                    }

                    ProgramRequestBean preview_body = new ProgramRequestBean();
                    preview_body.setCommoditys(preview_data);//名称和价格
                    preview_body.setImages(preview_imgs);//图片广告
                    preview_body.setModelId(modleId);//模板ID
                    toActivity(PreviewActivity_1.class, preview_body, "previewData");
                    break;
            }
        }
    };

    /**
     * 选择相片
     *
     * @param view
     * @param position
     */
    public void onItemClick(View view, int position) {
        mGetImgPath = position;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 相片返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null) {
            Uri uri = data.getData();
            Bitmap photoBmp = null;
            if (uri != null) {
                try {
                    //所获取图片的bitmap
                    photoBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    if (photoBmp != null) {
                        String path = uri.getEncodedPath();
                        LogUtil.d(TAG, "path:" + path);
                        String[] split = path.split(File.separator);
                        String imgName = split[split.length - 1];
                        LogUtil.d(TAG, "imgName:" + imgName);

                        String miniPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miniPhoto";
                        File file = new File(miniPath);
                        if (!file.exists())
                            file.mkdirs();
                        String miniImgPath = miniPath + File.separator + imgName + "mini.jpg";
                        LogUtil.d(TAG, "miniImgPath:" + miniImgPath);

                        if (!new File(miniImgPath).exists()) {//没有压缩过进行压缩
                            LogUtil.d(TAG, "压缩:");
                            //图片压缩
                            BitmapUtilPresenter presenter = new BitmapUtilPresenter(mBitmapCompressListen);
                            //图片压缩RxJava
                            presenter.bitmapCompress(photoBmp, miniImgPath);
                        } else {
                            LogUtil.d(TAG, "不压缩:");
                            replaceImg(miniImgPath);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将图片路径更换成url
     *
     * @param strPath
     */
    public void replaceImg(String strPath) {
        mImgs.remove(mGetImgPath);
        mImgs.add(mGetImgPath, strPath);
        mAdapter.notifyDataSetChanged();
        LogUtil.d(TAG, "path:" + strPath);
    }

    //图片压缩返回监听
    BitmapUtilPresenter.BitmapCompressListen mBitmapCompressListen = new BitmapUtilPresenter.BitmapCompressListen() {

        @Override
        public void compressSucceed(String compressPath) {//成功并返回结果
            replaceImg(compressPath);
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        }

        @Override
        public void compressError() {//失败
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        }

        @Override
        public void onStart() {//开始
            if (mDialog != null && !mDialog.isShowing())
                mDialog.show();
        }
    };

    @Override
    public void showProgress(int requestTag) {
        if (mDialog != null && !mDialog.isShowing())
            mDialog.show();
        LogUtil.d("showProgress");
    }

    @Override
    public void hideProgress(int requestTag) {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        LogUtil.d("showProgress");
    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        //TODO 推送成功
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        hideProgress(requestTag);
        ToastUtil.showToast(this, e.getMessage().toString(), 3000);
    }
}
