package com.jld.InformationRelease.view.my_program.program_create;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.BitmapCompress;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.program_create.adapter.ProgramImageAdapter;
import com.jld.InformationRelease.view.my_program.program_create.preview.ProgramImagePreview;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.jld.InformationRelease.view.my_terminal.MyDeviceFragment.mProgramResultCode;

@RuntimePermissions
public class ProgramImageActivity extends BaseProgramCompileActivity {
    private static final String TAG = "ProgramImageActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<String> mImagePath = new ArrayList<>();
    private ArrayList<String> mLastPath = new ArrayList<>();
    private ProgramImageAdapter mAdapter;
    private ImageView mAdd_image;
    //获取相片
    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    private static final int REQUEST_PREVIEW_CODE = 0x02;
    public static final int ADD_IMAGE = 0x10;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_IMAGE:
                    ProgramImageActivityPermissionsDispatcher.getImgWithCheck(ProgramImageActivity.this);
                    break;
            }
        }
    };
    private ProgressDialog mPhotoCompileDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_image);
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
        if (mIsAgainCompile) {
            mImagePath = mProgramBean.getImages();//提取视频路径
            mLastPath.addAll(mImagePath);
        }
        setPopupWindowListener(mPopupWindowListener);
        initView();
    }

    public void initView() {
        //title
        View title = findViewById(R.id.program_image_title);
        mIb_tool = (ImageButton) title.findViewById(R.id.title_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(mOnClickListener);
        TextView title_right = (TextView) title.findViewById(R.id.title_right);
        title_right.setVisibility(View.GONE);
        LinearLayout back = (LinearLayout) title.findViewById(R.id.title_back);
        back.setOnClickListener(mOnClickListener);
        TextView conter = (TextView) findViewById(R.id.title_center);
        conter.setText(getString(R.string.program_compile));
        //RelativeLayout
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_program_image);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new ProgramImageAdapter(this, mImagePath);
        mAdapter.setItemClickListen(new ProgramImageAdapter.MyItemClickListen() {
            @Override
            public void onItemClickListen(View view, int position) {
                //图片预览
                PhotoPreviewIntent intent = new PhotoPreviewIntent(ProgramImageActivity.this);
                intent.setCurrentItem(position); // 当前选中照片的下标
                intent.setPhotoPaths(mAdapter.getImgDatas()); // 已选中的照片地址
                startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //addImage
        mAdd_image = (ImageView) findViewById(R.id.iv_add_image);
        mAdd_image.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_add_image:
                    AnimationUtil.togetherRun(mAdd_image, 270);
                    if (mAdapter.getItemCount() < 20) {
                        mHandler.sendEmptyMessageDelayed(ADD_IMAGE, 200);
                    } else
                        ToastUtil.showToast(ProgramImageActivity.this, getString(R.string.add_image_max), 3000);
                    break;
                case R.id.title_back:
                    if (isDataChange()) {
                        backSaveDialog();
                    } else
                        finish();
                    break;
                case R.id.title_tool:
                    showPopupwindow();
                    break;
            }
        }
    };

    /**
     * 选择相片
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void getImg() {
        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(true); // 是否显示拍照， 默认false
        int max = (20 - mAdapter.getItemCount()) > 9 ? 9 : (20 - mAdapter.getItemCount());
        intent.setMaxTotal(max); // 最多选择照片数量，默认为9
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> mPhotos = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    LogUtil.d(TAG, "photos:" + mPhotos);
                    if (mPhotos != null && mPhotos.size() > 0) {
                        //获取图片集合并处理
                        BitmapCompress.getInstance().compressBitmaps(mPhotos, mCompressListener);
                        mPhotoCompileDialog = new ProgressDialog(this);
                        mPhotoCompileDialog.setCancelable(false);
                        mPhotoCompileDialog.setCanceledOnTouchOutside(false);
                        mPhotoCompileDialog.setMessage(getResources().getString(R.string.loading));
                        if (mPhotoCompileDialog != null && !mPhotoCompileDialog.isShowing())
                            mPhotoCompileDialog.show();
                    }
                }
                break;
            case REQUEST_PREVIEW_CODE:
                ArrayList<String> mPhotos = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                mAdapter.refreshDatas(mPhotos);
                break;


        }
    }

    BitmapCompress.CompressListener mCompressListener = new BitmapCompress.CompressListener() {
        @Override
        public void compressSucceed(String imgPath) {
            mAdapter.addData(imgPath);
        }

        @Override
        public void compressComplete() {
            if (mPhotoCompileDialog != null && mPhotoCompileDialog.isShowing())
                mPhotoCompileDialog.dismiss();
        }

        @Override
        public void compressDefeat(String imgPath) {
            mAdapter.addData(imgPath);
        }
    };



    @Override
    public void saveProgram() {
        String userID = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        mProgramBean.setTime(TimeUtil.getTodayDateTime());
        mProgramBean.setModelId(Constant.IMAGE_MODEL);//模板ID
        mProgramBean.setImages(mAdapter.getImgDatas());//需要推送终端的Mac地址
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
        try {
            ProgramDao mProgramDao = ProgramDao.getInstance(this);
            if (mIsAgainCompile) {
                mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
                mProgramDao.updateInDataBaseId(mProgramBean, userID);
            } else
                mProgramDao.addProgram(mProgramBean, userID);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void programPush() {
        SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        String userID = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        if (mCheckMac.size() <= 0) {
            ToastUtil.showToast(this, getResources().getString(R.string.terminal_id_no_null), Toast.LENGTH_SHORT);
            return;
        }
        mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
        mProgramBean.setModelId(Constant.IMAGE_MODEL);//模板ID
        mProgramBean.setImages(mAdapter.getImgDatas());
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        LogUtil.d(TAG, "programPush:" + mProgramBean);
        setResult(mProgramResultCode, intent);//编辑结果返回
        finish();
    }

    @Override
    public boolean isDataChange() {
        if (mLastPath.size() == mImagePath.size()) {
            for (int i = 0; i < mLastPath.size(); i++) {
                if (!mLastPath.get(i).equals(mImagePath.get(i)))
                    return true;
            }
            return false;
        } else
            return true;
    }

    PopupWindowListener mPopupWindowListener = new PopupWindowListener() {
        @Override
        public void onPreview() {
            if (mAdapter.getImgDatas() == null || mAdapter.getImgDatas().size() == 0) {
                ToastUtil.showToast(ProgramImageActivity.this, getString(R.string.data_is_null), 3000);
                return;
            }
            Intent intent = new Intent(ProgramImageActivity.this, ProgramImagePreview.class);
            intent.putStringArrayListExtra("imagePath", mAdapter.getImgDatas());
            startActivity(intent);
        }

        @Override
        public void onProgramPush() {
            if (mAdapter.getImgDatas().size() > 0)
                terminalSelect();
            else
                ToastUtil.showToast(ProgramImageActivity.this, getResources().getString(R.string.please_compile), 3000);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ProgramImageActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
