package com.jld.InformationRelease.view;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.request_bean.BindingRequest;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.TerminalFunctionPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.util.zxing.activity.CaptureActivity;
import com.jld.InformationRelease.view.my_program.MyProgramFragment;
import com.jld.InformationRelease.view.my_terminal.MyTerminalFragment;
import com.jld.InformationRelease.view.settings.SettingFragment;
import com.jld.InformationRelease.view.system_model.SystemModelFragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, IViewListen<BaseResponse> {

    private static final String TAG = "MainActivity";
    public MyTerminalFragment mTerminal_fragment;
    private MyProgramFragment mMyProgramFragment;
    private SystemModelFragment mSystemModelFragment;
    private Fragment mScanCodeFragment;
    private SettingFragment mSettingFragment;
    public DrawerLayout mDrawer;
    public NavigationView mNavigationView;
    private PopupWindow mPopupWindow;
    private TerminalFunctionPresenter mTerminalFunctionPresenter;
    private Dialog mSetNameDialog;
    private BindingRequest mBean;
    public static final int REQUEST_TAG_BIND = 0x11;//绑定tag
    public static final int REQUEST_TAG_PUSH = 0x12;//推送tag
    public static final int REQUEST_TAG_SR = 0x13;//关机&重启tag
    public static final int REQUEST_TAG_TS = 0x14;//定时开关机tag
    public static final int REQUEST_TAG_VOLUME = 0x15;//音量调节tag
    public static final int REQUEST_TAG_IMAGE = 0x16;//获取截屏tag
    private static final int mScanRequestCode = 0x23;//二维码扫描数据返回
    public static final int mScanResultCode = 0x24;//二维码扫描数据返回
    private static final int BIND_REQUEST_TAG = 0x31;
    private static final int REQUEST_CAMERA_PERMISSION = 0x32;//获取相机权限
    private ProgressDialog mBindDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d(TAG, "onCreate");
        initView();
    }

    public void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        //默认值
        mNavigationView.getMenu().getItem(0).setChecked(true);
        switchFragment(R.id.menu_my_terminal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        int id = item.getItemId();
        LogUtil.d(TAG, "onNavigationItemSelected:" + id);
        switchFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static final String MY_TERMINAL_TAG = "my_terminal_tag";
    public static final String MY_MODEL_TAG = "my_model_tag";
    public static final String SYSTEM_MODEL_TAG = "system_model_tag";
    public static final String SCAN_CODE_TAG = "scan_code_tag";
    public static final String SETTING_TAG = "setting_tag";
    HashMap<Integer, Fragment> maps = new HashMap();

    public void switchFragment(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (id == R.id.menu_my_terminal) {//我的终端
            mTerminal_fragment = (MyTerminalFragment) fm.findFragmentByTag(MY_TERMINAL_TAG);
            if (mTerminal_fragment == null) {
                mTerminal_fragment = new MyTerminalFragment();
                maps.put(id, mTerminal_fragment);
                ft.add(R.id.main_fragment, mTerminal_fragment, MY_TERMINAL_TAG);
            } else {
                ft.show(mTerminal_fragment);
            }
//            mTvComplete.setVisibility(View.VISIBLE);
        } else {
//            if (push.getVisibility() == View.VISIBLE)
//                changeCompleteState();
//            mTvComplete.setVisibility(View.GONE);
        }
        if (id == R.id.menu_my_model) {//节目列表
            mMyProgramFragment = (MyProgramFragment) fm.findFragmentByTag(MY_MODEL_TAG);
            if (mMyProgramFragment == null) {
                mMyProgramFragment = new MyProgramFragment();
                maps.put(id, mMyProgramFragment);
                ft.add(R.id.main_fragment, mMyProgramFragment, MY_MODEL_TAG);
            } else {
                ft.show(mMyProgramFragment);
            }
        } else if (id == R.id.menu_system_model) {//系统模板
            mSystemModelFragment = (SystemModelFragment) fm.findFragmentByTag(SYSTEM_MODEL_TAG);
            if (mSystemModelFragment == null) {
                mSystemModelFragment = new SystemModelFragment();
                maps.put(id, mSystemModelFragment);
                ft.add(R.id.main_fragment, mSystemModelFragment, SYSTEM_MODEL_TAG);
            } else {
                ft.show(mSystemModelFragment);
            }
        } else if (id == R.id.menu_scan_code) {//扫描添加
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//没有获得权限
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {//已获取权限了
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, mScanRequestCode);
            }
        } else if (id == R.id.menu_setting) {//设置
            mSettingFragment = (SettingFragment) fm.findFragmentByTag(SETTING_TAG);
            if (mSettingFragment == null) {
                mSettingFragment = new SettingFragment();
                maps.put(id, mSettingFragment);
                ft.add(R.id.main_fragment, mSettingFragment, SETTING_TAG);
            } else {
                ft.show(mSettingFragment);
            }
        }
        Iterator<Map.Entry<Integer, Fragment>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Fragment> entry = iterator.next();
            Integer key = entry.getKey();
            if (id == key)
                continue;
            ft.hide(entry.getValue());
        }
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//获取相机权限
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, mScanRequestCode);
        }
    }

    /**
     * 结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "requestCode:" + requestCode);
        LogUtil.d(TAG, "resultCode:" + resultCode);

        //扫描添加数据返回
        if (mScanResultCode == resultCode && requestCode == mScanRequestCode && data != null) {
            String mac = data.getStringExtra("result");
            boolean isMac = GeneralUtil.isMac(mac);
            if (isMac) {
                showSetNameDialog(mac);
            } else {
                LogUtil.d(TAG, "非法Mac地址");
            }
        }
        if (mScanRequestCode == requestCode) {
            //返回我的终端
            mNavigationView.getMenu().getItem(0).setChecked(true);
            switchFragment(R.id.menu_my_terminal);
        }
    }


    public void showSetNameDialog(final String mac) {
        View view = LayoutInflater.from(this).inflate(R.layout.set_name_dialog, null);
        final EditText setName = (EditText) view.findViewById(R.id.et_set_name);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        ImageView close = (ImageView) view.findViewById(R.id.iv_set_name_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetNameDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (mTerminalFunctionPresenter == null)
                        mTerminalFunctionPresenter = new TerminalFunctionPresenter(MainActivity.this, MainActivity.this);
                    if (mBean == null) {
                        SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
                        String mobile = sp.getString(UserConstant.USER_ID, "");
                        mBean = new BindingRequest();
                        mBean.setUserId(mobile);
                        mBean.setDevicename(name);
                    }
                    mBean.setDevicemac(mac);
                    LogUtil.d(TAG, "userid:" + mBean.getUserId());
                    LogUtil.d(TAG, "mac:" + mac);
                    String sign = MD5Util.getMD5(Constant.S_KEY + mBean.getUserId() + mac);
                    mBean.setSign(sign);
                    mTerminalFunctionPresenter.binding(mBean, BIND_REQUEST_TAG);
                } else
                    ToastUtil.showToast(MainActivity.this, getResources().getString(R.string.input_name), 3000);
            }
        });
        mSetNameDialog = new Dialog(this, R.style.CustomDialog);
//        mSetNameDialog.setCanceledOnTouchOutside();
        mSetNameDialog.setCancelable(false);
        mSetNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        mSetNameDialog.setContentView(view);
        mSetNameDialog.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showProgress(int requestTag) {
        if (requestTag == BIND_REQUEST_TAG) {
            mBindDialog = new ProgressDialog(this);
            mBindDialog.setMessage(getString(R.string.loading));
            mBindDialog.show();
        }
    }

    @Override
    public void hideProgress(int requestTag) {
        if (requestTag == BIND_REQUEST_TAG && mBindDialog.isShowing()) {
            mBindDialog.dismiss();
        }
    }

    @Override
    public void loadDataSuccess(BaseResponse data, int requestTag) {
        LogUtil.d(TAG, "loadDataSuccess:" + requestTag);
        if (requestTag == BIND_REQUEST_TAG) {
            ToastUtil.showToast(this, data.getMsg(), 3000);
            if (mSetNameDialog.isShowing())
                mSetNameDialog.dismiss();
            //刷新界面
            if (mTerminal_fragment != null && mTerminal_fragment.mAdapter != null)
                mTerminal_fragment.initData();
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        if (requestTag == BIND_REQUEST_TAG) {
            ToastUtil.showToast(this, e.getMessage(), 3000);
        }
    }
}
