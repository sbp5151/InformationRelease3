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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.interfaces.IViewToPresenter;
import com.jld.InformationRelease.presenter.BitmapUtilPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ModelId;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.my_terminal.adapter.RecyclerCommodityAdapter;
import com.jld.InformationRelease.view.my_terminal.adapter.RecyclerImgAdapter;
import com.jld.InformationRelease.view.my_terminal.preview.PreviewActivity_1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 三：节目编辑
 */
public class ProgramCompileActivity extends BaseActivity implements RecyclerImgAdapter.OnItemSelectClick, IViewToPresenter<BaseResponse> {

    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    private static final String TAG = "ProgramCompileActivity";
    private static final int REQUEST_TAG = 0x00;
    ArrayList<ProgramBean.Commodity> mCommodities = new ArrayList();
    ArrayList<String> mImgs = new ArrayList();
    private RecyclerCommodityAdapter mCommodityAdapter;
    private RecyclerImgAdapter mImgAdapter;
    private int mGetImgPath;
    private ProgressDialog mDialog;
    private ImageButton mImg_add;
    private ImageButton mCommodity_add;
    private ArrayList<String> mCheckMacs;
    private String modleId;
    private RecyclerView mRecyclerView_com;
    private RecyclerView mRecyclerView_img;
    private View mImg_head;
    private View mText_head;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_compile);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        //纯图片不用编辑文字
        LinearLayout ll_commodity = (LinearLayout) findViewById(R.id.ll_commodity);
        LinearLayout ll_img = (LinearLayout) findViewById(R.id.ll_img);
        if (modleId.equals(ModelId.modle_002)) {
            ll_commodity.setVisibility(View.GONE);
        }
        //title
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
        //图片head
        mImg_head = findViewById(R.id.program_compile_head_img);
        //文字head
        mText_head = findViewById(R.id.program_compile_head_text);
//        TextView img_tv_head = (TextView) img_head.findViewById(R.id.tv_compile_head_text);
//        mCommodity_add = (ImageButton) img_head.findViewById(R.id.bt_compile_head_add);
//        mCommodity_add.setOnClickListener(mOnClickListener);
//        mCommodity_add.setEnabled(false);
//        mImg_add = (ImageButton) findViewById(R.id.bt_img_add);
//        mImg_add.setEnabled(false);
//        mImg_add.setOnClickListener(mOnClickListener);

//        ScrollView
        final ScrollView scrollView = (ScrollView) findViewById(R.id.program_compile_scroll);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

            }
        });
        //CommodityRecycler
        mRecyclerView_com = (RecyclerView) findViewById(R.id.rv_compile_commodity);
        mRecyclerView_com.setItemAnimator(new DefaultItemAnimator());//动画
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
        mRecyclerView_com.setLayoutManager(layoutManager);
        mCommodities.add(new ProgramBean.Commodity("", ""));
        mCommodityAdapter = new RecyclerCommodityAdapter(mCommodities, this);
        mCommodityAdapter.addMyTextChangedListener(new RecyclerCommodityAdapter.MyTextChangedListener() {
            @Override
            public void onTextChanged() {
                mCommodity_add.setEnabled(true);
            }
        });
        mRecyclerView_com.setAdapter(mCommodityAdapter);
        //imgRecycler
        mRecyclerView_img = (RecyclerView) findViewById(R.id.rv_compile_img);
        mRecyclerView_img.setItemAnimator(new DefaultItemAnimator());//动画
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
//        layoutManager2.setStackFromEnd(true);
        mRecyclerView_img.setLayoutManager(layoutManager2);
        mImgs.add("");
        mImgAdapter = new RecyclerImgAdapter(mImgs, this);
        mRecyclerView_img.setAdapter(mImgAdapter);
        mImgAdapter.setOnItemSelectClick(this);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.bt_commodity_add:
//                    mCommodityAdapter.addData(mCommodities.size(), new ProgramBean.Commodity("", ""));
//                    Log.d(TAG,"size:"+mCommodityAdapter.getData().size());
//                    mRecyclerView_com.scrollToPosition(mCommodityAdapter.getData().size());
//                    mCommodity_add.setEnabled(false);
//                    break;
//                case R.id.bt_img_add:
//                    mImgAdapter.addData(mImgs.size(), "");
//                    mRecyclerView_img.scrollToPosition(mImgAdapter.getData().size());
//                    mImg_add.setEnabled(false);
//                    break;
                case R.id.title_back:
                    finish();
                    break;
                case R.id.title_right://节目推送
                    SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
                    String userID = sp.getString(UserConstant.USER_ID, "");
                    if (TextUtils.isEmpty(userID)) {//账号不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getResources().getString(R.string.please_login), 3000);
                        return;
                    }
                    ArrayList<String> imgs = mImgAdapter.getData();
                    if (TextUtils.isEmpty(imgs.get(0))) {//图片不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_img), 3000);
                        return;
                    }
                    ArrayList<ProgramBean.Commodity> data = mCommodityAdapter.getData();
                    if (TextUtils.isEmpty(data.get(0).getName()) || TextUtils.isEmpty(data.get(0).getPrice())) {//名称和价格不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_commodity), 3000);
                        return;
                    }

                    ProgramBean body = new ProgramBean();
                    body.setCommoditys(data);//名称和价格
                    body.setImages(imgs);//图片广告
                    body.setDeviceMacs(mCheckMacs);//需要推送终端的Mac地址
                    body.setModelId(modleId);//模板ID
                    body.setUserID(userID);//账号
                    body.setSign(MD5Util.getMD5(Constant.S_KEY + userID));//加密字符串

                    Intent intent = new Intent(ProgramCompileActivity.this, MainActivity.class);
                    intent.putExtra("body", body);
                    setResult(0x11, intent);//编辑结果返回
                    finish();
                    break;
                case R.id.title_preview://预览
                    ArrayList<String> preview_imgs = mImgAdapter.getData();
                    if (TextUtils.isEmpty(preview_imgs.get(0))) {//图片不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_img), 3000);
                        return;
                    }
                    ArrayList<ProgramBean.Commodity> preview_data = mCommodityAdapter.getData();
                    if (TextUtils.isEmpty(preview_data.get(0).getName()) || TextUtils.isEmpty(preview_data.get(0).getPrice())) {//名称和价格不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_commodity), 3000);
                        return;
                    }

                    ProgramBean preview_body = new ProgramBean();
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
    @Override
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
        mImgAdapter.notifyDataSetChanged();
        LogUtil.d(TAG, "path:" + strPath);
        mImg_add.setEnabled(true);
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
