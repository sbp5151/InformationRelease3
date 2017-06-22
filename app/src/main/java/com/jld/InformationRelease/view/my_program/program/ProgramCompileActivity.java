package com.jld.InformationRelease.view.my_program.program;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.presenter.BitmapUtilPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileAdapter;
import com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileImgItemAdapter;
import com.jld.InformationRelease.view.my_program.program.preview.PreviewActivity_1;

import java.io.File;
import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileAdapter.ITEM_TAG_IMG;
import static com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileAdapter.ITEM_TAG_IMG_HEAD;
import static com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileAdapter.ITEM_TAG_TEXT;
import static com.jld.InformationRelease.view.my_program.program.adapter.ProgramCompileAdapter.ITEM_TAG_TEXT_HEAD;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * 奶茶价格表 节目编辑
 */
public class ProgramCompileActivity extends BaseProgramCompileActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    //外部存储读取权限申请
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 0x02;
    public static final int REQUEST_PREVIEW_CODE = 0x03;
    //封面选择
    private static final int REQUEST_CODE_COVER = 0x04;

    public static final String MODEL_ID = "1";
    private static final String TAG = "ProgramCompileActivity";
    ArrayList<ProgramBean.Commodity> mCommodities = new ArrayList();
    ArrayList<String> mCheckMac = new ArrayList();
    private ProgramCompileAdapter mAdapter;
    private ProgressDialog mPhotoCompileDialog;
    public String modleId = Constant.NAICHA_MODEL_1;
    private RecyclerView mRecyclerView;
    private View mHead_head;
    private TextView mTv_head;
    private ProgramDao mProgramDao;
    private PopupWindow mPopupWindow;
    private ImageButton mIb_tool;
    SharedPreferences sp;
    boolean mIsAgainCompile = false;//再次编辑进入
    private ArrayList<String> mPhotos;
    private ArrayList<String> mLastImg = new ArrayList<>();
    private ArrayList<String> mLastText = new ArrayList<>();
    private String mLastCover = "";
    //节目标识
    private String mProgram_tab = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.activity_program_compile);
        //获取终端设备数据
//        mTerminals = getIntent().getParcelableArrayListExtra("terminal_data");
        //再编辑 节目数据
        mProgramBean = (ProgramBean) getIntent().getSerializableExtra("program_data");
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);

        if (mProgramBean == null) {
            mProgramBean = new ProgramBean();
            mProgramBean.setModelId(MODEL_ID);
            setTabDialog();
        } else {
            mIsAgainCompile = true;
            modleId = mProgramBean.getModelId();
            mCheckMac = mProgramBean.getDeviceMacs();
            //保存原始的img、text、cover用于是否保存判断
            mLastImg.addAll(mProgramBean.getImages());
            //tostring 保证last数据不是引用变量，不被更改
            for (ProgramBean.Commodity com : mProgramBean.getTexts())
                mLastText.add(com.toString());
            mLastCover = mProgramBean.getCover();
        }
        sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        initView();
    }

    private void initView() {
        /***********************title***********************/
        View title = findViewById(R.id.program_compile_titlebar);
        mIb_tool = (ImageButton) title.findViewById(R.id.titlebar_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(mOnClickListener);
        TextView title_right = (TextView) title.findViewById(R.id.title_right);
        title_right.setVisibility(View.GONE);
        LinearLayout back = (LinearLayout) title.findViewById(R.id.title_back);
        back.setOnClickListener(mOnClickListener);
        TextView conter = (TextView) findViewById(R.id.title_center);
        conter.setText(getString(R.string.program_compile));
        /***********************head***********************/
        //主head
        mHead_head = findViewById(R.id.program_compile_head);
        ImageButton add_item = (ImageButton) mHead_head.findViewById(R.id.bt_compile_head_add);
        add_item.setOnClickListener(mOnClickListener);
        mTv_head = (TextView) mHead_head.findViewById(R.id.tv_compile_head_text);
        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_program_compile);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//动画
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        //initData
        if (!mIsAgainCompile) {//新建节目初始化数据
            mCommodities.add(new ProgramBean.Commodity("", ""));
            mProgramBean.setTexts(mCommodities);
        }
        mAdapter = new ProgramCompileAdapter(mProgramBean, this, !mIsAgainCompile);
        mRecyclerView.setAdapter(mAdapter);
        //滑动监听
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        //点击监听
        mAdapter.setMyItemSelectClick(mMyItemClick);

        mAdapter.mImgAdapter.setItemClickListen(new ProgramCompileImgItemAdapter.MyItemClickListen() {

            @Override
            public void onItemClickListen(View view, int position) {
                //图片预览
                PhotoPreviewIntent intent = new PhotoPreviewIntent(ProgramCompileActivity.this);
                intent.setCurrentItem(position); // 当前选中照片的下标
                intent.setPhotoPaths(mAdapter.getImgDatas(true)); // 已选中的照片地址
                startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            }

            @Override
            public void onItemLongClickListen(View view, int position) {
            }

            @Override
            public void onItemCoverClickListen() {
                if (ContextCompat.checkSelfPermission(ProgramCompileActivity.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//没有获得权限
                    ActivityCompat.requestPermissions(ProgramCompileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                } else {//已获取权限了
                    getCover();
                }
            }
        });
    }


    ProgramCompileAdapter.MyItemClick mMyItemClick = new ProgramCompileAdapter.MyItemClick() {

        @Override
        public void onTextHeadClickListen(View view, int position) {
            LogUtil.d(TAG, "onTextHeadClickListen:" + position);
            mAdapter.addTextItem();
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    };

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<String> imgs;
            String userID;
            ArrayList<ProgramBean.Commodity> data;
            switch (view.getId()) {
                case R.id.bt_compile_head_add://head
                    int tag = (int) mHead_head.getTag();
                    if (tag == ITEM_TAG_IMG_HEAD) {//添加图片
                        //Android6.0运行时权限申请
                        if (ContextCompat.checkSelfPermission(ProgramCompileActivity.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//没有获得权限
                            ActivityCompat.requestPermissions(ProgramCompileActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        } else {//已获取权限了
                            getImg();
                        }
                    } else if (tag == ITEM_TAG_TEXT_HEAD) {//添加文字
                        mAdapter.addTextItem();
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                    break;
                case R.id.title_back:
                    if (isDataChange())
                        backSaveDialog();
                    else
                        finish();
                    break;
                case R.id.pp_program_push://发布
                    mPopupWindow.dismiss();
                    userID = sp.getString(UserConstant.USER_ID, "");
                    if (TextUtils.isEmpty(userID)) {//账号不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getResources().getString(R.string.please_login), 3000);
                        return;
                    }
                    imgs = mAdapter.getImgDatas(true);
                    if (imgs.size() == 0) {//图片不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_img), 3000);
                        return;
                    }
                    data = mAdapter.getTextDatas(true);
                    if (data.size() == 0) {//名称和价格不能为空
                        ToastUtil.showToast(ProgramCompileActivity.this, getString(R.string.please_set_commodity), 3000);
                        return;
                    }
                    //选择需要推送的终端设备
                    terminalSelect();
                    break;
                case R.id.pp_preview://预览
                    mPopupWindow.dismiss();
                    programPreview();
                    break;
                case R.id.pp_save://保存
                    mPopupWindow.dismiss();
                    saveProgram();
                    break;
                case R.id.titlebar_tool://tool
                    showPopupwindow();
                    mIb_tool.setEnabled(false);
                    mIb_tool.setClickable(false);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意
                    getImg();
                } else {//拒绝
                }
                break;
        }
    }

    /**
     * 数据是否变化
     */
    private boolean isDataChange() {
        mAdapter.getImgDatas(true).add(0, mAdapter.getCover());
        LogUtil.d(TAG, "mLastText:" + mLastText);
        LogUtil.d(TAG, "mLastImg:" + mLastImg);
        ArrayList<String> compareText = new ArrayList<>();
        for (ProgramBean.Commodity com : mAdapter.getTextDatas(true))
            compareText.add(com.toString());

        if (mAdapter.getImgDatas(true).size() == mLastImg.size()) {
            if (mAdapter.getImgDatas(true).containsAll(mLastImg)) {//img
                if (mAdapter.getCover().equals(mLastCover)) {//cover
                    if (compareText.size() == mLastText.size()) {//text
                        if (compareText.containsAll(mLastText))
                            return false;
                        else
                            return true;
                    } else return true;
                } else
                    return true;

            } else
                return true;
        } else
            return true;
    }

    /**
     * 节目预览
     */
    public void programPreview() {
        ArrayList<String> imgs = mAdapter.getImgDatas(true);
        ArrayList<ProgramBean.Commodity> data = mAdapter.getTextDatas(true);
        ProgramBean body = new ProgramBean();
        body.setTexts(data);//名称和价格
        body.setImages(imgs);//图片广告
        body.setModelId(modleId);//模板ID
        body.setCover(mAdapter.mImgAdapter.getCover());
        LogUtil.d(TAG, "pp_preview:" + body);
        if (body.getModelId().equals("001"))
            toActivity(PreviewActivity_1.class, body, "previewData");
    }

    /**
     * 退出是否保存节目提示
     */
    public void backSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.hint))
                .setMessage(getResources().getString(R.string.whether_save_program))
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveProgram();
                    }
                }).setNegativeButton(getResources().getString(R.string.no_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }


    /**
     * 推送、预览、保存
     */
    public void showPopupwindow() {
        mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.program_popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_preview).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_save).setOnClickListener(mOnClickListener);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
        mPopupWindow.showAsDropDown(mIb_tool, GeneralUtil.dip2px(this, 21), GeneralUtil.dip2px(this, -21));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mIb_tool.setClickable(true);
                mIb_tool.setEnabled(true);
            }
        });
    }

    /**
     * 保存节目到数据库
     */
    public void saveProgram() {
        sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        String userID = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(ProgramCompileActivity.this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
//        ArrayList<String> imgs = mAdapter.getImgDatas(true);
//        ArrayList<ProgramBean.Commodity> data = mAdapter.getTextDatas(true);
//        ProgramBean body = new ProgramBean();
        mProgramBean.setCreation_time(GeneralUtil.getTimeStr());
        mProgramBean.setCover(mAdapter.mImgAdapter.getCover());
        mProgramBean.getImages().remove(0);
//        body.setLift_texts(data);//名称和价格
//        body.setImages(imgs);//图片广告
        mProgramBean.setModelId(modleId);//模板ID
        mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
        mProgramBean.setUserid(userID);//账号
//        body.setTable_id(mProgramBean.getTable_id());
        mProgramBean.setState("0");
        try {
            mProgramDao = ProgramDao.getInstance(ProgramCompileActivity.this);
            if (mIsAgainCompile)
                mProgramDao.updateInDataBaseId(mProgramBean, userID);
            else
                mProgramDao.addProgram(mProgramBean, userID);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 监听返回键保存数据
         */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDataChange()) {
                backSaveDialog();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 节目发布
     */
    public void programPush() {
        if (mCheckMac.size() <= 0) {
            ToastUtil.showToast(this, getResources().getString(R.string.terminal_id_no_null), Toast.LENGTH_SHORT);
            return;
        }
        String userID = sp.getString(UserConstant.USER_ID, "");
//        ArrayList imgs = mAdapter.getImgDatas(true);
//        ArrayList data = mAdapter.getTextDatas(true);
//        ProgramBean body = new ProgramBean();
        mProgramBean.setCover(mAdapter.mImgAdapter.getCover());
        mProgramBean.setCreation_time(GeneralUtil.getTimeStr());
//        body.setLift_texts(data);//名称和价格
//        body.setImages(imgs);//图片广告
//        LogUtil.d(TAG, "mCheckMac:" + mCheckMac);
        mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
        mProgramBean.setModelId(modleId);//模板ID
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setSign(MD5Util.getMD5(Constant.S_KEY + userID));//加密字符串
        mProgramBean.getImages().remove(0);
        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        LogUtil.d(TAG, "programPush:" + mProgramBean);
        setResult(mProgramResultCode, intent);//编辑结果返回
        finish();
    }

    ArrayList<TerminalBeanSimple> mTerminals = null;

    /**
     * 终端选择
     */
    private void terminalSelect() {
        String terminalJson = sp.getString(Constant.MY_TERMINAL, "");
        LogUtil.d(TAG, "terminalJson:" + terminalJson);
        if (!TextUtils.isEmpty(terminalJson)) {
            mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<TerminalBeanSimple>>() {
            }.getType());
        }
        LogUtil.d(TAG, "mTerminals:" + mTerminals);

        if (mTerminals == null || mTerminals.size() <= 0) {
            ToastUtil.showToast(this, getString(R.string.terminal_null), 3000);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_device_dialog_title));
        /**
         * 设置内容区域为多选列表项
         */
        //默认选择都为false
        boolean[] terminalBoolean = new boolean[mTerminals.size()];
        for (int i = 0; i < terminalBoolean.length; i++) {
            terminalBoolean[i] = false;
        }
        ArrayList<String> select_item = new ArrayList<>();
        for (int i = 0; i < mTerminals.size(); i++) {
            if (mTerminals.get(i).getState().equals("1")) {//在线终端供选择
                select_item.add("ID: " + mTerminals.get(i).getId() + "  " + mTerminals.get(i).getName());
                if (mIsAgainCompile) {//再编辑  默认选中以前选中过的设备
                    for (String str : mCheckMac) {
                        if (mTerminals.get(i).getMac().equals(str)) {
                            terminalBoolean[i] = true;
                        }
                    }
                }
            }
        }
        LogUtil.d(TAG, "select_item:" + select_item);
        final String[] select_item_arry = select_item.toArray(new String[0]);
        LogUtil.d(TAG, "select_item_arry:" + select_item_arry);

        builder.setMultiChoiceItems(select_item_arry, terminalBoolean, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                LogUtil.d(TAG, "mPushId1:" + mCheckMac);
                if (b)
                    mCheckMac.add(mTerminals.get(i).getMac());
                else {
                    boolean remove = mCheckMac.remove(mTerminals.get(i).getMac());
                    LogUtil.d(TAG, "remove:" + remove);
                }
                LogUtil.d(TAG, "mPushId2:" + mCheckMac);
            }
        });
        //监听下方button点击事件
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                programPush();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCheckMac.clear();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * 选择相片
     */
    public void getImg() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(true); // 是否显示拍照， 默认false
        intent.setMaxTotal(9); // 最多选择照片数量，默认为9
//      intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
//      intent.setImageConfig(config);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    /**
     * 获取封面
     */
    public void getCover() {
        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(true); // 是否显示拍照， 默认false
        intent.setMaxTotal(1); // 最多选择照片数量，默认为9
        startActivityForResult(intent, REQUEST_CODE_COVER);
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
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {//轮播图返回
            isCompressCover = false;
            if (resultCode == RESULT_OK && data != null) {
                mPhotos = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                LogUtil.d(TAG, "photos:" + mPhotos);
                if (mPhotos != null && mPhotos.size() > 0) {
                    //获取图片集合并处理
                    mHandler.sendEmptyMessage(PHOTO_COMPRESS);
                    mPhotoCompileDialog = new ProgressDialog(this);
                    mPhotoCompileDialog.setCancelable(false);
                    mPhotoCompileDialog.setCanceledOnTouchOutside(false);
                    mPhotoCompileDialog.setMessage(getResources().getString(R.string.loading));
                    if (mPhotoCompileDialog != null && !mPhotoCompileDialog.isShowing())
                        mPhotoCompileDialog.show();
                }
            }
        } else if (requestCode == REQUEST_CODE_COVER) {//封面返回
            if (resultCode == RESULT_OK && data != null) {
                mPhotos = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                String coverPath = mPhotos.get(0);
                isCompressCover = true;
                LogUtil.d(TAG, "photos:" + mPhotos);
                //获取图片集合并处理
                mPhotoCompileDialog = new ProgressDialog(this);
                mPhotoCompileDialog.setCancelable(false);
                mPhotoCompileDialog.setCanceledOnTouchOutside(false);
                mPhotoCompileDialog.setMessage(getResources().getString(R.string.loading));
                if (mPhotoCompileDialog != null && !mPhotoCompileDialog.isShowing())
                    mPhotoCompileDialog.show();
                photoCompress(coverPath);
            }
        }
        if (requestCode == REQUEST_PREVIEW_CODE) {//预览处理后的图片
            ArrayList<String> mPhotos = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
            mAdapter.refreshImgs(mPhotos);
        }
    }

    /**
     * 是否压缩封面
     */
    boolean isCompressCover = false;

    public static final int PHOTO_COMPRESS = 0x11;
    private int current_photo = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PHOTO_COMPRESS://图片处理
                    if (current_photo < mPhotos.size()) {
                        photoCompress(mPhotos.get(current_photo));
                        current_photo++;
                    } else {//图片处理完成
                        current_photo = 0;
                        if (mPhotoCompileDialog != null && mPhotoCompileDialog.isShowing())
                            mPhotoCompileDialog.dismiss();
                    }
                    break;
            }
        }
    };

    /**
     * 图片压缩
     *
     * @param path
     */
    private void photoCompress(final String path) {
        LogUtil.d(TAG, "path:" + path);
        if (!TextUtils.isEmpty(path)) {
            //所获取图片的bitmap
            Bitmap photoBmp = BitmapFactory.decodeFile(path);
            if (photoBmp != null) {
                String[] split = path.split(File.separator);
                String imgName = split[split.length - 1];
                //创建miniphone文件夹
                String miniPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miniPhoto";
                File file = new File(miniPath);
                if (!file.exists())
                    file.mkdirs();

                int lastIndexOf = path.lastIndexOf(".");
                LogUtil.d(TAG, "lastIndexOf:" + lastIndexOf);

                String miniImgPath;
                if (imgName.contains("jldmini"))
                    miniImgPath = miniPath + File.separator + imgName;
                else
                    miniImgPath = miniPath + File.separator + "jldmini" + imgName;

                if (!new File(miniImgPath).exists()) {//没有压缩过进行压缩
                    LogUtil.d(TAG, "压缩:");
                    //图片压缩
                    BitmapUtilPresenter presenter = new BitmapUtilPresenter(mBitmapCompressListen);
                    //图片压缩RxJava
                    presenter.bitmapCompress(photoBmp, miniImgPath);
                } else {
                    LogUtil.d(TAG, "不压缩:" + isCompressCover);
                    //添加图片并更新
                    if (isCompressCover) {//封面
                        mAdapter.mImgAdapter.addCover(miniImgPath);
                        isCompressCover = false;
                        if (mPhotoCompileDialog != null && mPhotoCompileDialog.isShowing())
                            mPhotoCompileDialog.dismiss();
                    } else {
                        mAdapter.addImgItem(miniImgPath);
                        mHandler.sendEmptyMessage(PHOTO_COMPRESS);
                    }
                }
            }
        }
    }

    //图片压缩返回监听
    BitmapUtilPresenter.BitmapCompressListen mBitmapCompressListen = new BitmapUtilPresenter.BitmapCompressListen() {

        @Override
        public void compressSucceed(String compressPath) {//成功并返回结果
            if (isCompressCover) {
                mAdapter.mImgAdapter.addCover(compressPath);
                isCompressCover = false;
                if (mPhotoCompileDialog != null && mPhotoCompileDialog.isShowing())
                    mPhotoCompileDialog.dismiss();
            } else {
                //添加图片并更新
                mAdapter.addImgItem(compressPath);
                mHandler.sendEmptyMessage(PHOTO_COMPRESS);
            }
        }

        @Override
        public void compressError() {//失败
            if (isCompressCover) {
                isCompressCover = false;
                if (mPhotoCompileDialog != null && mPhotoCompileDialog.isShowing())
                    mPhotoCompileDialog.dismiss();

            } else
                mHandler.sendEmptyMessage(PHOTO_COMPRESS);
        }

        @Override
        public void onStart() {//开始
        }
    };
    /**
     * 悬浮head
     */
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
            //head
            if (headView_tag == ITEM_TAG_IMG_HEAD || headView_tag == ITEM_TAG_TEXT_HEAD) {
                //设置tag 点击区分
                mHead_head.setTag(headView_tag);
                TextView tv_view = (TextView) headView.findViewById(R.id.tv_compile_head_text);
                mTv_head.setText(tv_view.getText().toString());
            } else {//非head  各自显示自己的head
                if (headView_tag == ITEM_TAG_IMG) {
                    mHead_head.setTag(ITEM_TAG_IMG_HEAD);
                    mTv_head.setText(getResources().getString(R.string.add_imgs));
                } else if (headView_tag == ITEM_TAG_TEXT) {
                    mHead_head.setTag(ITEM_TAG_TEXT_HEAD);
                    mTv_head.setText(getResources().getString(R.string.add_commoditys));
                }
            }
            if (transInfoView_tag == ITEM_TAG_TEXT_HEAD) {
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

}
