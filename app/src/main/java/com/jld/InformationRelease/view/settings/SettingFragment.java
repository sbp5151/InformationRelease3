package com.jld.InformationRelease.view.settings;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.request_bean.ChangeNickRequest;
import com.jld.InformationRelease.bean.request_bean.ChangePWRequestBean;
import com.jld.InformationRelease.dialog.AlertTextDialog;
import com.jld.InformationRelease.dialog.ChangeNickDialog;
import com.jld.InformationRelease.dialog.ChangePasswordDialog;
import com.jld.InformationRelease.dialog.MyProgressDialog;
import com.jld.InformationRelease.dialog.PortraitDialog;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.UserPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LanguageUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.SDCardUtils;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.web_view.WebViewActivity;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.jld.InformationRelease.util.UserConstant.USER_PHONE;

/**
 * A simple {@link Fragment} subclass.
 */
@RuntimePermissions
public class SettingFragment extends Fragment implements View.OnClickListener, IViewListen {

    public static final String TAG = "SettingFragment";
    private Activity mActivity;
    //相册获取图片
    private static final int REQUEST_CODE_PICK_IMAGE = 0x01;
    //相机获取图片
    private static final int REQUEST_CODE_PICK_IMAGE2 = 0x012;
    //头像图片裁剪
    private static final int REQUEST_CODE_CUTTING_HEAD = 0x02;
    //相机
    private static final int REQUEST_CODE_CUTTING_HEAD2 = 0x022;
    //背景图片裁剪
    private static final int REQUEST_CODE_CUTTING_BACK = 0x03;
    //相机
    private static final int REQUEST_CODE_CUTTING_BACK2 = 0x032;
    //修改昵称
    private static final int CHANGE_NICK_REQUEST = 0x10;
    //修改密码
    private static final int CHANGE_PW_REQUEST = 0x11;
    //拍照获取图片
    private static final int SELECT_PIC_BY_TACK_PHOTO = 0x12;

    private SharedPreferences mSp;
    private TextView mNike_hint;
    private TextView mHead_nike;
    private ImageView mHead_icon;
    private ImageView mHead_background;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Uri mImageUri;
    private String mChangeNick;
    private String mChangePassword;
    private ChangePasswordDialog mChangePasswordDialog;
    private File mTempFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mSp = mActivity.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView:");
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.ll_set_nik_change).setOnClickListener(this);
        view.findViewById(R.id.ll_set_password_change).setOnClickListener(this);
        view.findViewById(R.id.ll_set_about_information).setOnClickListener(this);
        view.findViewById(R.id.ll_set_cache_clear).setOnClickListener(this);
        view.findViewById(R.id.ll_set_check_update).setOnClickListener(this);
        view.findViewById(R.id.ll_set_feed_back).setOnClickListener(this);
        view.findViewById(R.id.bt_set_logout).setOnClickListener(this);
        view.findViewById(R.id.tv_set_protocol).setOnClickListener(this);
        view.findViewById(R.id.iv_set_back).setOnClickListener(this);

        //昵称提示
        mNike_hint = (TextView) view.findViewById(R.id.tv_set_nik_change);
        mHead_nike = (TextView) view.findViewById(R.id.tv_set_head_nike);
        mHead_icon = (ImageView) view.findViewById(R.id.image_set_head_icon);
        mHead_background = (ImageView) view.findViewById(R.id.set_top_img_bg);
        mHead_background.setOnClickListener(this);
        mHead_icon.setOnClickListener(this);
    }

    private void initData() {
        String nike = mSp.getString(UserConstant.USER_NICK, "");
        String iconPath = mSp.getString(UserConstant.USER_ICON, "");
        String background = mSp.getString(UserConstant.SET_BACKGROUND, "");
        LogUtil.d(TAG, "nike:" + nike);
        LogUtil.d(TAG, "iconPath:" + iconPath);
        LogUtil.d(TAG, "background:" + background);
        mNike_hint.setText(nike);
        mHead_nike.setText(nike);
        Glide.with(mActivity).load(iconPath).crossFade()
                .error(mActivity.getResources().getDrawable(R.mipmap.meizi))
                .into(mHead_icon);
        Glide.with(mActivity).load(background).crossFade()
                .error(mActivity.getResources().getDrawable(R.mipmap.head_back))
                .into(mHead_background);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_set_nik_change://用户名修改
                String nike = mSp.getString(UserConstant.USER_NICK, "");
                ChangeNickDialog changeNickDialog = new ChangeNickDialog(mActivity, mSetChangeNickListen, nike);
                changeNickDialog.show(getFragmentManager(), "change_nick");
                break;
            case R.id.ll_set_password_change://密码修改
                mChangePasswordDialog = new ChangePasswordDialog(mActivity, mSetChangePasswordListen);
                mChangePasswordDialog.show(getFragmentManager(), "change_nick");
                break;
            case R.id.ll_set_about_information://关于
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra(WebViewActivity.LOAD_URL, Constant.ABOUT_INFORMATION);
                intent.putExtra(WebViewActivity.WEB_TITLE, getString(R.string.app_name));
                startActivity(intent);
                break;
            case R.id.ll_set_cache_clear://清除缓存
                AlertTextDialog clearCacheDialog = new AlertTextDialog(mActivity, mActivity.getString(R.string.can_clear_cache), new AlertTextDialog.OnAlertTextListen() {
                    @Override
                    public void onConfirm() {
                        final MyProgressDialog cache_dialog = new MyProgressDialog(mActivity, getString(R.string.cache_clear_ing));
                        cache_dialog.show(getFragmentManager(), "cache_dialog");
//                        File file = new File(Constant.IMAGE_CHACE);
//                        deleteDirWihtFile(file);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cache_dialog.dismiss();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(mActivity, getString(R.string.cache_clear_succeed), 3000);
                                    }
                                });
                            }
                        }, 1000);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                clearCacheDialog.show(getFragmentManager(), "clear_cache_dialog");
                break;
            case R.id.ll_set_check_update://检查更新
                ToastUtil.showToast(mActivity, getString(R.string.version_newest), 3000);
                break;
            case R.id.ll_set_feed_back://意见反馈
                Intent feedBackIntent = new Intent(mActivity, FeedBackActivity.class);
                startActivity(feedBackIntent);
                break;
            case R.id.bt_set_logout://退出登录
                AlertTextDialog logoutDialog = new AlertTextDialog(mActivity, getString(R.string.sure_logout), new AlertTextDialog.OnAlertTextListen() {
                    @Override
                    public void onConfirm() {
                        Intent loginIntent = new Intent(mActivity, LoginActivity.class);
                        startActivity(loginIntent);
                        mSp.edit().putBoolean(UserConstant.IS_LOGIN, false).apply();
                        mActivity.finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                logoutDialog.show(getFragmentManager(), "logout_dialog");
                break;
            case R.id.tv_set_protocol://用户协议
                Intent use_intent = new Intent(mActivity, WebViewActivity.class);
                if (LanguageUtil.isZh(mActivity)) {
                    use_intent.putExtra(WebViewActivity.LOAD_URL, Constant.USER_AGREEMENT_CN);
                } else {
                    use_intent.putExtra(WebViewActivity.LOAD_URL, Constant.USER_AGREEMENT_EN);
                }
                use_intent.putExtra(WebViewActivity.WEB_TITLE, "晶凌达信息发布系统");
                startActivity(use_intent);
                break;
            case R.id.set_top_img_bg://背景图片更换
                PortraitDialog portraitDialog = new PortraitDialog(new PortraitDialog.OnPortraitListen() {
                    @Override
                    public void capturePicture() {
                        SettingFragmentPermissionsDispatcher.takePhotoWithCheck(SettingFragment.this);
                        crop_request_tag = REQUEST_CODE_CUTTING_BACK;
                    }

                    @Override
                    public void phonePicture() {
                        SettingFragmentPermissionsDispatcher.pickPhotoWithCheck(SettingFragment.this);
                        crop_request_tag = REQUEST_CODE_CUTTING_BACK;
                    }
                });
                portraitDialog.show(getFragmentManager(), "dialog");
                break;
            case R.id.image_set_head_icon://头像图片更换
                PortraitDialog portraitDialog2 = new PortraitDialog(new PortraitDialog.OnPortraitListen() {
                    @Override
                    public void capturePicture() {
                        SettingFragmentPermissionsDispatcher.takePhotoWithCheck(SettingFragment.this);
                        crop_request_tag = REQUEST_CODE_CUTTING_HEAD;
                    }

                    @Override
                    public void phonePicture() {
                        SettingFragmentPermissionsDispatcher.pickPhotoWithCheck(SettingFragment.this);
                        crop_request_tag = REQUEST_CODE_CUTTING_HEAD;
                    }
                });
                portraitDialog2.show(getFragmentManager(), "dialog");
                break;
            case R.id.iv_set_back:
                break;
        }
    }

    public static void deleteDirWihtFile(File dir) {
        LogUtil.d(TAG, "dir1:" + dir.getAbsolutePath());
        LogUtil.d(TAG, "dir1:" + dir.exists());
        LogUtil.d(TAG, "dir1:" + dir.isDirectory());
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        LogUtil.d(TAG, "dir2:" + dir.isDirectory());
        for (File file : dir.listFiles()) {
            LogUtil.d(TAG, "dir3:" + file.getAbsolutePath());
            LogUtil.d(TAG, "dir4:" + file.isFile());
            if (file.isFile()) {
                boolean delete = file.delete();// 删除所有文件
                LogUtil.d(TAG, "delete:" + delete);
            } else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    private int crop_request_tag;

    /***
     * 从相册中取图片
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void pickPhoto() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PICK_IMAGE);
    }
    /**
     * 相机获取图片
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void takePhoto() {
        if (SDCardUtils.isSDCardEnable()) {
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Constant.IMAGE_CHACE);
            if (!file.exists())
                file.mkdirs();
            mTempFile = new File(Constant.IMAGE_CHACE + File.separator +
                    System.currentTimeMillis() + "photo.jpg");
            mImageUri = Uri.fromFile(mTempFile);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(getImageByCamera, REQUEST_CODE_PICK_IMAGE2);
        } else {
            ToastUtil.showToast(mActivity, R.string.t_set_photo_no_sdcard, 3000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult data:" + data);
        LogUtil.d(TAG, "onActivityResult resultCode:" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE://相册获取图片
                    if (null != data) {
                        mImageUri = data.getData();
                        LogUtil.d(TAG, "uri:" + mImageUri.getPath());
                        if (crop_request_tag == REQUEST_CODE_CUTTING_HEAD) {
                            cropImageUri(mImageUri, 250, 250, REQUEST_CODE_CUTTING_HEAD);
                        } else if (crop_request_tag == REQUEST_CODE_CUTTING_BACK) {
                            cropImageUri(mImageUri, 1080, GeneralUtil.dip2px(mActivity, 250), REQUEST_CODE_CUTTING_BACK);
                        }
                    }
                    break;
                case REQUEST_CODE_CUTTING_HEAD://头像裁剪
                    LogUtil.d(TAG, "REQUEST_CODE_CUTTING_HEAD:");
                    if (mImageUri != null) {
                        Glide.with(mActivity).load(mImageUri).crossFade()
                                .error(mActivity.getResources().getDrawable(R.mipmap.head_back))
                                .into(mHead_icon);
                        String head_path = GeneralUtil.getRealFilePath(mActivity, mImageUri);
                        LogUtil.d(TAG, "head_path:" + head_path);
                        mSp.edit().putString(UserConstant.USER_ICON, head_path).apply();
                    }
                    break;
                case REQUEST_CODE_CUTTING_BACK://背景裁剪
                    LogUtil.d(TAG, "REQUEST_CODE_CUTTING_BACK:");
                    if (mImageUri != null) {
                        Glide.with(mActivity).load(mImageUri).crossFade()
                                .error(mActivity.getResources().getDrawable(R.mipmap.head_back))
                                .into(mHead_background);
                        String head_path = GeneralUtil.getRealFilePath(mActivity, mImageUri);
                        LogUtil.d(TAG, "head_path:" + head_path);
                        mSp.edit().putString(UserConstant.SET_BACKGROUND, head_path).apply();
                    }
                    break;
                case REQUEST_CODE_PICK_IMAGE2://相机获取图片
                    LogUtil.d(TAG, "mTempFile:" + mTempFile);
                    if (SDCardUtils.isSDCardEnable()) {
                        if (null != mTempFile) {
                            if (crop_request_tag == REQUEST_CODE_CUTTING_HEAD) {
                                cropImageUri(Uri.fromFile(mTempFile), 250, 250, REQUEST_CODE_CUTTING_HEAD);
                            } else if (crop_request_tag == REQUEST_CODE_CUTTING_BACK) {
                                cropImageUri(Uri.fromFile(mTempFile), 1080, GeneralUtil.dip2px(mActivity, 250), REQUEST_CODE_CUTTING_BACK);
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 图片裁剪
     *
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
    /**
     * 昵称修改
     */
    ChangeNickDialog.SetChangeNickListen mSetChangeNickListen = new ChangeNickDialog.SetChangeNickListen() {
        @Override
        public void onConfirm(String name) {
            if (mUserPresenter == null)
                mUserPresenter = new UserPresenter(SettingFragment.this, mActivity);
            String user_id = mSp.getString(UserConstant.USER_ID, "");
            String phone = mSp.getString(USER_PHONE, "");
            mChangeNick = name;
            String sign = MD5Util.getMD5(Constant.S_KEY + phone + user_id);
            ChangeNickRequest body = new ChangeNickRequest(user_id, phone, mChangeNick, sign);
            mUserPresenter.ChangeNike(body, CHANGE_NICK_REQUEST);
        }
    };
    private UserPresenter mUserPresenter;
    /**
     * 密码修改
     */
    ChangePasswordDialog.SetChangePasswordListen mSetChangePasswordListen = new ChangePasswordDialog.SetChangePasswordListen() {
        @Override
        public void onConfirm(String originalPassword, String newPassword) {
            if (mUserPresenter == null)
                mUserPresenter = new UserPresenter(SettingFragment.this, mActivity);
            mChangePassword = newPassword;
            String user_id = mSp.getString(UserConstant.USER_ID, "");
            String phone = mSp.getString(USER_PHONE, "");
            originalPassword = MD5Util.getMD5(Constant.S_KEY + originalPassword);
            newPassword = MD5Util.getMD5(Constant.S_KEY + newPassword);
            String sign = MD5Util.getMD5(Constant.S_KEY + phone + originalPassword + newPassword);
            ChangePWRequestBean body = new ChangePWRequestBean(user_id, phone, originalPassword, newPassword, newPassword, sign);
            mUserPresenter.changePW(body, CHANGE_PW_REQUEST);
            mChangePasswordDialog.dismiss();
        }
    };
    @Override
    public void showProgress(int requestTag) {
    }
    @Override
    public void hideProgress(int requestTag) {
    }
    @Override
    public void loadDataSuccess(Object data, int requestTag) {
        if (requestTag == CHANGE_NICK_REQUEST) {
            mSp.edit().putString(UserConstant.USER_NICK, mChangeNick).apply();
            ToastUtil.showToast(mActivity, getString(R.string.change_success), 3000);
        } else if (requestTag == CHANGE_PW_REQUEST) {
            mSp.edit().putString(UserConstant.USER_PASSWORD, mChangePassword).apply();
            ToastUtil.showToast(mActivity, getString(R.string.change_success), 3000);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {

    }
}
